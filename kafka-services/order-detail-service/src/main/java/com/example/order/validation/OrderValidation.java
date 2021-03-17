package com.example.order.validation;

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
    private Type checkType;
    private Status validationResult;

    public enum Type {
        INVENTORY_CHECK, FRAUD_CHECK, ORDER_DETAILS_CHECK;
    }

    public enum Status {
        PASS, FAIL, ERROR;
    }
}
