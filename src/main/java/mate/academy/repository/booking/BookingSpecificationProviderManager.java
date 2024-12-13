package mate.academy.repository.booking;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Booking;
import mate.academy.repository.SpecificationProvider;
import mate.academy.repository.SpecificationProviderManager;
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
