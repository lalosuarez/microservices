package com.example.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafkaStreams
public class KafkaConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);

    public static final String ORDERS_TOPIC = "orders";
    public static final String ORDERS_VALIDATION_TOPIC = "orders-validation";

    public static final String ORDERS_BY_ID_STORE = "ORDERS_BY_ID_STORE";
    public static final String ORDERS_BY_CUSTOMER_ID_STORE = "ORDERS_BY_CUSTOMER_ID_STORE";
    public static final String ORDERS_VALIDATION_BY_ID_STORE = "ORDERS_VALIDATION_BY_ID_STORE";
    public static final String ORDERS_VALIDATION_BY_STATUS_STORE = "ORDERS_VALIDATION_BY_STATUS_STORE";

    // @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    /*public KafkaStreamsConfiguration kafkaStreamsConfig() {
        LOGGER.info("Init KafkaStreamsConfiguration");
        var configs = new HashMap<String, Object>();
        configs.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-detail-service");
        configs.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9091");
        configs.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configs.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configs.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "3000");
        configs.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        configs.put("spring.json.trusted.packages", "com.example.order.message");
        return new KafkaStreamsConfiguration(configs);
    }*/

    @Bean
    NewTopic ordersValidationTopic() {
        return createTopic(ORDERS_VALIDATION_TOPIC, 3, 1);
    }

    private NewTopic createTopic(final String name, final int partitions, final int replicas) {
        LOGGER.info("Create topic name: {}, partitions: {}, replicas: {}", name, partitions, replicas);
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
