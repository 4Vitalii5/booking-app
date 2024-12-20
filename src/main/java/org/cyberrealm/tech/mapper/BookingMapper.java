package org.cyberrealm.tech.mapper;

import org.cyberrealm.tech.config.MapperConfig;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.booking.CreateBookingRequestDto;
import org.cyberrealm.tech.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(source = "accommodation.id", target = "accommodationId")
    BookingDto toDto(Booking booking);

    @Mapping(source = "accommodationId", target = "accommodation.id")
    Booking toEntity(CreateBookingRequestDto requestDto);

    @Mapping(source = "accommodationId", target = "accommodation.id")
    void updateBookingFromDto(CreateBookingRequestDto requestDto,
                              @MappingTarget Booking booking);
}
