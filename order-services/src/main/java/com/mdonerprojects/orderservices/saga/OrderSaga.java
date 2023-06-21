package com.mdonerprojects.orderservices.saga;


import com.mdonerprojects.core.ProcessPaymentCommand;
import com.mdonerprojects.core.commands.ReserveProductCommand;
import com.mdonerprojects.core.events.ProductReservedEvent;
import com.mdonerprojects.core.query.FetchUserPaymentDetailsQuery;
import com.mdonerprojects.orderservices.event.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);


    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand
                .builder()
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .orderId(orderCreatedEvent.getOrderId())
                .build();

        LOGGER.info("OrderCreatedEvent handled for orderId:" + reserveProductCommand.getOrderId() + " and productId:" + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    // compensating queue start
                }
            }
        });

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {

        LOGGER.info("OrderCreatedEvent handled for orderId:" + productReservedEvent.getOrderId() + " and productId:" + productReservedEvent.getProductId());

        // process user payment

        FetchUserPaymentDetailsQuery query = new FetchUserPaymentDetailsQuery();
        query.setUserId(productReservedEvent.getUserId());


        UserObj user = null;

        try {
            user = queryGateway.query(query,
                    ResponseTypes.instanceOf(UserObj.class)).join();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

            // start compensating transaction
            return;

        }

        if (user == null) {
            // start compensating transaction
            return;

        }

        LOGGER.info("Successfully fetched user payment details for user:" + user.getUserId());

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand
                .builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(user.getPaymentDetails())
                .build();
        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            // start compensating transaction
        }
        if(result == null){
            LOGGER.info("ProcessPaymentCommand result is null. Initiating a compensating tracsation.");
            // start compensating transaction
        }


    }
}
