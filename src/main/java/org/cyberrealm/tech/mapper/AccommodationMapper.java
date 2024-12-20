package org.cyberrealm.tech.mapper;

import org.cyberrealm.tech.config.MapperConfig;
import org.cyberrealm.tech.dto.accommodation.AccommodationDto;
import org.cyberrealm.tech.dto.accommodation.CreateAccommodationRequestDto;
import org.cyberrealm.tech.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = AddressMapper.class)
public interface AccommodationMapper {
    @Mapping(source = "address.id", target = "addressId")
    AccommodationDto toDto(Accommodation accommodation);

    @Mapping(source = "addressDto", target = "address")
    Accommodation toEntity(CreateAccommodationRequestDto requestDto);

    @Mapping(source = "addressDto", target = "address")
    void updateAccommodationFromDto(CreateAccommodationRequestDto requestDto,
                                    @MappingTarget Accommodation accommodation);
}
