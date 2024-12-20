package org.cyberrealm.tech.repository;

import java.util.Optional;
import org.cyberrealm.tech.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    boolean existsByCountryAndCityAndStateAndStreetAndHouseNumber(
            String country, String city, String state, String street, String houseNumber
    );

    Optional<Address> findByCountryAndCityAndStateAndStreetAndHouseNumber(
            String country, String city, String state, String street, String houseNumber
    );
}
