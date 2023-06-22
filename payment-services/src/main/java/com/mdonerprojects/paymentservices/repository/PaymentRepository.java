package com.mdonerprojects.paymentservices.repository;

import com.mdonerprojects.paymentservices.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity,String> {
}
