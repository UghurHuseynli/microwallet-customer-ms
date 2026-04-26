package az.abb.customer.feign;

import az.abb.customer.config.FeignConfig;
import az.abb.customer.dto.event.PaymentHistoryEvent;
import az.abb.customer.dto.request.ConversionRequest;
import az.abb.customer.dto.request.DocumentRequest;
import az.abb.customer.dto.request.PaymentRequest;
import az.abb.customer.dto.response.ConversionResponse;
import az.abb.customer.dto.response.DocumentResponse;
import az.abb.customer.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-ms", url = "${payment.service.url:http://localhost:8081}", configuration = FeignConfig.class)
public interface PaymentFeignClient {

    @PostMapping(value = "/api/v1/payment/accept-doc", consumes = MediaType.APPLICATION_JSON_VALUE)
    DocumentResponse processDocument(
            @RequestHeader("X-Account-Id") Long userId,
            @RequestBody DocumentRequest documentRequest
            );

    @PostMapping("/api/v1/payment/convert")
    ConversionResponse convertCurrency(@RequestHeader("X-Account-Id") Long userId, @RequestBody ConversionRequest request);

    @PostMapping("/api/v1/payment/pay")
    PaymentResponse createPayment(@RequestHeader("X-Account-Id") Long userId, @RequestBody PaymentRequest request);
}
