package mate.academy.repository.booking;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingSearchParameters;
import mate.academy.model.Booking;
import mate.academy.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingSpecificationBuilder implements SpecificationBuilder<Booking> {
    private static final String STATUS_FIELD = "status";
    private static final String USER_ID_FIELD = "userId";
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
