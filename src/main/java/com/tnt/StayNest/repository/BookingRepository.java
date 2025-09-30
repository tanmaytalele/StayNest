package com.tnt.StayNest.repository;

import com.tnt.StayNest.entity.Booking;
import com.tnt.StayNest.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByPropertyIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long id, BookingStatus bookingStatus, LocalDate endDate, LocalDate startDate);

    List<Booking> findByPropertyId(Long propertyId);

    boolean existsByPropertyIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(Long id, BookingStatus bookingStatus, LocalDate endDate, LocalDate startDate, Long bookingId);

    boolean existsByPropertyIdAndStatusAndStartDateAfter(Long propertyId, BookingStatus status, LocalDate date);

}