package org.cyberrealm.tech.dto.accommodation;

import java.math.BigDecimal;
import java.util.List;

public record AccommodationDto(
        Long id,
        String type,
        Long addressId,
        String size,
        List<String> amenities,
        BigDecimal dailyRate,
        Integer availability
) {
}
