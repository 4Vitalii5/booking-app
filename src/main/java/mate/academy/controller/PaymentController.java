package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.CreatePaymentRequestDto;
import mate.academy.dto.payment.PaymentDto;
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

    @GetMapping
    @Operation(summary = "Get users payment information",
            description = "Retrieves payment information for users.")
    public PaymentDto getPaymentInfo(@RequestParam("user_id") Long userId,
                                     Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.getPaymentInfo(userId, user.getId());
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @PostMapping
    @Operation(summary = "Initiates payment sessions",
            description = "Initiates payment sessions for booking transactions.")
    public PaymentDto createPayment(@RequestBody @Valid CreatePaymentRequestDto requestDto) {
        return paymentService.createPayment(requestDto);
    }

    @GetMapping("/success/")
    @Operation(summary = "Handles successful payment",
            description = "Handles successful payment processing through Stripe redirection.")
    public String getSuccessPayment(@RequestParam("session_id") String sessionId) {
        return null;
    }

    @GetMapping("/cancel/")
    @Operation(summary = "Manages payment cancellation", description = "Manages payment "
            + "cancellation and returns payment paused messages during Stripe redirection.")
    public String getCanceledPayment(@RequestParam("session_id") String sessionId) {
        return null;
    }
}


//Payment Controller (Stripe): Facilitates payments for bookings through the platform.
//Interacts with Stripe API. Use stripe-java library.
//
//GET: /payments/?user_id=... - Retrieves payment information for users.
//POST: /payments/ - Initiates payment sessions for booking transactions.
//GET: /payments/success/ - Handles successful payment processing through Stripe redirection.
//GET: /payments/cancel/ - Manages payment cancellation and returns payment
//paused messages during Stripe redirection.
