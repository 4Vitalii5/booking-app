package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @EntityGraph(attributePaths = {"address", "amenities"})
    Page<Accommodation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"address", "amenities"})
    Optional<Accommodation> findById(Long id);
}
