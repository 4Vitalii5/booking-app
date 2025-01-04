package org.cyberrealm.tech.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_CITY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_COUNTRY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_HOUSE_NUMBER;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_POSTAL_CODE;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_STATE;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_STREET;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_CITY;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_COUNTRY;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_HOUSE_NUMBER;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_ID;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_POSTAL_CODE;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_STATE;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_STREET;
import static org.cyberrealm.tech.util.TestUtil.FIRST_ADDRESS;
import static org.cyberrealm.tech.util.TestUtil.INVALID_ADDRESS;

import org.cyberrealm.tech.model.Address;
import org.junit.jupiter.api.BeforeAll;
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
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/addresses/remove-addresses.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @BeforeAll
    static void beforeAll() {
        FIRST_ADDRESS.setId(FIRST_ADDRESS_ID);
        FIRST_ADDRESS.setCountry(FIRST_ADDRESS_COUNTRY);
        FIRST_ADDRESS.setCity(FIRST_ADDRESS_CITY);
        FIRST_ADDRESS.setState(FIRST_ADDRESS_STATE);
        FIRST_ADDRESS.setStreet(FIRST_ADDRESS_STREET);
        FIRST_ADDRESS.setHouseNumber(FIRST_ADDRESS_HOUSE_NUMBER);
        FIRST_ADDRESS.setPostalCode(FIRST_ADDRESS_POSTAL_CODE);

        INVALID_ADDRESS.setId(INVALID_ADDRESS_ID);
        INVALID_ADDRESS.setCountry(INVALID_ADDRESS_COUNTRY);
        INVALID_ADDRESS.setCity(INVALID_ADDRESS_CITY);
        INVALID_ADDRESS.setState(INVALID_ADDRESS_STATE);
        INVALID_ADDRESS.setStreet(INVALID_ADDRESS_STREET);
        INVALID_ADDRESS.setHouseNumber(INVALID_ADDRESS_HOUSE_NUMBER);
        INVALID_ADDRESS.setPostalCode(INVALID_ADDRESS_POSTAL_CODE);
    }

    @Test
    @DisplayName("Verify existsByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with valid data")
    void existsByCountryAndCityAndStateAndStreetAndHouseNumber_validData_returnsTrue() {
        // When
        boolean exists = addressRepository.existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                FIRST_ADDRESS.getCountry(),
                FIRST_ADDRESS.getCity(),
                FIRST_ADDRESS.getState(),
                FIRST_ADDRESS.getStreet(),
                FIRST_ADDRESS.getHouseNumber()
        );
        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Verify existsByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with invalid data")
    void existsByCountryAndCityAndStateAndStreetAndHouseNumber_invalidData_returnsFalse() {
        // When
        boolean exists = addressRepository.existsByCountryAndCityAndStateAndStreetAndHouseNumber(
                INVALID_ADDRESS.getCountry(),
                INVALID_ADDRESS.getCity(),
                INVALID_ADDRESS.getState(),
                INVALID_ADDRESS.getStreet(),
                INVALID_ADDRESS.getHouseNumber()
        );
        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Verify findByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with valid data")
    void findByCountryAndCityAndStateAndStreetAndHouseNumber_validData_returnsAddress() {
        // When
        Address foundAddress =
                addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                        FIRST_ADDRESS.getCountry(),
                        FIRST_ADDRESS.getCity(),
                        FIRST_ADDRESS.getState(),
                        FIRST_ADDRESS.getStreet(),
                        FIRST_ADDRESS.getHouseNumber()
                );
        // Then
        assertThat(foundAddress).isNotNull();
        assertThat(foundAddress.getId()).isEqualTo(FIRST_ADDRESS.getId());
    }

    @Test
    @DisplayName("Verify findByCountryAndCityAndStateAndStreetAndHouseNumber() method works "
            + "with invalid data")
    void findByCountryAndCityAndStateAndStreetAndHouseNumber_invalidData_returnsNull() {
        // When
        Address foundAddress =
                addressRepository.findByCountryAndCityAndStateAndStreetAndHouseNumber(
                        INVALID_ADDRESS.getCountry(),
                        INVALID_ADDRESS.getCity(),
                        INVALID_ADDRESS.getState(),
                        INVALID_ADDRESS.getStreet(),
                        INVALID_ADDRESS.getHouseNumber()
                );
        // Then
        assertThat(foundAddress).isNull();
    }
}
