package com.mdonerprojects.productservices.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class ProductController {

    @PostMapping
    public String createProduct() {
        return "HTTP POST Handled";
    }

    @GetMapping
    public String getProduct() {
        return "HTTP GET Handled";
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
