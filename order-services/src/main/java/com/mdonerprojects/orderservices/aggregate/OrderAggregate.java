package com.mdonerprojects.orderservices.aggregate;

import com.mdonerprojects.core.events.OrderApprovedEvent;
import com.mdonerprojects.core.util.OrderStatus;
import com.mdonerprojects.orderservices.command.ApproveOrderCommand;
import com.mdonerprojects.orderservices.command.CreateOrderCommand;
import com.mdonerprojects.orderservices.event.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {
    @AggregateIdentifier
    public String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    public OrderAggregate() {

    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.userId = orderCreatedEvent.getUserId();
        this.orderId = orderCreatedEvent.getOrderId();
        this.productId = orderCreatedEvent.getProductId();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
        this.quantity = orderCreatedEvent.getQuantity();
    }


    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        //create and publish orderapprovedEvent
        OrderApprovedEvent orderApprovedEvent = OrderApprovedEvent
                .builder()
                .orderId(approveOrderCommand.getOrderId())
                .build();
        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }
}
