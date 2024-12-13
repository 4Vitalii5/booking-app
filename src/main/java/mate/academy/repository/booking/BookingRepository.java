package mate.academy.repository.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"accommodation"})
    Optional<Booking> findById(Long id);

    boolean existsByCheckInDateAndCheckOutDateAndAccommodationId(
            LocalDate checkInDate, LocalDate checkOutDate, Long accommodationId
    );

    List<Booking> findByUserId(Long id, Pageable pageable);

    List<Booking> findAll(Specification<Booking> bookingSpecification, Pageable pageable);
}