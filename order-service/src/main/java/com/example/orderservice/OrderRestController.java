package com.example.orderservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Command API
 */
@RestController
public class OrderRestController {

    private static final Logger logger = LoggerFactory.getLogger(OrderRestController.class);

    private static final String RESOURCE_PATH = "/orders";

    private final OrderService orderService;

    public OrderRestController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(path = RESOURCE_PATH, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody final Order order) throws URISyntaxException {
        logger.info("Creating order {}", order);
        orderService.create(order);
        return ResponseEntity
                .created(new URI("http://localhost:8084/v1/orders/" + order.getId()))
                .build();
    }

    @RequestMapping(path = RESOURCE_PATH, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll() {
        logger.info("Getting orders");
        final ListResponse<Order> listResponse = new ListResponse<>();
        listResponse.setTotal(0L);
        listResponse.setItems(new ArrayList<>());
        return ResponseEntity.ok(listResponse);
    }
}