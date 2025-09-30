package com.tnt.StayNest.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateBookingRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer guests;
}
