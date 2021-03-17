package com.example.order;

import com.example.order.config.KafkaConfig;
import com.example.order.message.Order;
import com.example.order.validation.OrderValidation;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class OrderDetailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Autowired
    public OrderDetailService(StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    public ListResponse<OrderValidation> getOrdersValidation() {
        logger.debug("Getting orders validation");

        final KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        final ReadOnlyKeyValueStore<String, OrderValidation> store =
                kafkaStreams.store(StoreQueryParameters.fromNameAndType(KafkaConfig.ORDERS_VALIDATION_BY_ID_STORE,
                        QueryableStoreTypes.keyValueStore()));

        final List<OrderValidation> list = new LinkedList<>();
        final KeyValueIterator<String, OrderValidation> iterator = store.all();
        while (iterator.hasNext()) {
            KeyValue<String, OrderValidation> item = iterator.next();
            list.add(item.value);
        }
        return new ListResponse<>((long) list.size(), list);
    }

    public Map<String, Long> getOrdersValidationByStatus() {
        logger.debug("Getting orders validation count");

        final KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        final ReadOnlyKeyValueStore<String, Long> store =
                kafkaStreams.store(StoreQueryParameters.fromNameAndType(KafkaConfig.ORDERS_VALIDATION_BY_STATUS_STORE,
                        QueryableStoreTypes.keyValueStore()));

        // TODO: Create dto
        final Map<String, Long> map = new HashMap<>();
        final KeyValueIterator<String, Long> iterator = store.all();
        while (iterator.hasNext()) {
            KeyValue<String, Long> item = iterator.next();
            map.put(item.key.toLowerCase(), item.value);
        }
        return map;
    }

    public ListResponse<Order> getOrders() {
        logger.info("Getting orders");

        final KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        final ReadOnlyKeyValueStore<String, Order> store =
                kafkaStreams.store(StoreQueryParameters.fromNameAndType(KafkaConfig.ORDERS_BY_ID_STORE,
                        QueryableStoreTypes.keyValueStore()));

        final List<Order> list = new LinkedList<>();
        final KeyValueIterator<String, Order> iterator = store.all();
        while (iterator.hasNext()) {
            KeyValue<String, Order> item = iterator.next();
            list.add(item.value);
        }
        return new ListResponse((long)list.size(), list);
    }

    public ListResponse<Order> getOrdersByCustomerId(final Long id) {
        final KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        final ReadOnlyKeyValueStore<Long, OrdersByCustomer> store =
                kafkaStreams.store(StoreQueryParameters.fromNameAndType(KafkaConfig.ORDERS_BY_CUSTOMER_ID_STORE,
                        QueryableStoreTypes.keyValueStore()));

        final OrdersByCustomer ordersByCustomer = store.get(id);
        if (ordersByCustomer == null) {
            logger.info("Orders by customer {} not found", id);
            return null;
        }
        return new ListResponse((long)ordersByCustomer.getOrders().size(),
                        ordersByCustomer.getOrders());
    }
}
