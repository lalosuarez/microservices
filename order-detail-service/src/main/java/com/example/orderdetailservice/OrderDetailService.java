package com.example.orderdetailservice;

import com.example.orderdetailservice.validation.OrderValidation;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class OrderDetailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InteractiveQueryService interactiveQueryService;

    @Autowired
    public OrderDetailService(final InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
    }

    public ListResponse<OrderValidation> getOrdersValidation() {
        logger.debug("Getting orders validation");
        final ReadOnlyKeyValueStore<String, OrderValidation> store = interactiveQueryService
                .getQueryableStore(OrderBinding.ORDERS_VALIDATION_BY_ID_STORE,
                        QueryableStoreTypes.keyValueStore());

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
        final ReadOnlyKeyValueStore<String, Long> store = interactiveQueryService
                .getQueryableStore(OrderBinding.ORDERS_VALIDATION_BY_STATUS_STORE,
                        QueryableStoreTypes.keyValueStore());

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
        final ReadOnlyKeyValueStore<String, Order> store = interactiveQueryService
                .getQueryableStore(OrderBinding.ORDERS_BY_ID_STORE,
                        QueryableStoreTypes.keyValueStore());

        final List<Order> list = new LinkedList<>();
        final KeyValueIterator<String, Order> iterator = store.all();
        while (iterator.hasNext()) {
            KeyValue<String, Order> item = iterator.next();
            list.add(item.value);
        }
        return new ListResponse((long)list.size(), list);
    }

    public ListResponse<Order> getOrdersByCustomerId(final Long id) {
        final ReadOnlyKeyValueStore<Long, OrdersByCustomer> store = interactiveQueryService
                .getQueryableStore(OrderBinding.ORDERS_BY_CUSTOMER_ID_STORE,
                        QueryableStoreTypes.keyValueStore());

        final OrdersByCustomer ordersByCustomer = store.get(id);
        if (ordersByCustomer == null) {
            logger.info("Orders by customer {} not found", id);
            return null;
        }
        return new ListResponse((long)ordersByCustomer.getOrders().size(),
                        ordersByCustomer.getOrders());
    }
}
