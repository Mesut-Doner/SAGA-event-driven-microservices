package com.mdonerprojects.userservices.model;


import com.mdonerprojects.core.common.PaymentDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserObj {
    private final String firstName;
    private final String lastName;
    private final String userId;
    private final PaymentDetails paymentDetails;
}
