package com.zlin.order.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.zlin.enums.OrderStatusEnum;
import com.zlin.enums.YesOrNo;
import com.zlin.order.mapper.OrderStatusMapper;
import com.zlin.order.mapper.OrdersMapper;
import com.zlin.order.mapper.OrdersMapperCustom;
import com.zlin.order.pojo.OrderStatus;
import com.zlin.order.pojo.Orders;
import com.zlin.order.pojo.vo.MyOrdersVO;
import com.zlin.order.pojo.vo.OrderStatusCountsVO;
import com.zlin.order.service.center.MyOrderService;
import com.zlin.pojo.PagedGridResult;
import com.zlin.pojo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlin
 * @date 20201226
 */
@Service
public class MyOrderServiceImpl implements MyOrderService {

    @Resource
    private OrdersMapperCustom ordersMapperCustom;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private OrdersMapper ordersMapper;

    /**
     * 分页查询我的订单列表
     *
     * @param userId      用户ID
     * @param orderStatus 订单状态
     * @param page        页码
     * @param pageSize    每页数量
     * @return 订单分页列表
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        if (orderStatus != null) {
            paramMap.put("orderStatus", orderStatus);
        }
        List<MyOrdersVO> myOrdersVoList = ordersMapperCustom.queryMyOrders(paramMap);
        return new PagedGridResult(myOrdersVoList, page);
    }

    /**
     * 更新订单发货状态
     *
     * @param orderId 订单ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        orderStatus.setDeliverTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        orderStatusMapper.updateByExampleSelective(orderStatus, example);
    }

    /**
     * 查询我的订单
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单实体
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public Orders queryMyOrder(String userId, String orderId) {
        Orders order = new Orders();
        order.setUserId(userId);
        order.setId(orderId);
        order.setIsDelete(YesOrNo.NO.type);
        return ordersMapper.selectOne(order);
    }

    /**
     * 更新订单状态 ---> 确认收货
     *
     * @param orderId 订单ID
     * @return 更新状态
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        orderStatus.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        int result = orderStatusMapper.updateByExampleSelective(orderStatus, example);
        return result == 1;
    }

    /**
     * 删除订单（逻辑删除）
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 删除结果
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean deleteOrder(String userId, String orderId) {
        Orders order = new Orders();
        order.setIsDelete(YesOrNo.YES.type);
        order.setUpdatedTime(new Date());

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("userId", userId);
        int result = ordersMapper.updateByExampleSelective(order, example);
        return result == 1;
    }

    /**
     * 查询用户订单数
     *
     * @param userId 用户ID
     * @return 订单数VO
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public OrderStatusCountsVO getOrderStatusCounts(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.type);
        int waitPayCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.SUCCESS.type);
        map.put("isComment", YesOrNo.NO.type);
        int waitCommentCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        return new OrderStatusCountsVO(waitPayCounts,
                waitDeliverCounts,
                waitReceiveCounts,
                waitCommentCounts);
    }

    /**
     * 获得分页的订单动向
     *
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页数量
     * @return 订单动向
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("userId", userId);
        PageHelper.startPage(page, pageSize);
        List<OrderStatus> list = ordersMapperCustom.getMyOrderTrend(map);

        return new PagedGridResult(list, page);
    }

    @Override
    public Result checkUserOrder(String userId, String orderId) {
        Orders order = queryMyOrder(userId, orderId);
        if (order == null) {
            return Result.errorMsg("订单不存在！");
        }
        return Result.ok(order);
    }
}
