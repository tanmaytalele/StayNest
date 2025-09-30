package com.tnt.StayNest.repository;

import com.tnt.StayNest.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("""
    SELECT DISTINCT p
    FROM Property p
    LEFT JOIN Booking b
        ON b.property = p
        AND b.status = 'BOOKED'
        AND b.startDate <= :endDate
        AND b.endDate >= :startDate
    WHERE (:location IS NULL OR p.location = :location)
      AND (:minPrice IS NULL OR p.pricePerNight >= :minPrice)
      AND (:maxPrice IS NULL OR p.pricePerNight <= :maxPrice)
      AND (:guests IS NULL OR p.maxGuests >= :guests)
      AND b.id IS NULL
""")
    List<Property> searchAvailableProperties(
            @Param("location") String location,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("guests") Integer guests
    );

    List<Property> findByHostId(String hostId);

}