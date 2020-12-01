package com.example.orderservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageChannel messageChannel;

    @Autowired
    public OrderService(final OrderBinding orderBinding) {
        this.messageChannel = orderBinding.ordersOut();
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
     * @param order
     */
    private void sendMessage(final Order order) {
        try {
            byte[] msgKey = order.getId().getBytes();
            logger.debug("Message key bytes = {}", msgKey);
            Message<Order> message = MessageBuilder
                    .withPayload(order)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, msgKey)
                    .build();
            messageChannel.send(message);
            logger.info("Order sent {}", order);
        } catch (Exception e) {
            logger.error("Error sending order", e);
            e.printStackTrace();
        }
    }
}
