package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.model.Accommodation;
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
