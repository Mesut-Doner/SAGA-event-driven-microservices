package com.mdonerprojects.orderservices.command.rest;


import com.mdonerprojects.orderservices.command.CreateOrderCommand;
import com.mdonerprojects.orderservices.model.OrderSummary;
import com.mdonerprojects.orderservices.query.FindOrderQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    public OrdersCommandController(@Autowired CommandGateway commandGateway, @Autowired QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping(value = "/save")
    public OrderSummary createProduct(@RequestBody CreateOrderRestModel createOrderRestModel) {
        String orderId = UUID.randomUUID().toString();
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(orderId)
                .productId(createOrderRestModel.getProductId())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .quantity(createOrderRestModel.getQuantity())
                .addressId(createOrderRestModel.getAddressId())
                .build();
        String returnValue = null;
        try {
            returnValue = commandGateway.sendAndWait(createOrderCommand);
        } catch (Exception e) {
            returnValue = e.getLocalizedMessage();
        }
        SubscriptionQueryResult<OrderSummary, OrderSummary> result = queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummary.class), ResponseTypes.instanceOf(OrderSummary.class));

        try {
            return result.updates().blockFirst();
        } finally {
            result.close();

        }
    }

    @DeleteMapping
    public String deleteProduct() {
        return "HTTP DELETE Handled";
    }

    @PutMapping
    public String updateProduct() {
        return "HTTP PUT Handled";
    }

}
