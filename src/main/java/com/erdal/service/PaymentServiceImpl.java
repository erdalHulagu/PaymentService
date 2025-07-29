package com.erdal.service;

import org.springframework.stereotype.Service;

import com.erdal.DTO.BookingDTO;
import com.erdal.DTO.UserDTO;
import com.erdal.model.PaymentMethod;
import com.erdal.model.PaymentOrder;
import com.erdal.repository.PaymentRepository;
import com.erdal.responseMessages.PaymentLinkResponse;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor

public class PaymentServiceImpl implements PaymentService{

	private final PaymentRepository paymentRepository;
	
	@Override
	public PaymentLinkResponse createOrder(UserDTO userDTO, BookingDTO bookingDTO, PaymentMethod paymentMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentOrder getpaymentOrderById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentOrder getpaymentOrderByPaymentId(String patmentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentLinkResponse createGoCardlessPaymentLink(UserDTO userDTO, Long amount, Long paymentOrderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createStripePaymentLink(UserDTO userDTO, Long amount, Long paymentOrderId) {
		// TODO Auto-generated method stub
		return null;
	}

}
