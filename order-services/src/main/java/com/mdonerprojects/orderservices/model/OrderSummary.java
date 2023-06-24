package com.mdonerprojects.orderservices.model;

import com.mdonerprojects.core.util.OrderStatus;
import lombok.Value;

@Value
public class OrderSummary {
    private String orderId;
    private OrderStatus orderStatus;
    private String message;
}
