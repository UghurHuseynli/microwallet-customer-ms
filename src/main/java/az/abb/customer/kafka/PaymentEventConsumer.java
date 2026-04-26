package az.abb.customer.kafka;

import az.abb.customer.dto.event.PaymentHistoryEvent;
import az.abb.customer.dto.response.PaymentResponse;
import az.abb.customer.entity.PaymentHistory;
import az.abb.customer.repository.PaymentHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ObjectMapper objectMapper;

    private final AtomicInteger eventCount = new AtomicInteger(0);  // ← add this


    @KafkaListener(topics = "payment-result-topic", groupId = "customer-group")
    public void handlePaymentEvent(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventId) {
        try {
            TimeUnit.SECONDS.sleep(2);
            PaymentHistoryEvent event = objectMapper.readValue(payload, PaymentHistoryEvent.class);

            PaymentHistory paymentHistory = paymentHistoryRepository
                    .findByPaymentId(event.getPaymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentHistory not found"));
            paymentHistory.setPaymentStatus(event.getPaymentStatus());
            paymentHistoryRepository.save(paymentHistory);

            eventCount.incrementAndGet();  // ← increment on success

            log.info("Payment record updated - paymentId: {}, status: {}",
                    event.getPaymentId(), event.getPaymentStatus());

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize payment event [{}]: {}", eventId, e.getMessage());
            throw new RuntimeException("Deserialization failed", e);
        } catch (ResourceNotFoundException e) {
            log.error("Payment record not found - eventId: {}, error: {}", eventId, e.getMessage());
            // don't rethrow — no point retrying if record doesn't exist
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Unexpected error processing event [{}]: {}", eventId, e.getMessage(), e);
            throw e; // rethrow so Kafka retries
        }
    }

    public int getAndResetCount() {
        return eventCount.getAndSet(0);
    }
}
