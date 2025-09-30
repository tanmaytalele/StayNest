package com.tnt.StayNest.validation;

import com.tnt.StayNest.dto.BookingRequest;
import com.tnt.StayNest.entity.Booking;
import com.tnt.StayNest.entity.BookingStatus;
import com.tnt.StayNest.entity.Property;
import com.tnt.StayNest.exception.AccessDeniedException;
import com.tnt.StayNest.exception.DateUnavailableException;
import com.tnt.StayNest.exception.GuestLimitExceededException;
import com.tnt.StayNest.exception.InvalidPropertyValueException;
import com.tnt.StayNest.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class BookingValidator {

    private final BookingRepository bookingRepository;
    private final PropertyValidator propertyValidator;

    public BookingValidator(BookingRepository bookingRepository, PropertyValidator propertyValidator) {
        this.bookingRepository = bookingRepository;
        this.propertyValidator = propertyValidator;
    }

    public void validateBookingRequest(Property property, BookingRequest request) {
        log.info("Validating booking request for property {} with {} guests from {} to {}",
                property != null ? property.getId() : "null",
                request.getGuests(),
                request.getStartDate(),
                request.getEndDate());

        if (property == null) {
            log.error("Booking validation failed: Property does not exist");
            throw new InvalidPropertyValueException("Property does not exist");
        }

        if (request.getGuests() != null && request.getGuests() < 1) {
            log.error("Booking validation failed: Guests must be at least 1");
            throw new InvalidPropertyValueException("Guests must be at least 1");
        }

        if (request.getGuests() > property.getMaxGuests()) {
            log.error("Booking validation failed: Guest limit exceeded. Max allowed: {}", property.getMaxGuests());
            throw new GuestLimitExceededException();
        }

        LocalDate today = LocalDate.now();
        if (request.getStartDate().isBefore(today) || request.getEndDate().isBefore(today)) {
            log.error("Booking validation failed: Dates cannot be in the past");
            throw new InvalidPropertyValueException("Booking dates cannot be in the past");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            log.error("Booking validation failed: Start date {} is after end date {}", request.getStartDate(), request.getEndDate());
            throw new InvalidPropertyValueException("Start date cannot be after end date");
        }

        boolean isBooked = bookingRepository.existsByPropertyIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                property.getId(),
                BookingStatus.BOOKED,
                request.getEndDate(),
                request.getStartDate()
        );

        if (isBooked) {
            log.error("Booking validation failed: Dates overlap with existing booking");
            throw new DateUnavailableException();
        }

        log.debug("Booking request validated successfully for property {}", property.getId());
    }

    public Booking validateBookingExists(Long bookingId) {
        log.info("Validating existence of booking {}", bookingId);
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking not found with id: {}", bookingId);
                    return new InvalidPropertyValueException("Booking not found with id: " + bookingId);
                });
    }

    public void validateBookingAccess(Booking booking, String userId) {
        log.info("Validating access for user {} to booking {}", userId, booking.getId());
        String guestId = booking.getGuestId();
        if (!userId.equals(guestId)) {
            log.error("Access denied for user {} on booking {}", userId, booking.getId());
            throw new AccessDeniedException();
        }
    }

    public void validateBookingCancellation(Booking booking, String userId) {
        log.info("Validating cancellation for booking {} by user {}", booking != null ? booking.getId() : "null", userId);

        if (booking == null) {
            log.error("Cancellation failed: Booking does not exist");
            throw new InvalidPropertyValueException("Booking does not exist");
        }

        validateBookingAccess(booking, userId);

        if (!booking.getStartDate().isAfter(LocalDate.now())) {
            log.error("Cancellation failed: Booking {} cannot be cancelled as it is past or ongoing", booking.getId());
            throw new InvalidPropertyValueException("Cannot cancel past or ongoing bookings");
        }

        if (booking.getStatus() != BookingStatus.BOOKED) {
            log.error("Cancellation failed: Booking {} is not in BOOKED status", booking.getId());
            throw new InvalidPropertyValueException("Booking cannot be cancelled");
        }
    }

    public void validateBookingUpdate(Property property, LocalDate startDate, LocalDate endDate, Integer guests, Long bookingId) {
        log.info("Validating booking update for booking {} on property {} with {} guests from {} to {}",
                bookingId,
                property.getId(),
                guests,
                startDate,
                endDate);

        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today) || endDate.isBefore(today)) {
            log.error("Booking update validation failed: Dates cannot be in the past");
            throw new InvalidPropertyValueException("Booking dates cannot be in the past");
        }

        if (startDate.isAfter(endDate)) {
            log.error("Booking update validation failed: Start date {} is after end date {}", startDate, endDate);
            throw new InvalidPropertyValueException("Start date cannot be after end date");
        }

        if (guests > property.getMaxGuests()) {
            log.error("Booking update validation failed: Guest limit exceeded. Max allowed: {}", property.getMaxGuests());
            throw new GuestLimitExceededException();
        }

        boolean conflict = bookingRepository.existsByPropertyIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(
                property.getId(),
                BookingStatus.BOOKED,
                endDate,
                startDate,
                bookingId
        );

        if (conflict) {
            log.error("Booking update validation failed: Dates overlap with existing booking");
            throw new DateUnavailableException();
        }

        log.debug("Booking update validated successfully for booking {}", bookingId);
    }

    public void validateDoesNotHasFutureBooking(Property property){
        log.info("Validating future bookings for property {}", property.getId());
        boolean hasFutureBookings = bookingRepository.existsByPropertyIdAndStatusAndStartDateAfter(
                property.getId(),
                BookingStatus.BOOKED,
                LocalDate.now()
        );
        if (hasFutureBookings) {
            log.error("Property {} cannot be updated due to future bookings", property.getId());
            throw new InvalidPropertyValueException("Cannot update property with future bookings");
        }
        log.debug("No future bookings found for property {}", property.getId());
    }
}
