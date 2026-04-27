package az.abb.customer.scheduler;

import az.abb.customer.kafka.PaymentEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventScheduler {

    private final PaymentEventConsumer paymentEventConsumer;

    @Scheduled(fixedRate = 60000)
    public void logPaymentEventSummary() {
        int count = paymentEventConsumer.getAndResetCount();
        if (count > 0) {
            log.info("Scheduler report — {} payment event(s) processed in last 1 minute", count);
        } else {
            log.info("Scheduler report — no payment events received in last 1 minute");
        }
    }
}