package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.dto.address.CreateAddressRequestDto;
import mate.academy.exception.DuplicateResourceException;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.AccommodationMapper;
import mate.academy.model.Accommodation;
import mate.academy.repository.AccommodationRepository;
import mate.academy.repository.AddressRepository;
import mate.academy.service.AccommodationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AddressRepository addressRepository;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        checkAddressAvailability(requestDto.addressDto());
        Accommodation accommodation = accommodationMapper.toEntity(requestDto);
        accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public List<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable).stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationDto findById(Long id) {
        Accommodation accommodation = getAccommodationById(id);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public AccommodationDto updateById(Long id, CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = getAccommodationById(id);
        accommodationMapper.updateAccommodationFromDto(requestDto, accommodation);
        accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find accommodation by id: " + id)
        );
    }

    private void checkAddressAvailability(CreateAddressRequestDto requestDto) {
        if (addressRepository.existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                requestDto.country(), requestDto.city(), requestDto.state(),
                requestDto.street(), requestDto.houseNumber()
        )) {
            throw new DuplicateResourceException(
                    String.format("This address %s,%s,%s,%s,%s,%s already exists",
                            requestDto.country(), requestDto.city(), requestDto.state(),
                            requestDto.street(), requestDto.houseNumber(), requestDto.postalCode())
            );
        }
    }
}
