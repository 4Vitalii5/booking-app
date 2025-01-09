package org.cyberrealm.tech.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestUtil.getFirstAddress;
import static org.cyberrealm.tech.util.TestUtil.getInvalidAddress;

import java.util.Optional;
import org.cyberrealm.tech.model.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/addresses/add-addresses.sql"
})
@Sql(scripts = {
        "classpath:database/addresses/remove-addresses.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("Verify existsByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with valid data")
    void existsByCountryAndCityAndStateAndStreetAndHouseNumber_validData_returnsTrue() {
        //Given
        Address address = getFirstAddress();

        // When
        boolean exists = addressRepository.existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                address.getCountry(),
                address.getCity(),
                address.getState(),
                address.getStreet(),
                address.getHouseNumber()
        );

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Verify existsByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with invalid data")
    void existsByCountryAndCityAndStateAndStreetAndHouseNumber_invalidData_returnsFalse() {
        //Given
        Address invalidAddress = getInvalidAddress();

        // When
        boolean exists = addressRepository.existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                invalidAddress.getCountry(),
                invalidAddress.getCity(),
                invalidAddress.getState(),
                invalidAddress.getStreet(),
                invalidAddress.getHouseNumber()
        );

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Verify findByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with valid data")
    void findByCountryAndCityAndStateAndStreetAndHouseNumber_validData_returnsAddress() {
        // Given
        Address expected = getFirstAddress();

        // When
        Optional<Address> actual =
                addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                        expected.getCountry(),
                        expected.getCity(),
                        expected.getState(),
                        expected.getStreet(),
                        expected.getHouseNumber()
                );

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(Optional.of(expected));
    }

    @Test
    @DisplayName("Verify findByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with invalid data")
    void findByCountryAndCityAndStateAndStreetAndHouseNumber_invalidData_returnsNull() {
        //Given
        Address invalidAddress = getInvalidAddress();

        // When
        Optional<Address> foundAddress =
                addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                        invalidAddress.getCountry(),
                        invalidAddress.getCity(),
                        invalidAddress.getState(),
                        invalidAddress.getStreet(),
                        invalidAddress.getHouseNumber()
                );
        // Then

        assertThat(foundAddress).isEmpty();
    }
}
