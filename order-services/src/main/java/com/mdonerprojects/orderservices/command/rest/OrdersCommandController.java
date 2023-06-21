package com.mdonerprojects.orderservices.command.rest;


import com.mdonerprojects.orderservices.command.CreateOrderCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
    private final CommandGateway commandGateway;

    public OrdersCommandController(@Autowired CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping(value = "/save")
    public String createProduct(@RequestBody CreateOrderRestModel createOrderRestModel) {

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(UUID.randomUUID().toString())
                .productId(createOrderRestModel.getProductId())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .quantity(createOrderRestModel.getQuantity())
                .addressId(createOrderRestModel.getAddressId())
                .build();
        String returnValue = null;
        try {
            returnValue = commandGateway.sendAndWait(createOrderCommand);
        } catch (Exception e) {
            returnValue=e.getLocalizedMessage();
        }
        return "Return value:"+returnValue;
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
