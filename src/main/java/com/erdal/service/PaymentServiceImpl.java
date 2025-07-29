package com.erdal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.erdal.DTO.BookingDTO;
import com.erdal.DTO.UserDTO;
import com.erdal.model.PaymentMethod;
import com.erdal.model.PaymentOrder;
import com.erdal.repository.PaymentOrderRepository;
import com.erdal.responseMessages.PaymentLinkResponse;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor

public class PaymentServiceImpl implements PaymentService{

	private final PaymentOrderRepository paymentOrderRepository;
	
	@Value("${stripe.api.key}")
	private static String stripeApiKey;
	
	@Value("${stripe.api.secret}")
	private static String stripeSecret;
	
	@Value("${stripe.api.key}")
	private static String goCardlessApiKey;
	
	@Value("${goCardless.api.secret}")
	private static String goCardlessSecret;
	
	@Override
	public PaymentLinkResponse createOrder(UserDTO userDTO, BookingDTO bookingDTO, PaymentMethod paymentMethod) {
		
		Long amount=bookingDTO.getTotalPrice().longValue();
		
		PaymentOrder paymentOrder=new PaymentOrder();
		paymentOrder.setAmount(amount);
		paymentOrder.setPaymentMethod(paymentMethod);
		paymentOrder.setBookingId(bookingDTO.getId());
		paymentOrder.setSaloonId(bookingDTO.getSaloonId());
		
		PaymentOrder savedPaymentOrder=paymentOrderRepository.save(paymentOrder);
		if (paymentMethod.equals(PaymentMethod.DIRECT)) {
			
		}
		
		 return null;
	}

	@Override
	public PaymentOrder getpaymentOrderById(Long id) {
	
		
		
		return null;
	}

	@Override
	public PaymentOrder getpaymentOrderByPaymentId(String patmentId) {
		
		
		return null;
	}

	@Override
	public PaymentLinkResponse createGoCardlessPaymentLink(UserDTO userDTO, Long amount, Long paymentOrderId) {
		
		
		
		return null;
	}

	@Override
	public String createStripePaymentLink(UserDTO userDTO, Long amount, Long paymentOrderId) {
		// TODO Auto-generated method stub
		return null;
	}

}
