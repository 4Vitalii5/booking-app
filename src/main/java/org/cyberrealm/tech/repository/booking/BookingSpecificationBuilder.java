package org.cyberrealm.tech.repository.booking;

import static org.cyberrealm.tech.repository.booking.specification.StatusSpecificationProvider.STATUS_FIELD;
import static org.cyberrealm.tech.repository.booking.specification.UserIdSpecificationProvider.USER_ID_FIELD;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.booking.BookingSearchParameters;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingSpecificationBuilder implements SpecificationBuilder<Booking> {
    private final SpecificationProviderManager<Booking> specificationProviderManager;

    @Override
    public Specification<Booking> build(BookingSearchParameters searchParameters) {
        Specification<Booking> spec = Specification.where(null);
        if (searchParameters.status() != null && searchParameters.status().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(STATUS_FIELD)
                    .getSpecification(searchParameters.status()));
        }
        if (searchParameters.userId() != null && searchParameters.userId().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(USER_ID_FIELD)
                    .getSpecification(searchParameters.userId()));
        }
        return spec;
    }
}
