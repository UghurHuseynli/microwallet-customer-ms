package az.abb.customer.dto.response;

import az.abb.customer.enums.Currency;

import java.math.BigDecimal;

public record ConversionResponse (
        BigDecimal originalAmount,
        Currency fromCurrency,
        BigDecimal convertedAmount,
        Currency toCurrency,
        BigDecimal feeAmount,
        BigDecimal feePercentage,
        BigDecimal netAmount
) {}
