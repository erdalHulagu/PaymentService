package com.erdal.service;

import com.erdal.DTO.BookingDTO;
import com.erdal.DTO.UserDTO;
import com.erdal.model.PaymentMethod;
import com.erdal.model.PaymentOrder;
import com.erdal.responseMessages.PaymentLinkResponse;
import com.gocardless.resources.RedirectFlow;
import com.stripe.exception.StripeException;

public interface PaymentService {
	
	PaymentLinkResponse createOrder(UserDTO userDTO,BookingDTO bookingDTO,PaymentMethod paymentMethod)throws StripeException;
	
	PaymentOrder getpaymentOrderById(Long paymentId);
	
	PaymentOrder getpaymentOrderByPaymentLinkId(String patmentId);
	
	PaymentLinkResponse createGoCardlessPaymentLink(UserDTO userDTO, Long amount, PaymentOrder paymentOrder);
	
	PaymentLinkResponse createStripePaymentIntent(UserDTO userDTO, Long amount, PaymentOrder paymentOrder)throws StripeException;
	
	Boolean proccedPayment(PaymentOrder paymentOrder,String paymentId, String paymentLinkId);

}
