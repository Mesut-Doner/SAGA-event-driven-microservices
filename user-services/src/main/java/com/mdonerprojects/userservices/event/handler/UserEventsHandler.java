package com.mdonerprojects.userservices.event.handler;


import com.mdonerprojects.core.common.PaymentDetails;
import com.mdonerprojects.core.model.UserObj;
import com.mdonerprojects.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {


    @QueryHandler
    public void handle(FetchUserPaymentDetailsQuery query){

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("MDONER")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        UserObj userRest = UserObj.builder()
                .firstName("mesut")
                .lastName("doner")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();

    }

}
