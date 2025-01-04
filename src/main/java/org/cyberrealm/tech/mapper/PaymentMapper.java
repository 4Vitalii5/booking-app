package org.cyberrealm.tech.mapper;

import org.cyberrealm.tech.config.MapperConfig;
import org.cyberrealm.tech.dto.payment.CreatePaymentRequestDto;
import org.cyberrealm.tech.dto.payment.PaymentDto;
import org.cyberrealm.tech.dto.payment.PaymentWithoutSessionDto;
import org.cyberrealm.tech.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "amountToPay", target = "amount")
    PaymentDto toDto(Payment payment);

    @Mapping(source = "bookingId", target = "booking.id")
    Payment toEntity(CreatePaymentRequestDto requestDto);

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "amountToPay", target = "amount")
    PaymentWithoutSessionDto toDtoWithoutSession(Payment payment);
}
