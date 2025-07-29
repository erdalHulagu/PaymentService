package com.erdal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erdal.model.PaymentOrder;

public interface PaymentRepository extends JpaRepository<PaymentOrder, Long>{
	
	

}
