package org.cyberrealm.tech.repository.booking;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.repository.SpecificationProvider;
import org.cyberrealm.tech.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingSpecificationProviderManager implements SpecificationProviderManager<Booking> {
    private final List<SpecificationProvider<Booking>> bookingSpecificationProviders;

    @Override
    public SpecificationProvider<Booking> getSpecificationProvider(String key) {
        return bookingSpecificationProviders.stream()
                .filter(provider -> provider.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Can't find correct specification provider for key: " + key)
                );
    }
}
