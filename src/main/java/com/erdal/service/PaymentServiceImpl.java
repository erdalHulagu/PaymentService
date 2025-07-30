package com.erdal.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.erdal.DTO.BookingDTO;
import com.erdal.DTO.UserDTO;
import com.erdal.exeptions.PaymentBadRequestExeption;
import com.erdal.exeptions.PaymentErrorMessages;
import com.erdal.exeptions.PaymentNotFoundExeption;
import com.erdal.model.PaymentMethod;
import com.erdal.model.PaymentOrder;
import com.erdal.properties.GoCardlessProperties;
import com.erdal.repository.PaymentOrderRepository;
import com.erdal.responseMessages.PaymentLinkResponse;
import com.erdal.status.PaymentOrderStatus;
import com.gocardless.GoCardlessClient;
import com.gocardless.resources.Payment;
import com.gocardless.resources.RedirectFlow;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentOrderRepository paymentOrderRepository;
	private final GoCardlessProperties goCardlessProperties;

	@Value("${stripe.api.secret}")
	private String stripeSecret;

	private GoCardlessClient goCardlessClient;

	@PostConstruct
	public void init() {
		this.goCardlessClient = GoCardlessClient.newBuilder(goCardlessProperties.getApiKey())
				.withEnvironment(GoCardlessClient.Environment.SANDBOX) // Can be changed to LIVE in prod
				.build();
	}

	@Override
	public PaymentLinkResponse createOrder(UserDTO userDTO, BookingDTO bookingDTO, PaymentMethod paymentMethod)
			throws StripeException {
		Long amount = bookingDTO.getTotalPrice().longValue();

		PaymentOrder paymentOrder = new PaymentOrder();
		paymentOrder.setAmount(amount);
		paymentOrder.setPaymentMethod(paymentMethod);
		paymentOrder.setBookingId(bookingDTO.getId());
		paymentOrder.setSaloonId(bookingDTO.getSaloonId());
		paymentOrder.setUserId(userDTO.getId());

		PaymentOrder savedPaymentOrder = paymentOrderRepository.save(paymentOrder);

		if (paymentMethod.equals(PaymentMethod.GOCARDLESS)) {
			return createGoCardlessPaymentLink(userDTO, savedPaymentOrder.getAmount(), savedPaymentOrder);
		} else if (paymentMethod.equals(PaymentMethod.STRIPE)) {
			return createStripePaymentIntent(userDTO, savedPaymentOrder.getAmount(), savedPaymentOrder);
		}

		throw new PaymentBadRequestExeption(
				String.format(PaymentErrorMessages.PAYMENT_MEHOT_NOT_IN_USE, paymentMethod));
	}

	// GoCardless için redirect flow ile ödeme linki oluştur
	@Override
	public PaymentLinkResponse createGoCardlessPaymentLink(UserDTO userDTO, Long amount, PaymentOrder paymentOrder) {
		try {
			RedirectFlow redirectFlow = goCardlessClient.redirectFlows().create()
					.withDescription("Payment for booking " + paymentOrder.getBookingId())
					.withSessionToken("session_" + UUID.randomUUID())
					.withSuccessRedirectUrl("https://localhost:3000/payment-success/" + paymentOrder.getId()).execute();

			paymentOrder.setPaymentLinkId(redirectFlow.getId());
			paymentOrderRepository.save(paymentOrder);

			PaymentLinkResponse response = new PaymentLinkResponse();
			response.setPaymentLinkUrl(redirectFlow.getRedirectUrl());
			response.setPaymentLinkId(redirectFlow.getId());
			return response;
		} catch (Exception e) {
			throw new RuntimeException("Failed to create GoCardless payment link", e);
		}
	}
