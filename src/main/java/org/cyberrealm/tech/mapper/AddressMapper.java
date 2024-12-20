package org.cyberrealm.tech.mapper;

import org.cyberrealm.tech.config.MapperConfig;
import org.cyberrealm.tech.dto.address.AddressDto;
import org.cyberrealm.tech.dto.address.CreateAddressRequestDto;
import org.cyberrealm.tech.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    AddressDto toDto(Address address);

    Address toEntity(CreateAddressRequestDto requestDto);

    void updateAccommodationFromDto(CreateAddressRequestDto requestDto,
                                    @MappingTarget Address address);
}
