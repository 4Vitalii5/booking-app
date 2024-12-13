package mate.academy.repository.booking.specification;

import java.util.Arrays;
import mate.academy.model.Booking;
import mate.academy.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSpecificationProvider implements SpecificationProvider<Booking> {
    private static final String STATUS_FIELD = "status";

    @Override
    public String getKey() {
        return STATUS_FIELD;
    }

    @Override
    public Specification<Booking> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(STATUS_FIELD).in(Arrays.stream(params)
                .toArray());
    }
}
