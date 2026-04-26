package az.abb.customer.service;

import az.abb.customer.dto.request.PaymentRequest;
import az.abb.customer.dto.response.PaymentResponse;
import az.abb.customer.entity.PaymentHistory;
import az.abb.customer.feign.PaymentFeignClient;
import az.abb.customer.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryService {
    private final PaymentFeignClient paymentFeignClient;
    private final PaymentHistoryRepository paymentHistoryRepository;


    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, Long userId) {
        // 1. Call Feign
        PaymentResponse response = paymentFeignClient.createPayment(userId, request);

        // 2. Immediately flush to DB so Kafka consumer can find it
        PaymentHistory history = PaymentHistory.builder()
                .paymentId(response.paymentId())
                .paymentStatus(response.status())
                .amount(response.payedAmount())
                .currency(response.currency())
                .paymentDate(response.payedDate())
                .userId(userId)
                .build();

        paymentHistoryRepository.saveAndFlush(history);  // flush guarantees DB write immediately
        log.info("PaymentHistory saved and flushed for paymentId: {}", response.paymentId());

        return response;
    }
}
