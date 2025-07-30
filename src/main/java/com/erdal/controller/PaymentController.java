package com.erdal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erdal.DTO.BookingDTO;
import com.erdal.DTO.UserDTO;
import com.erdal.model.PaymentMethod;
import com.erdal.model.PaymentOrder;
import com.erdal.responseMessages.PaymentLinkResponse;
import com.erdal.service.PaymentService;
import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
	
	private PaymentService paymentService;
	
	
	@PostMapping("/create")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestBody BookingDTO bookingDTO,@RequestParam PaymentMethod paymentMethod) throws StripeException {
		
		UserDTO userDTO=new UserDTO();
		userDTO.setFullName("Erdal Hulagu");
		userDTO.setEmail("erdalhulagu@outlook.com");
		userDTO.setId(1L);
		
		PaymentLinkResponse paymentLinkResponse=paymentService.createOrder(userDTO, bookingDTO, paymentMethod);
		
		return ResponseEntity.ok(paymentLinkResponse);
		
	}
	
	@GetMapping("{paymentOrderId}")
	public ResponseEntity<PaymentOrder> getPaymentOrderById(@PathVariable Long paymentOrderId) {
	
		
		PaymentOrder paymentLinkResponse=paymentService.getpaymentOrderById(paymentOrderId);
		
		return ResponseEntity.ok(paymentLinkResponse);
		
	
	
	
		
	}
	@PatchMapping("/proceed{paymentOrderId}")
	public ResponseEntity<Boolean> proccedpayment(@RequestParam String paymentId ,@RequestParam String paymentLinkId) {
		

		PaymentOrder paymentOrder=paymentService.getpaymentOrderByPaymentLinkId(paymentLinkId);
		Boolean paymentLinkResponse=paymentService.proccedPayment(paymentOrder,paymentId,paymentLinkId);
		
		return ResponseEntity.ok(paymentLinkResponse);
		
		
		
		
		
	}

}
