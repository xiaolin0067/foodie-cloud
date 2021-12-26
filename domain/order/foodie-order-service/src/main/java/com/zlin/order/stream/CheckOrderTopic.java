package com.zlin.order.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author zlin
 * @date 20211226
 */
public interface CheckOrderTopic {

    String INPUT = "order-status-consumer";

    String OUTPUT = "order-status-producer";

    @Input(INPUT)
    SubscribableChannel input();

    @Output(OUTPUT)
    MessageChannel output();

}
