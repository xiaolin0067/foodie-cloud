package com.zlin.order.stream;

import com.zlin.enums.OrderStatusEnum;
import com.zlin.order.mapper.OrderStatusMapper;
import com.zlin.order.pojo.OrderStatus;
import com.zlin.order.pojo.bo.OrderStatusCheckBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author zlin
 * @date 20211226
 */
@Slf4j
@EnableBinding(value = CheckOrderTopic.class)
public class CheckOrderConsumer {

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @StreamListener(CheckOrderTopic.INPUT)
    public void consumerCheckOrderStatusMessage(OrderStatusCheckBO checkBo) {
        log.info("stream接收到订单状态检查消息 OrderStatusCheckBO={}", checkBo);
        if (checkBo == null || StringUtils.isBlank(checkBo.getOrderId())) {
            log.warn("接收到的检查订单状态消息中orderId为空");
            return;
        }
        String orderId = checkBo.getOrderId();
        OrderStatus orderStatusQuery = new OrderStatus();
        orderStatusQuery.setOrderId(checkBo.getOrderId());
        orderStatusQuery.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> orderStatuses = orderStatusMapper.select(orderStatusQuery);
        if (CollectionUtils.isEmpty(orderStatuses)) {
            log.debug("指定的订单ID不存在或不在等待状态 orderIds={}", orderId);
            return;
        }
        OrderStatus orderStatus = orderStatuses.get(0);
        long createTime = orderStatus.getCreatedTime().getTime();
        if ((System.currentTimeMillis() - createTime) > 86400000) {
            OrderStatus os = new OrderStatus();
            os.setOrderStatus(OrderStatusEnum.CLOSE.type);
            os.setCloseTime(new Date());
            os.setOrderId(orderStatus.getOrderId());
            orderStatusMapper.updateByPrimaryKeySelective(os);
        }
        log.info("已关闭超时订单 closedOrderIds={}", orderId);
    }

}
