package com.zlin.order.service.center;

import com.zlin.order.pojo.OrderItems;
import com.zlin.order.pojo.bo.center.OrderItemsCommentBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zlin
 * @date 20210214
 */
@FeignClient("foodie-order-service")
@RequestMapping("order-comments-api")
public interface MyCommentsService {

    /**
     * 查询订单商品
     * @param orderId 订单ID
     * @return 商品列表
     */
    @GetMapping("orderItems")
    List<OrderItems> queryPendingComment(@RequestParam("orderId") String orderId);

    /**
     * 保存评价
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param commentList 评价列表
     */
    @PostMapping("saveOrderComments")
    void saveComments(@RequestParam("orderId") String orderId,
                      @RequestParam("userId") String userId,
                      @RequestBody List<OrderItemsCommentBO> commentList);
}
