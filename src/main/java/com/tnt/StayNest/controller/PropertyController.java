package com.tnt.StayNest.controller;

import com.tnt.StayNest.dto.*;
import com.tnt.StayNest.entity.Property;
import com.tnt.StayNest.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PreAuthorize("hasAnyRole('ROLE_HOST')")
    @PostMapping
    public ResponseEntity<Property> createProperty(@Valid @RequestBody PropertyRequest request) {
        Property property = propertyService.createProperty(request);
        return new ResponseEntity<>(property, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> searchProperties(@ModelAttribute PropertySearchRequest request) {
        List<PropertyResponse> properties = propertyService.searchProperties(request);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        PropertyResponse property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @PreAuthorize("hasAnyRole('ROLE_HOST')")
    @PutMapping("{id}")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long id, @RequestBody @Valid UpdatePropertyRequest request) {
        PropertyResponse response = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(response);
    }


}
