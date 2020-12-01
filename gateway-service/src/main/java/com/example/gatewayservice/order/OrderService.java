package com.example.gatewayservice.order;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {
    private static final String ORDER_SERVICE_URL = "http://order-service/v1/orders";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate restTemplate;
    private final MessageChannel messageChannel;

    @Autowired
    public OrderService(final RestTemplate restTemplate, final OrderChannels orderChannels) {
        this.restTemplate = restTemplate;
        this.messageChannel = orderChannels.ordersOutFallback();
    }

    @HystrixCommand(fallbackMethod = "createFallback")
    public Order create(final Order order) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<Order> httpEntity = new HttpEntity<>(order, headers);
        return restTemplate
                .exchange(ORDER_SERVICE_URL, HttpMethod.POST, httpEntity, Order.class)
                .getBody();
    }

    @HystrixCommand(fallbackMethod = "getAllFallback")
    public ListResponse<Order> getAll() {
        logger.debug("Get orders");
        return restTemplate
                .exchange(ORDER_SERVICE_URL, HttpMethod.GET, null, ListResponse.class)
                .getBody();
    }

    private ListResponse<Order> getAllFallback() {
        logger.warn("Get orders fallback");
        return new ListResponse<>(0L, new ArrayList<>());
    }

    private Order createFallback(final Order order) {
        // In case of an error sends a message to the broker directly
        logger.warn("Create order fallback");
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
