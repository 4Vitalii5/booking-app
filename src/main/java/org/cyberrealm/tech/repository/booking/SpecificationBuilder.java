package org.cyberrealm.tech.repository.booking;

import org.cyberrealm.tech.dto.booking.BookingSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookingSearchParameters searchParameters);
}
