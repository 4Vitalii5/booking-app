package mate.academy.service;

import java.util.List;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    List<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    AccommodationDto updateById(Long id, CreateAccommodationRequestDto requestDto);

    void deleteById(Long id);
}
