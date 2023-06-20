package com.mdonerprojects.productservices.command;


import com.mdonerprojects.productservices.core.data.ProductLookupEntity;
import com.mdonerprojects.productservices.core.repository.ProductLookupRepository;
import com.mdonerprojects.productservices.event.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group") //it groups 2 event handler classes for same service
public class ProductLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventsHandler(@Autowired ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }


    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent){

        ProductLookupEntity productLookupEntity = new ProductLookupEntity();
        productLookupEntity.setProductId(productLookupEntity.getProductId());
        productLookupEntity.setTitle(productLookupEntity.getTitle());

        productLookupRepository.save(productLookupEntity);


    }

}
