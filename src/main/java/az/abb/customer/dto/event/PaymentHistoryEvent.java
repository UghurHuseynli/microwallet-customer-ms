package az.abb.customer.dto.event;

import az.abb.customer.enums.Currency;
import az.abb.customer.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryEvent {
    private Long paymentId;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private Currency currency;
    private LocalDateTime paymentDate;
}
