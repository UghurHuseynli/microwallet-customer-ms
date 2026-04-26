package az.abb.customer.entity;

import az.abb.customer.enums.Currency;
import az.abb.customer.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus  paymentStatus;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable = false, updatable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private Long userId;
}


