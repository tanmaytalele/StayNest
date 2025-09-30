package com.tnt.StayNest.dto;

import com.tnt.StayNest.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String guestId;
    private Long propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer guests;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
