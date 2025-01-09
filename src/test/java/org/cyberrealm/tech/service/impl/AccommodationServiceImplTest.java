package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.ACCOMMODATION_STRING;
import static org.cyberrealm.tech.util.TestConstants.ADDRESS_DUPLICATE_RESOURCE_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.BOOKED_ADDRESS_DUPLICATE_RESOURCE_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestUtil.ACCOMMODATION_PAGE;
import static org.cyberrealm.tech.util.TestUtil.ACCOMMODATION_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.CREATE_ACCOMMODATION_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.CREATE_ADDRESS_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAGEABLE;
import static org.cyberrealm.tech.util.TestUtil.SECOND_CREATE_ACCOMMODATION_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.getFirstAccommodation;
import static org.cyberrealm.tech.util.TestUtil.getFirstAddress;
import static org.cyberrealm.tech.util.TestUtil.getSecondAddress;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.dto.accommodation.AccommodationDto;
import org.cyberrealm.tech.exception.DuplicateResourceException;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.mapper.AccommodationMapper;
import org.cyberrealm.tech.mapper.AddressMapper;
import org.cyberrealm.tech.mapper.impl.AccommodationMapperImpl;
import org.cyberrealm.tech.mapper.impl.AddressMapperImpl;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Address;
import org.cyberrealm.tech.repository.AccommodationRepository;
import org.cyberrealm.tech.repository.AddressRepository;
import org.cyberrealm.tech.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceImplTest {
    @Mock
    private AccommodationRepository accommodationRepository;
    @Spy
    private AddressMapper addressMapper = new AddressMapperImpl();
    @Spy
    private AccommodationMapper accommodationMapper = new AccommodationMapperImpl(addressMapper);
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    @DisplayName("Save a valid accommodation")
    void save_validCreateAccommodationRequestDto_returnsAccommodationDto() {
        //Given
        Accommodation accommodation = getFirstAccommodation();
        when(accommodationMapper.toEntity(CREATE_ACCOMMODATION_REQUEST_DTO))
                .thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(addressRepository.existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                CREATE_ADDRESS_REQUEST_DTO.country(), CREATE_ADDRESS_REQUEST_DTO.city(),
                CREATE_ADDRESS_REQUEST_DTO.state(), CREATE_ADDRESS_REQUEST_DTO.street(),
                CREATE_ADDRESS_REQUEST_DTO.houseNumber())).thenReturn(false);

        //When
        AccommodationDto actual = accommodationService.save(CREATE_ACCOMMODATION_REQUEST_DTO);

        //Then
        AccommodationDto expected = ACCOMMODATION_RESPONSE_DTO;
        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
        verify(addressRepository, times(1))
                .existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                        CREATE_ADDRESS_REQUEST_DTO.country(), CREATE_ADDRESS_REQUEST_DTO.city(),
                        CREATE_ADDRESS_REQUEST_DTO.state(), CREATE_ADDRESS_REQUEST_DTO.street(),
                        CREATE_ADDRESS_REQUEST_DTO.houseNumber());
        verify(accommodationRepository, times(1)).save(accommodation);
        verify(accommodationMapper, times(1))
                .toEntity(CREATE_ACCOMMODATION_REQUEST_DTO);
        verify(notificationService, times(1))
                .sendNotification("New booking created with ID:" + accommodation.getId());
    }

    @Test
    @DisplayName("Throw DuplicateResourceException")
    void save_duplicatedAddress_throwsDuplicateResourceException() {
        //Given
        when(addressRepository.existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                CREATE_ADDRESS_REQUEST_DTO.country(), CREATE_ADDRESS_REQUEST_DTO.city(),
                CREATE_ADDRESS_REQUEST_DTO.state(), CREATE_ADDRESS_REQUEST_DTO.street(),
                CREATE_ADDRESS_REQUEST_DTO.houseNumber())).thenReturn(true);

        //When
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                accommodationService.save(CREATE_ACCOMMODATION_REQUEST_DTO));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ADDRESS_DUPLICATE_RESOURCE_EXCEPTION,
                CREATE_ADDRESS_REQUEST_DTO.country(), CREATE_ADDRESS_REQUEST_DTO.city(),
                CREATE_ADDRESS_REQUEST_DTO.state(), CREATE_ADDRESS_REQUEST_DTO.street(),
                CREATE_ADDRESS_REQUEST_DTO.houseNumber(), CREATE_ADDRESS_REQUEST_DTO.postalCode());
        assertThat(actual).isEqualTo(expected);
        verify(addressRepository, times(1))
                .existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                        CREATE_ADDRESS_REQUEST_DTO.country(), CREATE_ADDRESS_REQUEST_DTO.city(),
                        CREATE_ADDRESS_REQUEST_DTO.state(), CREATE_ADDRESS_REQUEST_DTO.street(),
                        CREATE_ADDRESS_REQUEST_DTO.houseNumber());
        verify(notificationService, times(0)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Find all accommodations")
    void findAll_validPageable_returnsAllAccommodations() {
        //Given
        when(accommodationRepository.findAll(PAGEABLE)).thenReturn(ACCOMMODATION_PAGE);

        //When
        List<AccommodationDto> actual = accommodationService.findAll(PAGEABLE);

        //Then
        AccommodationDto expected = ACCOMMODATION_RESPONSE_DTO;
        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(expected);
        verify(accommodationRepository, times(1)).findAll(PAGEABLE);
    }

    @Test
    @DisplayName("Find accommodation by ID")
    void findById_validId_returnsAccommodationDto() {
        //Given
        Accommodation accommodation = getFirstAccommodation();
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(accommodation));

        //When
        AccommodationDto actual = accommodationService.findById(FIRST_ACCOMMODATION_ID);

        //Then
        AccommodationDto expected = ACCOMMODATION_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(accommodationRepository, times(1)).findById(FIRST_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Find accommodation by invalid id should throw EntityNotFoundException")
    void findById_invalidId_throwsEntityNotFoundException() {
        //Given
        when(accommodationRepository.findById(INVALID_ACCOMMODATION_ID))
                .thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                accommodationService.findById(INVALID_ACCOMMODATION_ID));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, ACCOMMODATION_STRING,
                INVALID_ACCOMMODATION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(accommodationRepository, times(1)).findById(INVALID_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Update accommodation by ID")
    void updateById_validIdAndRequestDto_returnsUpdatedAccommodationDto() {
        //Given
        Accommodation accommodation = getFirstAccommodation();
        Address address = getFirstAddress();
        when(addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                address.getCountry(),
                address.getCity(),
                address.getState(),
                address.getStreet(),
                address.getHouseNumber()
        )).thenReturn(Optional.of(address));
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);

        //When
        AccommodationDto actual = accommodationService.updateById(FIRST_ACCOMMODATION_ID,
                CREATE_ACCOMMODATION_REQUEST_DTO);

        //Then
        AccommodationDto expected = ACCOMMODATION_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(accommodationRepository, times(1))
                .findById(FIRST_ACCOMMODATION_ID);
        verify(accommodationRepository, times(1)).save(accommodation);
    }

    @Test
    @DisplayName("Update accommodation by invalid id")
    void updateById_invalidId_throwsEntityNotFoundException() {
        //Given
        when(accommodationRepository.findById(INVALID_ACCOMMODATION_ID))
                .thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                accommodationService.updateById(INVALID_ACCOMMODATION_ID,
                        CREATE_ACCOMMODATION_REQUEST_DTO));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, ACCOMMODATION_STRING,
                INVALID_ACCOMMODATION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(accommodationRepository, times(1))
                .findById(INVALID_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Update accommodation with unavailable address")
    void updateById_invalidRequestDto_throwsDuplicateResourceException() {
        //Given
        Accommodation accommodation = getFirstAccommodation();
        Address address = getSecondAddress();
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(accommodation));
        when(addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                address.getCountry(),
                address.getCity(),
                address.getState(),
                address.getStreet(),
                address.getHouseNumber()
        )).thenReturn(Optional.of(address));

        //When
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                accommodationService.updateById(FIRST_ACCOMMODATION_ID,
                        SECOND_CREATE_ACCOMMODATION_REQUEST_DTO));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(BOOKED_ADDRESS_DUPLICATE_RESOURCE_EXCEPTION,
                address.getCountry(),
                address.getCity(),
                address.getState(),
                address.getStreet(),
                address.getHouseNumber(),
                address.getPostalCode());
        assertThat(actual).isEqualTo(expected);
        verify(accommodationRepository, times(1))
                .findById(FIRST_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Delete accommodation by ID")
    void deleteById_validId_deletesAccommodation() {
        //Given
        doNothing().when(accommodationRepository).deleteById(FIRST_ACCOMMODATION_ID);

        //When
        accommodationService.deleteById(FIRST_ACCOMMODATION_ID);

        //Then
        verify(accommodationRepository, times(1)).deleteById(FIRST_ACCOMMODATION_ID);
    }
}
