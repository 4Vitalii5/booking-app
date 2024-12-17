package mate.academy.repository.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.model.Booking;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @NotNull
    @EntityGraph(attributePaths = {"accommodation"})
    Optional<Booking> findById(@NotNull Long id);

    @EntityGraph(attributePaths = {"user"})
    List<Booking> findAllByUserId(Long userId);

    List<Booking> findByUserId(Long id, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    List<Booking> findAll(Specification<Booking> bookingSpecification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.accommodation.id = ?1 "
            + "AND b.checkOutDate > ?2 AND b.checkInDate < ?3")
    int countOverlappingBookings(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate);

    List<Booking> findAllByCheckOutDateBeforeAndStatusNot(LocalDate checkOutDate,
                                                          Booking.BookingStatus status);

    @Query("SELECT DATEDIFF(day, b.checkInDate, b.checkOutDate) AS numberOfDays "
            + "FROM Booking b "
            + "WHERE b.id = ?1")
    int numberOfDays(Long bookingId);
}
