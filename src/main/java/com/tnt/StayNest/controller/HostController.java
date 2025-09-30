package com.tnt.StayNest.controller;

import com.tnt.StayNest.dto.BookingResponse;
import com.tnt.StayNest.dto.PropertyResponse;
import com.tnt.StayNest.service.BookingService;
import com.tnt.StayNest.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/host")
public class HostController {

    private final PropertyService propertyService;
    private final BookingService bookingService;

    public HostController(PropertyService propertyService, BookingService bookingService) {
        this.propertyService = propertyService;
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasAnyRole('ROLE_HOST')")
    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByHost() {
        List<PropertyResponse> properties = propertyService.getPropertiesByHost();
        return ResponseEntity.ok(properties);
    }

    @PreAuthorize("hasAnyRole('ROLE_HOST')")
    @GetMapping("/properties/{id}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByProperty(@PathVariable Long id) {
        List<BookingResponse> bookings = bookingService.getBookingsByProperty(id);
        return ResponseEntity.ok(bookings);
    }
}
