package com.mdonerprojects.productservices.event.handler;

import com.mdonerprojects.productservices.event.ProductCreatedEvent;
import com.mdonerprojects.productservices.model.ProductEntity;
import com.mdonerprojects.productservices.repository.ProductRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private final ProductRepository productRepository;

    public ProductEventsHandler(@Autowired ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception){
        // sadece olduğu handler classtaki exceptionu yakalar
        exception.printStackTrace();
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception){
        exception.printStackTrace();
    }


    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent,productEntity);

        productRepository.save(productEntity);

    }
}
