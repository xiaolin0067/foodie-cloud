package com.zlin.order.controller;

import com.zlin.controller.BaseController;
import com.zlin.enums.CacheKey;
import com.zlin.enums.OrderStatusEnum;
import com.zlin.enums.PayMethod;
import com.zlin.order.pojo.OrderStatus;
import com.zlin.order.pojo.bo.OrderStatusCheckBO;
import com.zlin.order.pojo.bo.PlaceOrderBO;
import com.zlin.order.pojo.bo.SubmitOrderBO;
import com.zlin.order.pojo.vo.MerchantOrdersVO;
import com.zlin.order.pojo.vo.OrderVO;
import com.zlin.order.service.OrderService;
import com.zlin.order.stream.CheckOrderTopic;
import com.zlin.pojo.Result;
import com.zlin.pojo.ShopCartBO;
import com.zlin.utils.CookieUtils;
import com.zlin.utils.JsonUtils;
import com.zlin.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

/**
 * @author zlin
 * @date 20201228
 */
@Api(value = "订单相关接口", tags = {"订单相关接口"})
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrdersController.class);

    @Resource
    private OrderService orderService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisOperator redisOperator;

    @Autowired
    private CheckOrderTopic orderStatusProducer;

    @ApiOperation(value = "获取订单token", notes = "获取订单token", httpMethod = "POST")
    @PostMapping("/token")
    public Result getOrderToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        // 这里的key看需求的粒度，若只防止用户在同一个浏览器上重复提交，在读个浏览器上算正常业务时，可使用sessionId
        redisOperator.set(CacheKey.ORDER_TOKEN.append(session.getId()), token, 600);
        return Result.ok(token);
    }

    /**
     * 创建订单
     * @return 地址列表
     */
    @ApiOperation(value = "创建订单", notes = "创建订单", httpMethod = "POST")
    @PostMapping("/create")
    public Result list(@RequestBody SubmitOrderBO submitOrderBO,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        LOGGER.info("创建订单请求 {}", JsonUtils.objectToJson(submitOrderBO));
        if (PayMethod.illegalPayMethod(submitOrderBO.getPayMethod())) {
            return Result.errorMsg("不合法的支付方式");
        }
        if (StringUtils.isBlank(submitOrderBO.getItemSpecIds())) {
            return Result.errorMsg("商品规格ID为空");
        }
        // 获取用户购物车中的商品
        String userShopCartCacheKey = CacheKey.SHOP_CART.append(submitOrderBO.getUserId());
        String shopCartJson = redisOperator.get(userShopCartCacheKey);
        if (StringUtils.isBlank(shopCartJson)) {
            return Result.errorMsg("购物车为空");
        }
        List<ShopCartBO> shopCartList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);
        if (CollectionUtils.isEmpty(shopCartList)) {
            return Result.errorMsg("购物车为空");
        }
        // 1、创建订单
        OrderVO order = orderService.createOrder(new PlaceOrderBO(shopCartList, submitOrderBO));
        // 2、提交的商品在购物车中移除
        shopCartList.removeIf(shopCart -> submitOrderBO.getItemSpecIds().contains(shopCart.getSpecId()));
        String currentShopCartJson = JsonUtils.objectToJson(shopCartList);
        redisOperator.set(userShopCartCacheKey, currentShopCartJson);
        CookieUtils.setCookie(request, response, CacheKey.SHOP_CART.value, currentShopCartJson, true);

        // stream延迟队列，一天后检查订单状态
        orderStatusProducer.output().send(
                MessageBuilder.withPayload(new OrderStatusCheckBO(order.getOrderId()))
                        .setHeader("x-delay", 86400000 + 300000)
                        .build()
        );

        // 3、向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = order.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(PAT_RETURN_URL);

        // 将所有支付金额统一改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);
        ResponseEntity<Result> payResponseResult = restTemplate.postForEntity(PAY_MENT_URL, entity, Result.class);
        Result result = payResponseResult.getBody();
        if (result == null || !result.isOK()) {
            return Result.errorMsg("支付中心订单创建失败，请联系管理员！");
        }

        LOGGER.info("创建订单请求响应结果 {}", JsonUtils.objectToJson(order));
        // 响应为{"orderId":"210117F9AR96A6NC","merchantOrdersVO":null}时报400错误，在请求目标中找到无效字符。有效字符在RFC 7230和RFC 3986中定义
        return Result.ok(order.getOrderId());
    }

    /**
     * 通知商户订单支付结果
     * @param merchantOrderId 商户订单ID
     * @return 结果
     */
    @ApiOperation(value = "支付中心更新订单状态为已支付", notes = "更新订单状态为已支付", httpMethod = "POST")
    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId){
        if (StringUtils.isBlank(merchantOrderId)) {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    /**
     * 查询订单状态
     * @param orderId 订单ID
     * @return 订单状态
     */
    @ApiOperation(value = "查询订单状态", notes = "查询订单状态", httpMethod = "POST")
    @PostMapping("getPaidOrderInfo")
    public Result getPaidOrderInfo(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return Result.errorMsg("");
        }
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return Result.ok(orderStatus);
    }
}
