package com.erdal.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.erdal.DTO.BookingDTO;
import com.erdal.DTO.UserDTO;
import com.erdal.exeptions.PaymentBadRequestExeption;
import com.erdal.exeptions.PaymentErrorMessages;
import com.erdal.model.PaymentMethod;
import com.erdal.model.PaymentOrder;
import com.erdal.properties.GoCardlessProperties;
import com.erdal.repository.PaymentOrderRepository;
import com.erdal.responseMessages.PaymentLinkResponse;
import com.gocardless.GoCardlessClient;
import com.gocardless.resources.RedirectFlow;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentIntentCreateParams;

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
    public PaymentLinkResponse createOrder(UserDTO userDTO, BookingDTO bookingDTO, PaymentMethod paymentMethod) {
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

        throw new PaymentBadRequestExeption(String.format(PaymentErrorMessages.PAYMENT_MEHOT_NOT_IN_USE, paymentMethod));
    }

    // GoCardless için redirect flow ile ödeme linki oluştur
    public PaymentLinkResponse createGoCardlessPaymentLink(UserDTO userDTO, Long amount, PaymentOrder paymentOrder) {
        try {
            RedirectFlow redirectFlow = goCardlessClient.redirectFlows().create()
                    .withDescription("Payment for booking " + paymentOrder.getBookingId())
                    .withSessionToken("session_" + UUID.randomUUID())
                    .withSuccessRedirectUrl("https://yourdomain.com/payment-success")
                    .execute();

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

    // Stripe için PaymentIntent oluştur (kart ödemeleri için)
    public PaymentLinkResponse createStripePaymentIntent(UserDTO userDTO, Long amount, PaymentOrder paymentOrder) {
        try {
            Stripe.apiKey = stripeSecret;

            // Stripe API expects amount in the smallest currency unit, e.g. pence for GBP
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount) // amount in pence
                    .setCurrency("gbp")
                    .addPaymentMethodType("card")
                    .setDescription("Booking #" + paymentOrder.getBookingId())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Burada paymentIntent id'yi kaydedebilirsin (isteğe bağlı)
            paymentOrder.setPaymentLinkId(paymentIntent.getId());
            paymentOrderRepository.save(paymentOrder);

            PaymentLinkResponse response = new PaymentLinkResponse();
            response.setPaymentLinkUrl(paymentIntent.getClientSecret()); // Ödeme için client secret döner
            response.setPaymentLinkId(paymentIntent.getId());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe payment intent", e);
        }
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
	public RedirectFlow createGoCardlessPaymentLink(UserDTO userDTO, Long amount, Long paymentOrderId) {
		
		
		
		return null;
	}

	@Override
	public String createStripePaymentLink(UserDTO userDTO, Long amount, Long paymentOrderId) {
		// TODO Auto-generated method stub
		return null;
	}

}
