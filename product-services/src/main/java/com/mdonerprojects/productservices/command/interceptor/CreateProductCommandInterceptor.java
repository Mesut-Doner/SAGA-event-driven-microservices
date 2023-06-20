package com.mdonerprojects.productservices.command.interceptor;

import com.mdonerprojects.productservices.command.CreateProductCommand;
import com.mdonerprojects.productservices.core.data.ProductLookupEntity;
import com.mdonerprojects.productservices.core.repository.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>>{

    private final ProductLookupRepository productLookupRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    public CreateProductCommandInterceptor(@Autowired ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> list) {
        return (index,command) ->{

            LOGGER.info("Intercepted command {}",command.getPayloadType());
            if(CreateProductCommand.class.equals(command.getPayloadType())){

                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                ProductLookupEntity productLookupEntity =
                productLookupRepository.findByProductIdOrTitle(
                        createProductCommand.getProductId(),
                        createProductCommand.getTitle());

                if(productLookupEntity !=  null){
                    throw new IllegalArgumentException(
                            String.format("DB'de ilgili %s product zaten var:",createProductCommand.getProductId()));
                }

            }
            return  command;
        };
    }
}
