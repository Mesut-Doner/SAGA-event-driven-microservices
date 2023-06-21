package com.mdonerprojects.orderservices.command.rest;

import com.mdonerprojects.orderservices.util.OrderStatus;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CreateOrderRestModel {
    private String productId;
    private int quantity;
    private String addressId;
}
