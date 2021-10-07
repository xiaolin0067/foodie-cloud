package com.zlin.order.service.impl.center;

import com.zlin.enums.YesOrNo;
import com.zlin.order.mapper.OrderItemsMapper;
import com.zlin.order.mapper.OrderStatusMapper;
import com.zlin.order.mapper.OrdersMapper;
import com.zlin.order.pojo.OrderItems;
import com.zlin.order.pojo.OrderStatus;
import com.zlin.order.pojo.Orders;
import com.zlin.order.pojo.bo.center.OrderItemsCommentBO;
import com.zlin.order.service.center.MyCommentsService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlin
 * @date 20210214
 */
@Service
public class MyCommentsServiceImpl implements MyCommentsService {

    @Resource
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private LoadBalancerClient client;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private Sid sid;

    /**
     * 查询订单商品
     *
     * @param orderId 订单ID
     * @return 商品列表
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems orderItems = new OrderItems();
        orderItems.setOrderId(orderId);
        return orderItemsMapper.select(orderItems);
    }

    /**
     * 保存评价
     *
     * @param orderId     订单ID
     * @param userId      用户ID
     * @param commentList 评价列表
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList) {
        // 1.保存评价 items_comments
        for (OrderItemsCommentBO commentBo : commentList) {
            commentBo.setCommentId(sid.nextShort());
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", userId);
        map.put("commentList", commentList);

        ServiceInstance instance = client.choose("FOODIE-ITEM-SERVICE");
        String url = String.format("http://%s:%s/item-comments-api/saveOrderComments",
                instance.getHost(),
                instance.getPort());
        restTemplate.postForLocation(url, map);

        // 2.修改订单状态为已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNo.YES.type);
        ordersMapper.updateByPrimaryKeySelective(order);

        // 3.修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }
}
