package com.erdal.responseMessages;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentLinkResponse {

	public PaymentLinkResponse() {

	}

	public PaymentLinkResponse(String url) {
		this.paymentLinkId = url;

	}
	public PaymentLinkResponse(String url,String id) {
		this.paymentLinkId=id;
		this.paymentLinkUrl=url;
	}

	private String paymentLinkUrl;
	private String paymentLinkId;

}
