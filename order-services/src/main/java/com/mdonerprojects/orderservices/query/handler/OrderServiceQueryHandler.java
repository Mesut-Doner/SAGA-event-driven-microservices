package com.mdonerprojects.orderservices.query.handler;

import com.mdonerprojects.orderservices.model.OrderEntity;
import com.mdonerprojects.orderservices.model.OrderSummary;
import com.mdonerprojects.orderservices.query.FindOrderQuery;
import com.mdonerprojects.orderservices.repository.OrderRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceQueryHandler {

    private final OrderRepository orderRepository;

    public OrderServiceQueryHandler(@Autowired OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery){
        OrderEntity orderEntity =orderRepository
                .findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummary(orderEntity.orderId,orderEntity.getOrderStatus(),"");
    }
}
