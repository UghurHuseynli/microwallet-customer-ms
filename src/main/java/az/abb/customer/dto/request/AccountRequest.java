package az.abb.customer.dto.request;

import az.abb.customer.enums.Currency;

import java.math.BigDecimal;

public record AccountRequest (
    BigDecimal balance,
    Currency currency
) {
    public static AccountRequest defaultAzn() {
        return new AccountRequest(new BigDecimal(1000), Currency.AZN);
    }
}
