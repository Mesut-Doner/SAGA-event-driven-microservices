package com.mdonerprojects.orderservices.event;

import com.mdonerprojects.orderservices.util.OrderStatus;
import lombok.Data;


@Data
public class OrderCreatedEvent {


    public String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus = OrderStatus.CREATED;
}
