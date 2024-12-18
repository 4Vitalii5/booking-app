package mate.academy.repository;

import mate.academy.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    boolean existsByCountryAndCityAndStateAndStreetAndHouseNumber(
            String country, String city, String state, String street, String houseNumber
    );

    Address findByCountryAndCityAndStateAndStreetAndHouseNumber(
            String country, String city, String state, String street, String houseNumber
    );
}
