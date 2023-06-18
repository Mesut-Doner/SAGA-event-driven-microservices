package com.mdonerprojects.productservices.command.rest;





import com.mdonerprojects.productservices.command.CreateProductCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsCommandController {

    private final Environment env;

    private final CommandGateway commandGateway;
    @PostMapping(value = "/save")
    public String createProduct(@RequestBody CreateProductRestModel createProductRestModel) {

         CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .productId(UUID.randomUUID().toString())
                .price(createProductRestModel.getPrice())
                .title(createProductRestModel.getTitle())
                .quantity(createProductRestModel.getQuantity())
                .build();
        String returnValue = null;
        try {
            returnValue = commandGateway.sendAndWait(createProductCommand);
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
