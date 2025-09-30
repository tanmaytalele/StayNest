package com.tnt.StayNest.validation;

import com.tnt.StayNest.dto.PropertySearchRequest;
import com.tnt.StayNest.dto.PropertyRequest;
import com.tnt.StayNest.dto.UpdatePropertyRequest;
import com.tnt.StayNest.entity.Property;
import com.tnt.StayNest.exception.AccessDeniedException;
import com.tnt.StayNest.exception.InvalidPropertyValueException;
import com.tnt.StayNest.exception.InvalidSearchRequestException;
import com.tnt.StayNest.repository.PropertyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PropertyValidator {

    private final PropertyRepository propertyRepository;

    public PropertyValidator(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public void validatePropertyRequest(PropertyRequest request) {
        log.info("Validating property request: title={}, location={}, pricePerNight={}, maxGuests={}",
                request.getTitle(), request.getLocation(), request.getPricePerNight(), request.getMaxGuests());

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            log.error("Property validation failed: Title is required");
            throw new InvalidPropertyValueException("Title is required");
        }
        if (request.getLocation() == null || request.getLocation().isBlank()) {
            log.error("Property validation failed: Location is required");
            throw new InvalidPropertyValueException("Location is required");
        }
        if (request.getPricePerNight() < 0) {
            log.error("Property validation failed: Price per night {} is invalid", request.getPricePerNight());
            throw new InvalidPropertyValueException("Price per night must be >= 0");
        }
        if (request.getMaxGuests() < 1) {
            log.error("Property validation failed: Max guests {} is invalid", request.getMaxGuests());
            throw new InvalidPropertyValueException("Max guests must be >= 1");
        }

        log.debug("Property request validated successfully");
    }

    public void validateSearchRequest(PropertySearchRequest request) {
        log.info("Validating property search request: startDate={}, endDate={}, guests={}, minPrice={}, maxPrice={}",
                request.getStartDate(), request.getEndDate(), request.getGuests(), request.getMinPrice(), request.getMaxPrice());

        if (request.getStartDate() != null && request.getEndDate() != null &&
                request.getStartDate().isAfter(request.getEndDate())) {
            log.error("Search validation failed: Start date {} is after end date {}", request.getStartDate(), request.getEndDate());
            throw new InvalidSearchRequestException("INVALID_DATE_RANGE", "Start date cannot be after end date");
        }
        if (request.getGuests() != null && request.getGuests() < 1) {
            log.error("Search validation failed: Guests {} is invalid", request.getGuests());
            throw new InvalidPropertyValueException("Guests must be at least 1");
        }
        if ((request.getMinPrice() != null && request.getMinPrice() < 0) ||
                (request.getMaxPrice() != null && request.getMaxPrice() < 0)) {
            log.error("Search validation failed: Price range invalid (min={}, max={})", request.getMinPrice(), request.getMaxPrice());
            throw new InvalidPropertyValueException("Price must be >= 0");
        }
        if (request.getMinPrice() != null && request.getMaxPrice() != null &&
                request.getMinPrice() > request.getMaxPrice()) {
            log.error("Search validation failed: Min price {} exceeds max price {}", request.getMinPrice(), request.getMaxPrice());
            throw new InvalidSearchRequestException("INVALID_PRICE_RANGE", "Minimum price cannot exceed maximum price");
        }

        log.debug("Property search request validated successfully");
    }

    public Property validatePropertyExists(Long propertyId) {
        log.info("Validating existence of property {}", propertyId);
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    log.error("Property not found with id: {}", propertyId);
                    return new InvalidPropertyValueException("property not found with id: " + propertyId);
                });
    }

    public void validateHostAccess(Property property, String hostId) {
        log.info("Validating host access for host {} on property {}", hostId, property.getId());
        if (!property.getHostId().equals(hostId)) {
            log.error("Access denied for host {} on property {}", hostId, property.getId());
            throw new AccessDeniedException();
        }
        log.debug("Host {} validated for property {}", hostId, property.getId());
    }

    public void validateUpdatePropertyRequest(UpdatePropertyRequest request, Property property, String userId) {
        log.info("Validating update request for property {} by user {}", property.getId(), userId);

        validateHostAccess(property, userId);

        if (request.getPricePerNight() != null && request.getPricePerNight() < 0) {
            log.error("Update validation failed: Price per night {} is invalid", request.getPricePerNight());
            throw new InvalidPropertyValueException("Price per night must be >= 0");
        }
        if (request.getMaxGuests() != null && request.getMaxGuests() < 1) {
            log.error("Update validation failed: Max guests {} is invalid", request.getMaxGuests());
            throw new InvalidPropertyValueException("Max guests must be >= 1");
        }

        log.debug("Property update request validated successfully for property {}", property.getId());
    }
}
