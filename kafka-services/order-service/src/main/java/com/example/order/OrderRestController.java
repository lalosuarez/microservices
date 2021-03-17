package com.example.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import com.example.order.message.Order;

/**
 * Command API
 */
@RestController
public class OrderRestController {
    private static final String RESOURCE_PATH = "/orders";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRestController.class);
    private final OrderService orderService;

    @Autowired
    public OrderRestController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(path = RESOURCE_PATH, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody final Order order) throws URISyntaxException {
        LOGGER.info("Create order {}", order);
        orderService.create(order);
        return ResponseEntity
                .created(new URI("http://localhost:8084/v1/orders/" + order.getId()))
                .build();
    }

    @RequestMapping(path = RESOURCE_PATH, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll() {
        LOGGER.info("Get orders");
        final List<Order> orders = Arrays.asList(
                new Order("1234-5678", -1L, Order.Status.CANCELLED, orderService.getTimestamp()));
        final ListResponse<Order> listResponse = new ListResponse<>();
        listResponse.setTotal((long) orders.size());
        listResponse.setItems(orders);
        return ResponseEntity.ok(listResponse);
    }
}
