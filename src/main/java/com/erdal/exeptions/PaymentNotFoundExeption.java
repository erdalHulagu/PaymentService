package com.erdal.exeptions;

public class PaymentNotFoundExeption extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Long id;

	public PaymentNotFoundExeption(String message) {
		super(message);
	}

	public PaymentNotFoundExeption(String message, Long id) {
		super(message);
		this.id = id;

	}
}