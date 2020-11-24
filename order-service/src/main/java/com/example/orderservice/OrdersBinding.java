package com.example.orderservice;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 *
 */
public interface OrdersBinding {
    String ORDERS_OUT = "orders-out";

    @Output(ORDERS_OUT)
    MessageChannel ordersOut();
}
