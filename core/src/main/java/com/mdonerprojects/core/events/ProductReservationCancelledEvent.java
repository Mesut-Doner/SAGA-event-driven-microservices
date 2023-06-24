package com.mdonerprojects.core.events;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Builder
@Data
public class ProductReservationCancelledEvent {

    @AggregateIdentifier
    private final String productId;
    private final Integer  quantity;

    private final String orderId;

    private final String userId;
    private final String  reason;
}
