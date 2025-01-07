package org.cyberrealm.tech.repository;

import java.util.Optional;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"booking"})
    Optional<Payment> findBySessionId(String sessionId);

    @EntityGraph(attributePaths = {"booking"})
    Optional<Payment> findByBookingId(Long bookingId);

    boolean existsBySessionId(String sessionId);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.booking.user.id = :id "
            + "AND p.status = 'PENDING' "
            + "AND p.isDeleted = false")
    boolean existsPendingPaymentsByUserId(Long id);

    @Query("SELECT p.booking FROM Payment p WHERE p.sessionId = :sessionId")
    Optional<Booking> findBookingBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p WHERE p.id = :paymentId AND p.booking.user.id = :currentUserId")
    Optional<Payment> findByIdAndUserId(Long paymentId, Long currentUserId);
}
