package com.example.orderdetailservice;

import com.example.orderservice.Order;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@EnableKafkaStreams
@EnableKafka
public class KafkaConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String ORDERS_TOPIC = "orders";
    public static final String ORDERS_VALIDATION = "orders-validation";

    public static final String ORDERS_BY_ID_STORE = "ORDERS_BY_ID_STORE";
    public static final String ORDERS_BY_CUSTOMER_ID_STORE = "ORDERS_BY_CUSTOMER_ID_STORE";

    @Bean
    NewTopic ordersValidationTopic() {
        return createTopic(ORDERS_VALIDATION, 3, 1);
    }

    private NewTopic createTopic(final String name, final int partitions, final int replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public Topology topology(final StreamsBuilder streamsBuilder) {
        defineOrderStream(streamsBuilder);
        final Topology topology = streamsBuilder.build();
        logger.debug("Topology description {}", topology.describe());
        return topology;
    }

    private void defineOrderStream(final StreamsBuilder streamsBuilder) {
        final Serde<String> stringSerde = Serdes.String();
        final JsonSerde<Order> orderSerde = new JsonSerde<>();
        // Builds the stream from the orders topic
        streamsBuilder.
                stream(KafkaConfig.ORDERS_TOPIC, Consumed.with(stringSerde, orderSerde));
    }
}
