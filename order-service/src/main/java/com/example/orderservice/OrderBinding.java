package com.example.orderservice;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 *
 */
public interface OrderBinding {
    // Stores for materialized views
    String ORDERS_BY_ID_STORE = "ORDERS_BY_ID_STORE";

    String ORDERS_OUT = "orders-out";

    @Output(ORDERS_OUT)
    MessageChannel ordersOut();
}
