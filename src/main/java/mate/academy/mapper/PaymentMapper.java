package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.payment.CreatePaymentRequestDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.dto.payment.PaymentWithoutSessionDto;
import mate.academy.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "amountToPay", target = "amount")
    PaymentDto toDto(Payment payment);

    @Mapping(source = "bookingId", target = "booking.id")
    @Mapping(source = "amount", target = "amountToPay")
    Payment toEntity(CreatePaymentRequestDto requestDto);

    @Mapping(source = "booking.id", target = "bookingId")
    PaymentWithoutSessionDto toDtoWithoutSession(Payment payment);
}
