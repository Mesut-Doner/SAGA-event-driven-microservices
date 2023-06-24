package com.mdonerprojects.orderservices.saga;


import com.mdonerprojects.core.ProcessPaymentCommand;
import com.mdonerprojects.core.commands.CancelProductReservationCommand;
import com.mdonerprojects.core.commands.ReserveProductCommand;
import com.mdonerprojects.core.events.OrderApprovedEvent;
import com.mdonerprojects.core.events.PaymentProcessedEvent;
import com.mdonerprojects.core.events.ProductReservationCancelledEvent;
import com.mdonerprojects.core.events.ProductReservedEvent;
import com.mdonerprojects.core.model.UserObj;
import com.mdonerprojects.core.query.FetchUserPaymentDetailsQuery;
import com.mdonerprojects.orderservices.command.ApproveOrderCommand;
import com.mdonerprojects.orderservices.command.RejectOrderCommand;
import com.mdonerprojects.orderservices.event.OrderCreatedEvent;
import com.mdonerprojects.orderservices.event.OrderRejectedEvent;
import com.mdonerprojects.orderservices.model.OrderSummary;
import com.mdonerprojects.orderservices.query.FindOrderQuery;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private String scheduleId;

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
            cancelProductResevartion(productReservedEvent, e.getMessage());
            return;

        }

        if (user == null) {
            // start compensating transaction
            cancelProductResevartion(productReservedEvent, "User bilgileri alınamadı.");
            return;

        }

        LOGGER.info("Successfully fetched user payment details for user:" + user.getUserId());

        scheduleId = deadlineManager.schedule(Duration.of(10, ChronoUnit.SECONDS),
                "payment-processing-deadline", productReservedEvent);

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
            cancelProductResevartion(productReservedEvent, e.getMessage());
            return;
        }
        if (result == null) {
            LOGGER.info("ProcessPaymentCommand result is null. Initiating a compensating tracsation.");
            // start compensating transaction
        }
        cancelProductResevartion(productReservedEvent, "ProcessPaymentCommand result is null.");

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {

        cancelDeadlines();
        // send approveordercommand
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        try {
            commandGateway.send(approveOrderCommand);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void cancelDeadlines() {
        if (scheduleId == null) {
            return;
        }
        deadlineManager.cancelSchedule("payment-processing-deadline", scheduleId);
        scheduleId = null;
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved. Order saga is completed for orderId:" + orderApprovedEvent.getOrderId());
        //SagaLifecycle.end();
        queryUpdateEmitter.emit(FindOrderQuery.class, findOrderQuery -> true,
                new OrderSummary(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus(), ""));

    }

    private void cancelProductResevartion(ProductReservedEvent productReservedEvent, String reason) {
        cancelDeadlines();
        CancelProductReservationCommand cancelProductReservationCommand
                = CancelProductReservationCommand
                .builder()
                .reason(reason)
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .build();

        commandGateway.send(cancelProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // create and send a RejectOrderCommand
        RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
                .orderId(productReservationCancelledEvent.getOrderId())
                .reason(productReservationCancelledEvent.getReason()).build();

        try {
            commandGateway.send(rejectOrderCommand);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOGGER.info("Successfully Rejected Order:" + orderRejectedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, findOrderQuery -> true,
                new OrderSummary(orderRejectedEvent.getOrderId(), orderRejectedEvent.getOrderStatus(), orderRejectedEvent.getReason()));

    }

    @DeadlineHandler(deadlineName = "payment-processing-deadline")
    public void handleDeadline(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Payment processing deadline took place");
        cancelProductResevartion(productReservedEvent, "Payment timeout");
    }
}
