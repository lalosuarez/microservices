package com.example.orderdetailservice;

import lombok.*;

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

