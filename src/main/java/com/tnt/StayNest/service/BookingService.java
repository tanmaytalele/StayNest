package com.tnt.StayNest.service;

import com.tnt.StayNest.Utils.SecurityUtils;
import com.tnt.StayNest.dto.BookingRequest;
import com.tnt.StayNest.dto.BookingResponse;
import com.tnt.StayNest.dto.UpdateBookingRequest;
import com.tnt.StayNest.entity.Booking;
import com.tnt.StayNest.entity.BookingStatus;
import com.tnt.StayNest.entity.Property;
import com.tnt.StayNest.repository.BookingRepository;
import com.tnt.StayNest.repository.PropertyRepository;
import com.tnt.StayNest.validation.BookingValidator;
import com.tnt.StayNest.validation.PropertyValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final BookingValidator bookingValidator;
    private final PropertyValidator propertyValidator;
    private final SecurityUtils securityUtils;

    public BookingService(BookingRepository bookingRepository, PropertyRepository propertyRepository, BookingValidator bookingValidator, PropertyValidator propertyValidator, SecurityUtils securityUtils) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.bookingValidator = bookingValidator;
        this.propertyValidator = propertyValidator;
        this.securityUtils = securityUtils;
    }

    public BookingResponse createBooking(BookingRequest request) {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("User {} is creating a booking for property {}", loggedInUserId, request.getPropertyId());

        Property property = propertyValidator.validatePropertyExists(request.getPropertyId());
        bookingValidator.validateBookingRequest(property, request);
        log.debug("Booking request validated for user {} on property {}", loggedInUserId, request.getPropertyId());

        Booking booking = Booking.builder()
                .guestId(loggedInUserId)
                .property(property)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .guests(request.getGuests())
                .status(BookingStatus.BOOKED)
                .build();

        bookingRepository.save(booking);
        log.info("Booking {} created successfully for user {}", booking.getId(), loggedInUserId);
        return mapToResponse(booking);
    }

    public BookingResponse getBooking(Long id) {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("User {} is fetching booking {}", loggedInUserId, id);

        Booking booking = bookingValidator.validateBookingExists(id);
        bookingValidator.validateBookingAccess(booking, loggedInUserId);

        log.debug("Booking {} retrieved successfully for user {}", id, loggedInUserId);
        return mapToResponse(booking);
    }

    public void cancelBooking(Long bookingId) {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("User {} is cancelling booking {}", loggedInUserId, bookingId);

        Booking booking = bookingValidator.validateBookingExists(bookingId);
        bookingValidator.validateBookingCancellation(booking, loggedInUserId);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("Booking {} cancelled successfully by user {}", bookingId, loggedInUserId);
    }

    public List<BookingResponse> getBookingsByProperty(Long propertyId) {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("User {} fetching bookings for property {}", loggedInUserId, propertyId);

        Property property = propertyValidator.validatePropertyExists(propertyId);
        propertyValidator.validateHostAccess(property, loggedInUserId);

        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId);
        log.debug("Found {} bookings for property {} by user {}", bookings.size(), propertyId, loggedInUserId);

        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request) {
        String loggedInUserId = securityUtils.getLoggedInUserId();
        log.info("User {} updating booking {}", loggedInUserId, bookingId);

        Booking booking = bookingValidator.validateBookingExists(bookingId);
        bookingValidator.validateBookingAccess(booking, loggedInUserId);

        Property property = booking.getProperty();

        LocalDate newStartDate = request.getStartDate() != null ? request.getStartDate() : booking.getStartDate();
        LocalDate newEndDate   = request.getEndDate()   != null ? request.getEndDate()   : booking.getEndDate();
        Integer newGuests      = request.getGuests()    != null ? request.getGuests()    : booking.getGuests();

        bookingValidator.validateBookingUpdate(property, newStartDate, newEndDate, newGuests, bookingId);
        log.debug("Booking {} update validated for user {}", bookingId, loggedInUserId);

        booking.setStartDate(newStartDate);
        booking.setEndDate(newEndDate);
        booking.setGuests(newGuests);
        bookingRepository.save(booking);

        log.info("Booking {} updated successfully by user {}", bookingId, loggedInUserId);
        return mapToResponse(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getGuestId(),
                booking.getProperty().getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getGuests(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
