package com.mdonerprojects.paymentservices.aggregate;


import com.mdonerprojects.core.events.PaymentProcessedEvent;
import com.mdonerprojects.paymentservices.command.ProcessPaymentCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class PaymentAggregate {


    @AggregateIdentifier
    private String orderId;
    private String paymentId;

    public PaymentAggregate() {

    }

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {

        if(processPaymentCommand.getPaymentId() == null || processPaymentCommand.getOrderId() == null){
            throw new IllegalArgumentException("ProcessPaymentCommand payment-order id is null");
        }



        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent
                .builder()
                .paymentId(processPaymentCommand.getPaymentId())
                .orderId(processPaymentCommand.getOrderId())
                .build();
        AggregateLifecycle.apply(paymentProcessedEvent);
    }


    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();

    }

}
