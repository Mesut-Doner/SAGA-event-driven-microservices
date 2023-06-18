package com.mdonerprojects.productservices.controller;

import com.mdonerprojects.productservices.rest.CreateProductRestModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final Environment env;
    @PostMapping(value = "/save")
    public String createProduct(@RequestBody CreateProductRestModel createProductRestModel) {

        return "HTTP POST Handled with:"+createProductRestModel.getTitle();
    }

    @GetMapping
    public String getProduct() {
        return "HTTP GET Handled with instance:"+ env.getProperty("local.server.port");
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
