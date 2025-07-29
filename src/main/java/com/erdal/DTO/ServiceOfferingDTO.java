package com.erdal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOfferingDTO {
	
	private Long id;
	
	private String offeringName;
	
	private String description;
	
	private Integer price;
	
	private Integer duration;
	
	private Long saloonId;
	
	private Long categoryId;
	
	private String offeringImage;

}
