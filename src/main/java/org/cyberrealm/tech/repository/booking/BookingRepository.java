package org.cyberrealm.tech.repository.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"accommodation"})
    Optional<Booking> findById(Long id);

    @EntityGraph(attributePaths = {"user"})
    List<Booking> findAllByUserId(Long userId);

    List<Booking> findByUserId(Long id, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Booking> findAll(Specification<Booking> bookingSpecification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.accommodation.id = :accommodationId "
            + "AND b.checkOutDate > :checkInDate AND b.checkInDate < :checkOutDate")
    int countOverlappingBookings(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate);

    List<Booking> findAllByCheckOutDateBeforeAndStatusNot(LocalDate checkOutDate,
                                                          Booking.BookingStatus status);

    @Query("SELECT DATEDIFF(day, b.checkInDate, b.checkOutDate) AS numberOfDays "
            + "FROM Booking b "
            + "WHERE b.id = :bookingId")
    int numberOfDays(Long bookingId);
}
