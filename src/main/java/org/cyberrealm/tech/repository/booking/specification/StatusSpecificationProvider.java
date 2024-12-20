package org.cyberrealm.tech.repository.booking.specification;

import java.util.Arrays;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSpecificationProvider implements SpecificationProvider<Booking> {
    public static final String STATUS_FIELD = "status";

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
