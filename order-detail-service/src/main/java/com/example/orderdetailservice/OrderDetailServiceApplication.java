package com.example.orderdetailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableDiscoveryClient
@EnableBinding(OrderBinding.class)
@SpringBootApplication
public class OrderDetailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderDetailServiceApplication.class, args);
	}

}
