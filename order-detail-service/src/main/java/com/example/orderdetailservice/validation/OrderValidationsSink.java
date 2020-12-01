package com.example.orderdetailservice.validation;

import com.example.orderdetailservice.OrderBinding;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class OrderValidationsSink {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @StreamListener
    public void process(
            @Input(OrderBinding.ORDERS_VALIDATION_IN) final KStream<String, OrderValidation> orderValidations) {

        // Groups by validation status and stores the result in a materialized view
        orderValidations
                .peek((key, orderValidation) -> logger.info("Order {} validation result {}", key,
                        orderValidation.getValidationResult()))
                .groupBy((key, orderValidation) -> orderValidation.getValidationResult().name())
                .count(Materialized.as(OrderBinding.ORDERS_VALIDATION_BY_STATUS_STORE));

        // Groups by id and stores the result in a materialized view
        orderValidations
                .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(OrderValidation.class)))
                .aggregate(
                        OrderValidation::new,
                        (key, orderValidation, newOrderValidation) -> orderValidation,
                        Materialized.<String, OrderValidation, KeyValueStore<Bytes, byte[]>>as(OrderBinding.ORDERS_VALIDATION_BY_ID_STORE)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(OrderValidation.class))
                );
    }
}