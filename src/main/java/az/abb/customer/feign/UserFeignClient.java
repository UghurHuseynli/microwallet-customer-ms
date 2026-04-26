package az.abb.customer.feign;

import az.abb.customer.config.FeignConfig;
import az.abb.customer.dto.request.AccountRequest;
import az.abb.customer.dto.response.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-ms", url = "${payment.service.url:http://localhost:8081}", contextId = "userAccountClient",configuration = FeignConfig.class)
public interface UserFeignClient {

    @PostMapping("/api/v1/account")
    AccountResponse createAccount(@RequestHeader("X-Account-Id") Long userId, @RequestBody AccountRequest request);
}
