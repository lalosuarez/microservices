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
public class OrderValidation {
    private String orderId;
    private OrderValidationType checkType;
    private OrderValidationResult validationResult;
}
