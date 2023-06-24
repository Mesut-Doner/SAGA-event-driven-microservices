package com.mdonerprojects.orderservices.command;

import com.mdonerprojects.core.util.OrderStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Data
@Builder
public class RejectOrderCommand {
    @TargetAggregateIdentifier
    public final String orderId;
    private final String reason;
}
