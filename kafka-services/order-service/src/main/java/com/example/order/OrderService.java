package com.example.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.Date;
import com.example.order.message.Order;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderService(/*@Qualifier("orderKafkaTemplate")*/ KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * @param order
     * @return
     */
    public Order create(final Order order) {
        order.setId(UUID.randomUUID().toString());
        order.setStatus(Order.Status.CREATED);
        order.setTimestamp(getTimestamp());
        sendMessage(order);
        return order;
    }

    /**
     * @return
     */
    public long getTimestamp() {
        return new Date().getTime();
    }

    /**
     * Sends a message to the broker
     *
     * @param order order
     */
    private void sendMessage(final Order order) {
        final Message<Order> message = MessageBuilder
                .withPayload(order)
                .setHeader(KafkaHeaders.MESSAGE_KEY, order.getId())
                .setHeader(KafkaHeaders.TOPIC, KafkaConfig.ORDERS_TOPIC)
                .build();

        kafkaTemplate.send(message)
                .addCallback(successCallback(order), failureCallback());

        /*kafkaTemplate.send(KafkaConfig.ORDERS_TOPIC, order.getId(), order)
                .addCallback(successCallback(order), failureCallback());*/
    }

    private FailureCallback failureCallback() {
        return ex -> LOGGER.error("Error sending order", ex);
    }

    private SuccessCallback<SendResult<String, Order>> successCallback(Order order) {
        return result -> {
            if (result != null) {
                final String topic = result.getRecordMetadata().topic();
                final long offset = result.getRecordMetadata().offset();
                final int partition = result.getRecordMetadata().partition();
                LOGGER.info("Callback {} topic '{}' offset = '{}' partition = '{}'", order, topic, offset, partition);
            }
        };
    }
}
