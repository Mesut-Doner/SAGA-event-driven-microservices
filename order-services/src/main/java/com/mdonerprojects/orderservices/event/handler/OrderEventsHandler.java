package com.mdonerprojects.orderservices.event.handler;


import com.mdonerprojects.core.events.OrderApprovedEvent;
import com.mdonerprojects.orderservices.event.OrderCreatedEvent;
import com.mdonerprojects.orderservices.model.OrderEntity;
import com.mdonerprojects.orderservices.repository.OrderRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("order-group")
public class OrderEventsHandler {


    private final OrderRepository orderRepository;

    public OrderEventsHandler(@Autowired OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) {
        // sadece olduğu handler classtaki exceptionu yakalar
        exception.printStackTrace();
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        exception.printStackTrace();
    }


    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreatedEvent, orderEntity);

        orderRepository.save(orderEntity);

    }
    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {

        OrderEntity orderEntity = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());

        if(orderEntity == null){
            //log
            return;
        }

        orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
        orderRepository.save(orderEntity);

    }
}
