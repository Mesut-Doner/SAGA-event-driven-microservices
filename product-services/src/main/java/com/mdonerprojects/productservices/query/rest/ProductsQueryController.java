package com.mdonerprojects.productservices.query.rest;

import com.mdonerprojects.productservices.query.FindProductsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductsQueryController {

    private final QueryGateway queryGateway;

    public ProductsQueryController(@Autowired QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<ProductRestModel> getProducts() {
        FindProductsQuery query = new FindProductsQuery();
        List<ProductRestModel> products = queryGateway.query(query,
                ResponseTypes.multipleInstancesOf(ProductRestModel.class)).join();

        return products;
    }

}
