package com.erdal.requests;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class PaymentRequest {

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Set<Long> serviceOfferingId;

}
