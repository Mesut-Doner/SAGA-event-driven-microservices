package com.mdonerprojects.orderservices.query;


import lombok.Value;

@Value
public class FindOrderQuery {

    private final String orderId;
}
