package com.tnt.StayNest.dto;

import lombok.Data;

@Data
public class UpdatePropertyRequest {
    private String description;
    private String location;
    private Long pricePerNight;
    private String imageUrl;
    private Integer maxGuests;
}
