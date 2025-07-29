package com.erdal.exeptions;

public class PaymentBadRequestExeption extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Long id;

	public PaymentBadRequestExeption(String message) {
		super(message);
	}

	public PaymentBadRequestExeption(String message, Long id) {
		super(message);
		this.id = id;

	}
}
