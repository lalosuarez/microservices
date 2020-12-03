package com.example.orderdetailservice;

import com.example.orderdetailservice.validation.OrderValidation;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*@StreamListener
    @SendTo({OrderBinding.ORDERS_VALIDATION_OUT})
    public KStream<String, OrderValidation> validateOrder(
            @Input(OrderBinding.ORDERS_IN) final KStream<String, Order> orders) {

        // Groups by id and stores the result in a materialized view
        orders
                .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
                .aggregate(
                        Order::new,
                        (key, order, newOrder) -> order,
                        Materialized.<String, Order, KeyValueStore<Bytes, byte[]>>as(OrderBinding.ORDERS_BY_ID_STORE)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(Order.class))
                );

        // Groups by customer id and stores the result in a materialized view
        orders
                .groupBy((s, order) -> order.getCustomerId(), Grouped.with(Serdes.Long(), new JsonSerde<>(Order.class)))
                .aggregate(
                        OrdersByCustomer::new,
                        (customerId, order, ordersByCustomer) -> {
                            ordersByCustomer.getOrders().add(order);
                            logger.debug("orders by customer id {} = {}", customerId, ordersByCustomer.getOrders().size());
                            return ordersByCustomer;
                        },
                        Materialized.<Long, OrdersByCustomer, KeyValueStore<Bytes, byte[]>>as(OrderBinding.ORDERS_BY_CUSTOMER_ID_STORE)
                                .withKeySerde(Serdes.Long())
                                .withValueSerde(new JsonSerde<>(OrdersByCustomer.class))
                );

        return orders
                .filter((key, order) -> Order.Status.CREATED.equals(order.getStatus()))
                .map((key, order) -> getOrderValidationResult(order, isValid(order) ?
                        OrderValidationResult.PASS : OrderValidationResult.FAIL));
    }*/

    @StreamListener
    @SendTo({OrderBinding.ORDERS_VALIDATION_OUT})
    public KStream<String, OrderValidation> validateOrder(
            @Input(OrderBinding.ORDERS_IN) final KStream<String, Order> orders) {
        logger.info("Validate order processor");

        // Groups by id and stores the result in a materialized view
        groupById(orders);

        // Groups by customer id and stores the result in a materialized view
        groupByCustomerId(orders);

        return getOrderValidationKStream(orders);
    }

    @StreamListener
    @SendTo({OrderBinding.ORDERS_VALIDATION_OUT})
    public KStream<String, OrderValidation> validateOrderFromFallback(
            @Input(OrderBinding.ORDERS_IN_FALLBACK) final KStream<String, Order> orders) {
        logger.info("Validate order from fallback processor");

        // Groups by id and stores the result in a materialized view
        groupById(orders);

        // Groups by customer id and stores the result in a materialized view
        groupByCustomerId(orders);

        return getOrderValidationKStream(orders);
    }

    private void groupById(final KStream<String, Order> orders) {
        orders
                .peek((key, value) -> logger.debug("Order group by id {} {}", key, value))
                .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
                .aggregate(
                        Order::new,
                        (key, order, newOrder) -> order,
                        Materialized.<String, Order, KeyValueStore<Bytes, byte[]>>as(OrderBinding.ORDERS_BY_ID_STORE)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(Order.class))
                );
    }

    private void groupByCustomerId(final KStream<String, Order> orders) {
        orders
                .groupBy((s, order) -> order.getCustomerId(), Grouped.with(Serdes.Long(), new JsonSerde<>(Order.class)))
                .aggregate(
                        OrdersByCustomer::new,
                        (customerId, order, ordersByCustomer) -> {
                            ordersByCustomer.getOrders().add(order);
                            logger.debug("Order by customer id {} = {}", customerId, ordersByCustomer.getOrders().size());
                            return ordersByCustomer;
                        },
                        Materialized.<Long, OrdersByCustomer, KeyValueStore<Bytes, byte[]>>as(OrderBinding.ORDERS_BY_CUSTOMER_ID_STORE)
                                .withKeySerde(Serdes.Long())
                                .withValueSerde(new JsonSerde<>(OrdersByCustomer.class))
                );
    }

    private KStream<String, OrderValidation> getOrderValidationKStream(final KStream<String, Order> orders) {
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
        logger.info("Validating order {}", order.getId());
        final OrderValidation value = new OrderValidation(order.getId(), OrderValidation.Type.ORDER_DETAILS_CHECK, passOrFail);
        logger.debug("Validation result {}", value);
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