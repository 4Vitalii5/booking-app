package org.cyberrealm.tech.repository.booking.specification;

import java.util.Arrays;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserIdSpecificationProvider implements SpecificationProvider<Booking> {
    public static final String USER_FIELD = "user";
    public static final String ID_FIELD = "id";
    public static final String USER_ID_FIELD = "userId";

    @Override
    public String getKey() {
        return USER_ID_FIELD;
    }

    @Override
    public Specification<Booking> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(USER_FIELD).get(ID_FIELD).in(
                Arrays.stream(params)
                        .map(Long::parseLong)
                        .toArray());
    }
}
