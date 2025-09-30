package com.tnt.StayNest.service;

import com.tnt.StayNest.Utils.SecurityUtils;
import com.tnt.StayNest.dto.PropertyRequest;
import com.tnt.StayNest.dto.PropertyResponse;
import com.tnt.StayNest.dto.PropertySearchRequest;
import com.tnt.StayNest.dto.UpdatePropertyRequest;
import com.tnt.StayNest.entity.Property;
import com.tnt.StayNest.repository.PropertyRepository;
import com.tnt.StayNest.validation.BookingValidator;
import com.tnt.StayNest.validation.PropertyValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyValidator propertyValidator;
    private final BookingValidator bookingValidator;
    private final SecurityUtils securityUtils;

    public PropertyService(PropertyRepository propertyRepository, PropertyValidator propertyValidator,
                           BookingValidator bookingValidator, SecurityUtils securityUtils) {
        this.propertyRepository = propertyRepository;
        this.propertyValidator = propertyValidator;
        this.bookingValidator = bookingValidator;
        this.securityUtils = securityUtils;
    }

    public Property createProperty(PropertyRequest request) {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("User {} is creating a new property with title '{}'", loggedInUserId, request.getTitle());

        propertyValidator.validatePropertyRequest(request);

        Property property = Property.builder()
                .hostId(loggedInUserId)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .pricePerNight(request.getPricePerNight())
                .imageUrl(request.getImageUrl())
                .maxGuests(request.getMaxGuests())
                .createdAt(LocalDateTime.now())
                .build();

        property = propertyRepository.save(property);
        log.info("Property {} created successfully by user {}", property.getId(), loggedInUserId);

        return property;
    }

    public List<PropertyResponse> searchProperties(PropertySearchRequest request) {
        log.info("Searching properties with criteria: location={}, startDate={}, endDate={}, guests={}, minPrice={}, maxPrice={}",
                request.getLocation(), request.getStartDate(), request.getEndDate(), request.getGuests(),
                request.getMinPrice(), request.getMaxPrice());

        propertyValidator.validateSearchRequest(request);

        List<Property> properties = propertyRepository.searchAvailableProperties(
                request.getLocation(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getStartDate(),
                request.getEndDate(),
                request.getGuests()
        );

        log.debug("Found {} properties matching search criteria", properties.size());

        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PropertyResponse getPropertyById(Long id) {
        log.info("Fetching property with id {}", id);
        Property property = propertyValidator.validatePropertyExists(id);
        return mapToResponse(property);
    }

    public PropertyResponse updateProperty(Long propertyId, UpdatePropertyRequest request) {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("User {} is updating property {}", loggedInUserId, propertyId);

        Property property = propertyValidator.validatePropertyExists(propertyId);
        propertyValidator.validateHostAccess(property, loggedInUserId);
        bookingValidator.validateDoesNotHasFutureBooking(property);

        if (request.getDescription() != null) property.setDescription(request.getDescription());
        if (request.getLocation() != null) property.setLocation(request.getLocation());
        if (request.getPricePerNight() != null) property.setPricePerNight(request.getPricePerNight());
        if (request.getImageUrl() != null) property.setImageUrl(request.getImageUrl());
        if (request.getMaxGuests() != null) property.setMaxGuests(request.getMaxGuests());

        propertyRepository.save(property);
        log.info("Property {} updated successfully by user {}", propertyId, loggedInUserId);

        return mapToResponse(property);
    }

    public List<PropertyResponse> getPropertiesByHost() {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("Fetching properties for host {}", loggedInUserId);

        List<Property> properties = propertyRepository.findByHostId(loggedInUserId);
        log.debug("Found {} properties for host {}", properties.size(), loggedInUserId);

        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PropertyResponse mapToResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getHostId(),
                property.getTitle(),
                property.getDescription(),
                property.getLocation(),
                property.getPricePerNight(),
                property.getImageUrl(),
                property.getMaxGuests(),
                property.getCreatedAt()
        );
    }
}
