package com.example.order;

import com.example.order.config.KafkaConfig;
import com.example.order.message.Order;
import com.example.order.validation.OrderValidation;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDetailProcessor.class);

    @Autowired
    public void validateOrder(final StreamsBuilder builder) {
        LOGGER.info("Validate order processor");

        final var stringSerde = Serdes.String();
        final var orderSerde = new JsonSerde<>(Order.class);
        final var orderValidationSerde = new JsonSerde<>(OrderValidation.class);

        // Builds the stream from the orders topic
        KStream<String, Order> ordersStream = builder.
                stream(KafkaConfig.ORDERS_TOPIC, Consumed.with(stringSerde, orderSerde));

        // Groups by id and stores the result in a materialized view
        groupById(ordersStream);

        // Groups by customer id and stores the result in a materialized view
        groupByCustomerId(ordersStream);

        // Sends the validation stream to validations topic
        getOrderValidationKStream(ordersStream)
                 .to(KafkaConfig.ORDERS_VALIDATION_TOPIC, Produced.with(stringSerde, orderValidationSerde));
    }

    private void groupById(final KStream<String, Order> orders) {
        LOGGER.debug("Validate order processor group by id");
        final var stringSerde = Serdes.String();
        final var orderSerde = new JsonSerde<>(Order.class);
        orders
                .peek((key, value) -> LOGGER.info("key: {}, value: {}", key, value))
                .groupByKey(Grouped.with(stringSerde, orderSerde))
                .aggregate(
                        Order::new,
                        (key, order, newOrder) -> order,
                        Materialized.<String, Order, KeyValueStore<Bytes, byte[]>>as(KafkaConfig.ORDERS_BY_ID_STORE)
                                .withKeySerde(stringSerde)
                                .withValueSerde(orderSerde)
                );
    }

    private void groupByCustomerId(final KStream<String, Order> orders) {
        LOGGER.debug("Validate order processor group by customer id");
        final Serde<Long> longSerde = Serdes.Long();
        final var orderSerde = new JsonSerde<>(Order.class);
        final var orderByCustomerSerde = new JsonSerde<>(OrdersByCustomer.class);
        orders
                .groupBy((s, order) -> order.getCustomerId(), Grouped.with(longSerde, orderSerde))
                .aggregate(
                        OrdersByCustomer::new,
                        (customerId, order, ordersByCustomer) -> {
                            ordersByCustomer.getOrders().add(order);
                            LOGGER.info("customerId: {}, total of orders: {}", customerId, ordersByCustomer.getOrders().size());
                            return ordersByCustomer;
                        },
                        Materialized.<Long, OrdersByCustomer, KeyValueStore<Bytes, byte[]>>as(KafkaConfig.ORDERS_BY_CUSTOMER_ID_STORE)
                                .withKeySerde(longSerde)
                                .withValueSerde(orderByCustomerSerde)
                );
    }

    private KStream<String, OrderValidation> getOrderValidationKStream(final KStream<String, Order> orders) {
        LOGGER.debug("Validate order processor get validation stream");
        return orders
                .filter((key, order) -> Order.Status.CREATED.equals(order.getStatus()))
                .map((key, order) -> getOrderValidationResult(order, isValid(order) ?
                        OrderValidation.Status.PASS : OrderValidation.Status.FAIL));
    }

    /**
     *
     * @param order
     * @param passOrFail
     * @return
     */
    private KeyValue<String, OrderValidation> getOrderValidationResult(final Order order,
                                                                       final OrderValidation.Status passOrFail) {
        LOGGER.info("Validating order {}", order.getId());
        final OrderValidation value = new OrderValidation(order.getId(), OrderValidation.Type.ORDER_DETAILS_CHECK, passOrFail);
        LOGGER.info("Validation result {}", value);
        return new KeyValue<>(order.getId(), value);
    }

    /**
     *
     * @param order
     * @return
     */
    private boolean isValid(final Order order) {
        if (order.getId() == null || order.getId().isBlank() || order.getId().isEmpty()
                || order.getCustomerId() == null || order.getCustomerId() <= 0
                || order.getStatus() == null) return false;
        return true;
    }
}
