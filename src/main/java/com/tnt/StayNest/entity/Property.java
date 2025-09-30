package com.tnt.StayNest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hostId;

    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    private Long pricePerNight;

    private String imageUrl;

    private Integer maxGuests;

    private LocalDateTime createdAt;
}
