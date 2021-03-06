package com.example.orderdetailservice;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListResponse<T> {
    private Long total;
    private List<T> items;
}
