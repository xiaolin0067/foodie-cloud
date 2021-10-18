package com.zlin.order.service.center;

import com.zlin.order.pojo.Orders;
import com.zlin.order.pojo.vo.OrderStatusCountsVO;
import com.zlin.pojo.PagedGridResult;
import com.zlin.pojo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlin
 * @date 20201226
 */
@FeignClient("foodie-order-service")
@RequestMapping("myorder-api")
public interface MyOrderService {

    /**
     * 分页查询我的订单列表
     *
     * @param userId 用户ID
     * @param orderStatus 订单状态
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单分页列表
     */
    @GetMapping("order/page")
    PagedGridResult queryMyOrders(@RequestParam("userId") String userId,
                                  @RequestParam("orderStatus") Integer orderStatus,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 更新订单发货状态
     * @param orderId 订单ID
     */
    @PutMapping("order/delivered")
    void updateDeliverOrderStatus(@RequestParam("orderId") String orderId);

    /**
     * 查询我的订单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 订单实体
     */
    @GetMapping("order/details")
    Orders queryMyOrder(@RequestParam("userId") String userId,
                        @RequestParam("orderId") String orderId);

    /**
     * 更新订单状态 ---> 确认收货
     * @param orderId 订单ID
     * @return 更新状态
     */
    @PutMapping("order/received")
    boolean updateReceiveOrderStatus(@RequestParam("orderId") String orderId);

    /**
     * 删除订单（逻辑删除）
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 删除结果
     */
    @DeleteMapping("order")
    boolean deleteOrder(@RequestParam("userId") String userId,
                        @RequestParam("orderId") String orderId);

    /**
     * 查询用户订单数
     * @param userId 用户ID
     * @return 订单数VO
     */
    @GetMapping("order/counts")
    OrderStatusCountsVO getOrderStatusCounts(@RequestParam("orderId") String userId);

    /**
     * 获得分页的订单动向
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单动向
     */
    @GetMapping("order/trend/page")
    PagedGridResult getOrdersTrend(@RequestParam("userId") String userId,
                                   @RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 检查用户订单
     */
    @GetMapping("checkUserOrder")
    Result checkUserOrder(@RequestParam("userId") String userId,
                          @RequestParam("orderId") String orderId);

}
