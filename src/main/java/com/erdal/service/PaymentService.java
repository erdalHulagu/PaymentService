package com.erdal.service;

import com.erdal.DTO.BookingDTO;
import com.erdal.DTO.UserDTO;
import com.erdal.model.PaymentMethod;
import com.erdal.model.PaymentOrder;
import com.erdal.responseMessages.PaymentLinkResponse;
import com.gocardless.resources.RedirectFlow;

public interface PaymentService {
	
	PaymentLinkResponse createOrder(UserDTO userDTO,BookingDTO bookingDTO,PaymentMethod paymentMethod);
	
	PaymentOrder getpaymentOrderById(Long id);
	
	PaymentOrder getpaymentOrderByPaymentId(String patmentId);
	
	RedirectFlow createGoCardlessPaymentLink(UserDTO userDTO,Long amount,Long paymentOrderId);
	
	String createStripePaymentLink(UserDTO userDTO,Long amount,Long paymentOrderId);
	
	

}