//	@Override
//	public PaymentLinkResponse createGoCardlessPaymentLink(UserDTO userDTO, Long amount, PaymentOrder paymentOrder, String screenId) {
//	    try {
//	        // Spring Security'den token al
//	        Object principal = SecurityContextHolder.getContext().getAuthentication();
//	        String sessionToken;
//	        
//	        if (principal instanceof JwtAuthenticationToken jwtAuth) {
//	            sessionToken = jwtAuth.getToken().getTokenValue();
//	        } else {
//	            throw new RuntimeException("Cannot extract token from authentication context");
//	        }
//
//	        RedirectFlow redirectFlow = goCardlessClient.redirectFlows().create()
//	            .withDescription("Payment for booking " + paymentOrder.getBookingId())
//	            .withSessionToken(sessionToken) // buraya Spring Security'den gelen token'ı koyduk
//	            .withSuccessRedirectUrl("https://localhost:3000/payment-success/" + screenId)
//	            .execute();
//
//	        paymentOrder.setPaymentLinkId(redirectFlow.getId());
//	        paymentOrderRepository.save(paymentOrder);
//
//	        PaymentLinkResponse response = new PaymentLinkResponse();
//	        response.setPaymentLinkUrl(redirectFlow.getRedirectUrl());
//	        response.setPaymentLinkId(redirectFlow.getId());
//	        return response;
//	    } catch (Exception e) {
//	        throw new RuntimeException("Failed to create GoCardless payment link", e);
//	    }
//	}

	// Stripe için PaymentIntent oluştur (kart ödemeleri için)
	@Override
	public PaymentLinkResponse createStripePaymentIntent(UserDTO userDTO, Long amount, PaymentOrder paymentOrder)
			throws StripeException {

		Stripe.apiKey = stripeSecret;

		SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl("https://localhost:3000/payment-succes/}")
				.setCancelUrl("https://localhost:3000/payment/cancel")
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(SessionCreateParams.LineItem.builder().setQuantity(1L)
						.setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency("gbp")
								.setUnitAmount(amount * 100) // amount in pence
								.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
										.setName("saloon apointment booking" + paymentOrder.getBookingId()).build())
								.build())
						.build())
				.build();

		Session session = Session.create(params);

		return new PaymentLinkResponse(session.getUrl());

	}

	@Override
	public PaymentOrder getpaymentOrderById(Long id) {

		return findPaymentOrderById(id);
	}

	public PaymentOrder findPaymentOrderById(Long id) {

		return paymentOrderRepository.findById(id)
				.orElseThrow(() -> new PaymentNotFoundExeption(PaymentErrorMessages.PAYMENT_MEHOT_NOT_IN_USE));
	}

	@Override
	public PaymentOrder getpaymentOrderByPaymentLinkId(String patmentLinkId) {

		return paymentOrderRepository.findByPaymentLinkId(patmentLinkId);
	}

	@Override
	public Boolean proccedPayment(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) {
	    if (paymentOrder.getPaymentStatus().equals(PaymentOrderStatus.PENDING)) {
	        try {
	            if (paymentOrder.getPaymentMethod().equals(PaymentMethod.GOCARDLESS)) {
	                // GoCardless payment'ı getir
	                Payment payment = goCardlessClient.payments().get(paymentId).execute();

	                if ("confirmed".equals(payment.getStatus()) || "paid_out".equals(payment.getStatus())) {
	                    paymentOrder.setPaymentStatus(PaymentOrderStatus.SUCCES); // enum'daki SUCCES
	                    paymentOrder.setPaymentLinkId(paymentLinkId);
	                    paymentOrderRepository.save(paymentOrder);
	                    return true;
	                } else if ("failed".equals(payment.getStatus()) || "cancelled".equals(payment.getStatus())) {
	                    paymentOrder.setPaymentStatus(PaymentOrderStatus.CANCELLED);
	                    paymentOrderRepository.save(paymentOrder);
	                    return false;
	                }
	            } else if (paymentOrder.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
	                // Stripe checkout session'ı getir
	                Session session = Session.retrieve(paymentId);

	                if ("complete".equals(session.getStatus())) {
	                    paymentOrder.setPaymentStatus(PaymentOrderStatus.SUCCES); // enum'daki SUCCES
	                    paymentOrder.setPaymentLinkId(paymentLinkId);
	                    paymentOrderRepository.save(paymentOrder);
	                    return true;
	                } else if ("expired".equals(session.getStatus()) || "canceled".equals(session.getStatus())) {
	                    paymentOrder.setPaymentStatus(PaymentOrderStatus.CANCELLED);
	                    paymentOrderRepository.save(paymentOrder);
	                    return false;
	                }
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("Failed to proceed payment verification", e);
	        }
	    }
	    return false;
	}

}
