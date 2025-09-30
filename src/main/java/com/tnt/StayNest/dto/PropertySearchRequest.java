package com.tnt.StayNest.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PropertySearchRequest {
    private String location;
    private Long minPrice;
    private Long maxPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer guests;
}
