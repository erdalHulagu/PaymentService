package com.erdal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erdal.model.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long>{
	
	PaymentOrder findByPaymentLinkId(String paymentLinkId);

}
