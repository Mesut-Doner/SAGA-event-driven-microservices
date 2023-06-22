package com.mdonerprojects.core.events;

import com.mdonerprojects.core.util.OrderStatus;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OrderApprovedEvent {

    private final String orderId;

    private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
