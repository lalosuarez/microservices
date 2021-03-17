package com.example.order.validation;

import com.example.order.config.KafkaConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class OrderValidationsSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderValidationsSink.class);

    @Autowired
    public void process(final StreamsBuilder builder) {

        final var stringSerde = Serdes.String();
        final var orderValidationSerde = new JsonSerde<>(OrderValidation.class);

        final KStream<String, OrderValidation> orderValidations = builder.
                stream(KafkaConfig.ORDERS_VALIDATION_TOPIC, Consumed.with(stringSerde, orderValidationSerde));

        // Groups by validation status and stores the result in a materialized view
        orderValidations
                .peek((key, orderValidation) -> LOGGER.info("key: {}, validation result: {}", key,
                        orderValidation.getValidationResult()))
                .groupBy((key, orderValidation) -> orderValidation.getValidationResult().name())
                .count(Materialized.as(KafkaConfig.ORDERS_VALIDATION_BY_STATUS_STORE));

        // Groups by id and stores the result in a materialized view
        orderValidations
                .peek((key, value) -> LOGGER.debug("key: {}, value: {}", key, value))
                .groupByKey(Grouped.with(stringSerde, orderValidationSerde))
                .aggregate(
                        OrderValidation::new,
                        (key, orderValidation, newOrderValidation) -> orderValidation,
                        Materialized.<String, OrderValidation, KeyValueStore<Bytes, byte[]>>as(KafkaConfig.ORDERS_VALIDATION_BY_ID_STORE)
                                .withKeySerde(stringSerde)
                                .withValueSerde(orderValidationSerde)
                );
    }
}