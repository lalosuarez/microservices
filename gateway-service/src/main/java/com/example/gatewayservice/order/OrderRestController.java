package com.example.gatewayservice.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderRestController {
    private static final String RESOURCE_PATH = "/v1/orders";
    private static final String ORDER_SERVICE_URL = "http://order-service/v1/orders";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate restTemplate;

    @Autowired
    public OrderRestController(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(path = RESOURCE_PATH, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll() {
        logger.info("Getting orders");
        return restTemplate.exchange(ORDER_SERVICE_URL, HttpMethod.GET, null, ListResponse.class);
    }
}
