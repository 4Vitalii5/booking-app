package mate.academy.repository.booking;

import mate.academy.dto.booking.BookingSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookingSearchParameters searchParameters);
}
