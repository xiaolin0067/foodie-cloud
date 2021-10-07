package com.zlin.order.service;

import com.zlin.order.pojo.OrderStatus;
import com.zlin.order.pojo.bo.PlaceOrderBO;
import com.zlin.order.pojo.vo.OrderVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlin
 * @date 20201226
 */
@RequestMapping("order-api")
public interface OrderService {

    /**
     * 用于创建订单相关信息
     * @param orderBO 创建订单BO
     * @return 订单VO
     */
    @PostMapping("placeOrder")
    OrderVO createOrder(@RequestBody PlaceOrderBO orderBO);

    /**
     * 修改订单状态
     * @param orderId 订单ID
     * @param orderStatus 订单状态
     */
    @PutMapping("orderStatus")
    void updateOrderStatus(@RequestParam("orderId") String orderId,
                           @RequestParam("orderStatus") Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId 订单ID
     * @return 订单状态
     */
    @GetMapping("orderStatus")
    OrderStatus queryOrderStatusInfo(@RequestParam("orderId") String orderId);

    /**
     * 关闭超时未支付订单
     */
    @PostMapping("closePayOvertimeOrder")
    void closePayOvertimeOrder();
}
