package com.mdonerprojects.productservices.command.rest;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateProductRestModel {
    @NotBlank(message="Product title is required")
    private String title;
    @Min(value =1, message = "Price can not be less than 1")
    private BigDecimal price;
    @Min(value =1, message = "Quantity can not be less than 1")
    @Max(value =5, message = "Quantity can not be larger than 5")
    private Integer quantity;

}
