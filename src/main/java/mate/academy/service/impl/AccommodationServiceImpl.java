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
import mate.academy.model.Address;
import mate.academy.repository.AccommodationRepository;
import mate.academy.repository.AddressRepository;
import mate.academy.service.AccommodationService;
import mate.academy.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        checkAndSaveAddress(requestDto.addressDto());
        Accommodation accommodation = accommodationMapper.toEntity(requestDto);
        accommodationRepository.save(accommodation);
        notificationService.sendNotification("New booking created with ID:"
                + accommodation.getId());
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
        return accommodationMapper.toDto(getAccommodationById(id));
    }

    @Transactional
    @Override
    public AccommodationDto updateById(Long id, CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = getAccommodationById(id);
        checkAddressAvailability(requestDto, accommodation);
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

    private void checkAndSaveAddress(CreateAddressRequestDto requestDto) {
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

    private Address getAddressByAddressDto(CreateAccommodationRequestDto requestDto) {
        return addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                requestDto.addressDto().country(), requestDto.addressDto().city(),
                requestDto.addressDto().state(), requestDto.addressDto().street(),
                requestDto.addressDto().houseNumber()
        );
    }

    private void checkAddressAvailability(CreateAccommodationRequestDto requestDto,
                                          Accommodation accommodation) {
        Address addressFromDto = getAddressByAddressDto(requestDto);
        if (!accommodation.getAddress().getId().equals(addressFromDto.getId())) {
            throw new DuplicateResourceException(
                    String.format(
                            "This address %s,%s,%s,%s,%s,%s already belong another accommodation",
                            requestDto.addressDto().country(), requestDto.addressDto().city(),
                            requestDto.addressDto().state(), requestDto.addressDto().street(),
                            requestDto.addressDto().houseNumber(),
                            requestDto.addressDto().postalCode()
                    )
            );
        }
    }
}
