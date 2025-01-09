package org.cyberrealm.tech.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.accommodation.AccommodationDto;
import org.cyberrealm.tech.dto.accommodation.CreateAccommodationRequestDto;
import org.cyberrealm.tech.dto.address.CreateAddressRequestDto;
import org.cyberrealm.tech.exception.DuplicateResourceException;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.mapper.AccommodationMapper;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Address;
import org.cyberrealm.tech.repository.AccommodationRepository;
import org.cyberrealm.tech.repository.AddressRepository;
import org.cyberrealm.tech.service.AccommodationService;
import org.cyberrealm.tech.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
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
        sendNotification("New booking created with ID:" + accommodation.getId());
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

    @Async
    public void sendNotification(String message) {
        if (notificationService != null) {
            notificationService.sendNotification(message);
        }
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find accommodation by id:" + id)
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

    private Address getAddressByAddressDto(CreateAddressRequestDto requestDto) {
        return addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                requestDto.country(), requestDto.city(), requestDto.state(), requestDto.street(),
                requestDto.houseNumber()
        ).orElseThrow(() -> new EntityNotFoundException(
                        String.format("Can't find address %s,%s,%s,%s,%s,%s.",
                                requestDto.country(), requestDto.city(), requestDto.state(),
                                requestDto.street(), requestDto.houseNumber(),
                                requestDto.postalCode())
                )
        );
    }

    private void checkAddressAvailability(CreateAccommodationRequestDto requestDto,
                                          Accommodation accommodation) {
        Address addressFromDto = getAddressByAddressDto(requestDto.addressDto());
        if (addressFromDto != null
                && !accommodation.getAddress().getId().equals(addressFromDto.getId())) {
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
