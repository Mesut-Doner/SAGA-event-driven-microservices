package com.mdonerprojects.productservices.query.handler;


import com.mdonerprojects.productservices.model.ProductEntity;
import com.mdonerprojects.productservices.query.FindProductsQuery;
import com.mdonerprojects.productservices.query.rest.ProductRestModel;
import com.mdonerprojects.productservices.repository.ProductRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductsQueryHandler {

    private final ProductRepository productRepository;

    public ProductsQueryHandler(ProductRepository productRepository) {


        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery query) {

        List<ProductRestModel> productRestList = new ArrayList<>();

        List<ProductEntity> productEntityList = productRepository.findAll();

        for (ProductEntity productEntity : productEntityList) {

            ProductRestModel productRestModel = new ProductRestModel();

            BeanUtils.copyProperties(productEntity, productRestModel);

            productRestList.add(productRestModel);
        }

        return productRestList;
    }
}
