package com.example.orderdetailservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Query API
 */
@RestController
public class OrderDetailController {
    private static final String RESOURCE_PATH = "/orders";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailController(final OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @RequestMapping(path = RESOURCE_PATH + "/validations", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity ordersValidation() {
        logger.info("Getting orders validation");
        return ResponseEntity.ok()
                .body(orderDetailService.getOrdersValidation());
    }

    @RequestMapping(path = RESOURCE_PATH + "/validations/status", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity ordersValidationByStatus() {
        logger.info("Getting orders validation count");
        return ResponseEntity.ok().body(orderDetailService.getOrdersValidationByStatus());
    }

    @RequestMapping(path = RESOURCE_PATH, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity orders() {
        logger.info("Getting orders");
        return ResponseEntity.ok()
                .body(orderDetailService.getOrders());
    }

    @RequestMapping(path = RESOURCE_PATH + "/customers/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity ordersByCustomerId(@PathVariable("id") final Long id) {
        logger.info("Getting orders by customer {}", id);
        final ListResponse<Order> ordersByCustomer = orderDetailService.getOrdersByCustomerId(id);
        if (ordersByCustomer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ordersByCustomer);
    }
}
