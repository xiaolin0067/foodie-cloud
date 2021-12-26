package com.zlin.order.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * stream中的消息类型
 * @author zlin
 * @date 20211226
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusCheckBO {

    private String orderId;

    // 后续可添加其他属性

}
