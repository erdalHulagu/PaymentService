package com.erdal.DTO;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import lombok.Data;

@Data
public class BookingDTO {
	
	private Long id;

	private Long saloonId;

	private Long customerId;

	private Set<Long> serviceOfferinIds;

//	private BookingStatus bookingStatus;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Integer totalPrice;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingDTO)) return false;
        BookingDTO that = (BookingDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
