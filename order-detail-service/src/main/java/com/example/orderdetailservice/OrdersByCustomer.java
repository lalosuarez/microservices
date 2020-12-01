package com.example.orderdetailservice;

import com.example.kafka.streams.order.Order;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrdersByCustomer {
    private List<Order> orders = new LinkedList<>();
}
