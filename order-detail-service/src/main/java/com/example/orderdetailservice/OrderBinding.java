package com.example.orderdetailservice;

import com.example.orderdetailservice.validation.OrderValidation;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

/**
 *
 */
public interface OrderBinding {
    // Stores for materialized views
    String ORDERS_VALIDATION_BY_ID_STORE = "ORDERS_VALIDATION_BY_ID_STORE";
    String ORDERS_VALIDATION_BY_STATUS_STORE = "ORDERS_VALIDATION_BY_STATUS_STORE";
    String ORDERS_BY_ID_STORE = "ORDERS_BY_ID_STORE";
    String ORDERS_BY_CUSTOMER_ID_STORE = "ORDERS_BY_CUSTOMER_ID_STORE";

    String ORDERS_IN = "orders-in";
    String ORDERS_IN_FALLBACK = "orders-in-fallback";

    String ORDERS_VALIDATION_OUT = "orders-validation-out";
    String ORDERS_VALIDATION_IN = "orders-validation-in";

    @Input(ORDERS_IN)
    KStream<String, Order> ordersIn();

    @Input(ORDERS_IN_FALLBACK)
    KStream<String, Order> ordersInFallback();

    @Output(ORDERS_VALIDATION_OUT)
    KStream<String, OrderValidation> ordersValidationOut();

    @Input(ORDERS_VALIDATION_IN)
    KStream<String, OrderValidation> ordersValidationIn();
}
