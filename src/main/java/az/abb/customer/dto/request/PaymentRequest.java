package az.abb.customer.dto.request;

import az.abb.customer.enums.Currency;
import az.abb.customer.enums.PaymentType;

import java.math.BigDecimal;

public record PaymentRequest (
        BigDecimal amount,
        Currency currencyType,
        PaymentType paymentType,
        String description
) {}
