package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.payment.CreatePaymentRequestDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.dto.payment.PaymentWithoutSessionDto;
import mate.academy.model.User;
import mate.academy.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management",
        description = "Facilitates payments for bookings through the platform.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @GetMapping
    @Operation(summary = "Get users payment information",
            description = "Retrieves payment information for users.")
    public List<PaymentWithoutSessionDto> getPaymentInfo(@RequestParam Long userId,
                                                         Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return paymentService.getPaymentInfo(userId, currentUser);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @PostMapping
    @Operation(summary = "Initiates payment sessions",
            description = "Initiates payment sessions for booking transactions.")
    public PaymentDto createPayment(@RequestBody @Valid CreatePaymentRequestDto requestDto) {
        return paymentService.createPayment(requestDto);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @GetMapping("/success/")
    @Operation(summary = "Handles successful payment",
            description = "Handles successful payment processing through Stripe redirection.")
    public PaymentWithoutSessionDto getSuccessPayment(@NotBlank String sessionId) {
        return paymentService.handleSuccessPayment(sessionId);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @GetMapping("/cancel/")
    @Operation(summary = "Manages payment cancellation", description = "Manages payment "
            + "cancellation and returns payment paused messages during Stripe redirection.")
    public BookingDto getCanceledPayment(@NotBlank String sessionId) {
        return paymentService.handleCancelledPayment(sessionId);
    }
}
