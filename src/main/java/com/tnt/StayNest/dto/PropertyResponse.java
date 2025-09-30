package com.tnt.StayNest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PropertyResponse {
    private Long id;
    private String hostId;
    private String title;
    private String description;
    private String location;
    private Long pricePerNight;
    private String imageUrl;
    private Integer maxGuests;
    private LocalDateTime createdAt;
}
