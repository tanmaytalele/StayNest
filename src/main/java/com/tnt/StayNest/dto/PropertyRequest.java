package com.tnt.StayNest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class PropertyRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotBlank(message = "Location is required")
    private String location;
    @NotNull(message = "Price per night is required")
    private Long pricePerNight;
    private String imageUrl;
    @NotNull(message = "Max guests is required")
    private Integer maxGuests;
}
