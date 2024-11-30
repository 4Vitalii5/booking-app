package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.address.AddressDto;
import mate.academy.dto.address.CreateAddressRequestDto;
import mate.academy.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    AddressDto toDto(Address address);

    Address toEntity(CreateAddressRequestDto requestDto);

    void updateAccommodationFromDto(CreateAddressRequestDto requestDto,
                                    @MappingTarget Address address);
}
