package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"booking"})
    Optional<Payment> findBySessionId(String sessionId);

    @EntityGraph(attributePaths = {"booking"})
    Optional<Payment> findByBookingId(Long bookingId);
}
