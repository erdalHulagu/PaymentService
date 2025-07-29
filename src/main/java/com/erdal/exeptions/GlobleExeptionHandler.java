package com.erdal.exeptions;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.erdal.exeptions.response.PaymentExeptionResponse;

@ControllerAdvice
public class GlobleExeptionHandler {
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<PaymentExeptionResponse> exeptionHandler(Exception ex, WebRequest req){ 
		
		PaymentExeptionResponse exeptionResponse= new PaymentExeptionResponse(ex.getMessage(),req.getDescription(false),LocalDateTime.now());
		
	return	ResponseEntity.ok(exeptionResponse);
		
	}

}
