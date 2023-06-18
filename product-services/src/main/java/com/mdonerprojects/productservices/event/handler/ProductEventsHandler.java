package com.mdonerprojects.productservices.event.handler;

import com.mdonerprojects.productservices.event.ProductCreatedEvent;
import com.mdonerprojects.productservices.model.ProductEntity;
import com.mdonerprojects.productservices.repository.ProductRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductEventsHandler {

    private final ProductRepository productRepository;

    public ProductEventsHandler(@Autowired ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent,productEntity);

        productRepository.save(productEntity);

    }
}
