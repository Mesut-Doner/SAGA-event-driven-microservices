package com.mdonerprojects.orderservices.event;

import com.mdonerprojects.core.util.OrderStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class OrderRejectedEvent {
    @TargetAggregateIdentifier
    public final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
