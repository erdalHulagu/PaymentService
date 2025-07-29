package com.erdal.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaloonDTO {
	
	
	private Long id;
	
	
	private String saloonName;
	
	
	private List<String> images;
	

	private String address;
	
	
	private String phoneNumber;
	
	
	private String email;
	
	
	private String city;
	
	
	private Long ownerId;
	
	
	private LocalTime openTime;
	
	
	private LocalTime closeTime;
	
	

}
