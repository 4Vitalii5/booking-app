package org.cyberrealm.tech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.accommodation.AccommodationDto;
import org.cyberrealm.tech.dto.accommodation.CreateAccommodationRequestDto;
import org.cyberrealm.tech.service.AccommodationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management", description = "Managing accommodation inventory"
        + "(CRUD for Accommodations)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new accommodation",
            description = "Permits the addition of new accommodations.")
    public AccommodationDto createAccommodation(
            @RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }
    
    @GetMapping
    @Operation(summary = "Get all accommodations",
            description = "Provides a list of available accommodations.")
    public List<AccommodationDto> getAll(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get accommodation by ID",
            description = "Retrieves detailed information about a specific accommodation.")
    public AccommodationDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update accommodation by ID", description = "Allows updates to "
            + "accommodation details, including inventory management.")
    public AccommodationDto updateAccommodationById(
            @PathVariable Long id, @RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.updateById(id, requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete accommodation by ID",
            description = "Enables the removal of accommodations.")
    public void deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
