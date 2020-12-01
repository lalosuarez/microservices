package com.example.gatewayservice.order;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderChannels {
    String ORDERS_OUT_FALLBACK = "orders-out-fallback";

    @Output(ORDERS_OUT_FALLBACK)
    MessageChannel ordersOutFallback();
}
