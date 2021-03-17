package com.example.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);

    public static final String ORDERS_TOPIC = "orders";

    @Bean
    NewTopic ordersTopic() {
        return createTopic(ORDERS_TOPIC, 3, 1);
    }

    private NewTopic createTopic(final String name, final int partitions, final int replicas) {
        LOGGER.info("Create topic name: {}, partitions: {}, replicas: {}", name, partitions, replicas);
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .compact()
                .build();
    }
}
