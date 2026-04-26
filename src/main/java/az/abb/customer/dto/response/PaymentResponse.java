package az.abb.customer.dto.response;

import az.abb.customer.enums.Currency;
import az.abb.customer.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse (
        Long paymentId,
        BigDecimal balance,
        BigDecimal payedAmount,
        Currency currency,
        PaymentStatus status,
        String eventId,
        LocalDateTime payedDate
) {}
