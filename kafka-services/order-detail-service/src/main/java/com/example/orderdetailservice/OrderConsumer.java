package com.example.orderdetailservice;

import com.example.orderservice.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(id = "order-consumer", topics = KafkaConfig.ORDERS_TOPIC)
    public void consumer(ConsumerRecord<String, Order> consumerRecord) {
        logger.debug("order consumer key: '{}' {} headers: '{}'",
                consumerRecord.key(), consumerRecord.value(), consumerRecord.headers());
    }
}
