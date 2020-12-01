package com.example.orderdetailservice;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private String id;
    private Long customerId;
    private Status status;
    private Long timestamp;

    enum Status {
        CREATED, VALIDATED, CANCELLED, SHIPPED;
    }
}

