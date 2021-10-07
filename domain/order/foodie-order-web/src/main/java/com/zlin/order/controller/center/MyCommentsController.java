package com.zlin.order.controller.center;

import com.zlin.controller.BaseController;
import com.zlin.enums.YesOrNo;
import com.zlin.order.pojo.OrderItems;
import com.zlin.order.pojo.Orders;
import com.zlin.order.pojo.bo.center.OrderItemsCommentBO;
import com.zlin.order.service.center.MyCommentsService;
import com.zlin.order.service.center.MyOrderService;
import com.zlin.pojo.PagedGridResult;
import com.zlin.pojo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zlin
 * @date 20210121
 */
@Api(value = "用户中心-我的评价相关接口", tags = {"用户中心-我的评价相关接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Resource
    private MyCommentsService myCommentsService;

    @Autowired
    private MyOrderService myOrderService;

    @Autowired
    private LoadBalancerClient client;

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "查询未评价订单商品列表", notes = "查询未评价订单商品列表", httpMethod = "POST")
    @PostMapping("pending")
    public Result query(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单ID", required = true)
            @RequestParam String orderId) {
        Result result = myOrderService.checkUserOrder(userId, orderId);
        if (!result.isOK()) {
            return result;
        }
        Orders orders = (Orders) result.getData();
        if (orders.getIsComment().equals(YesOrNo.YES.type)) {
            return Result.errorMsg("订单已评价!");
        }
        List<OrderItems> orderItemsList = myCommentsService.queryPendingComment(orderId);
        return Result.ok(orderItemsList);
    }

    @ApiOperation(value = "保存商品评价列表", notes = "保存商品评价列表", httpMethod = "POST")
    @PostMapping("saveList")
    public Result saveList(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单ID", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList) {
        if (CollectionUtils.isEmpty(commentList)) {
            return Result.errorMsg("评价列表为空!");
        }
        Result result = myOrderService.checkUserOrder(userId, orderId);
        if (!result.isOK()) {
            return result;
        }
        Orders orders = (Orders) result.getData();
        if (orders.getIsComment().equals(YesOrNo.YES.type)) {
            return Result.errorMsg("订单已评价!");
        }
        myCommentsService.saveComments(orderId, userId, commentList);
        return Result.ok();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public Result query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam("userId") String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数")
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return Result.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        ServiceInstance userInstance = client.choose("FOODIE-ITEM-SERVICE");
        String getAddressUrl = String.format("http://%s:%s/item-comments-api/myComments?userId=%s&page=%s&pageSize=%s",
                userInstance.getHost(),
                userInstance.getPort(),
                userId,
                page,
                pageSize
        );
        PagedGridResult grid = restTemplate.getForObject(getAddressUrl, PagedGridResult.class);
        return Result.ok(grid);
    }

}
