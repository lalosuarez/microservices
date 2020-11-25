package com.example.gatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Beans {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        logger.debug("Creating bean for RestTemplate");
        return new RestTemplate();
    }
}
