package az.abb.customer.controller;

import az.abb.customer.dto.request.ConversionRequest;
import az.abb.customer.dto.request.PaymentRequest;
import az.abb.customer.dto.response.ConversionResponse;
import az.abb.customer.dto.response.DocumentResponse;
import az.abb.customer.dto.response.PaymentResponse;
import az.abb.customer.entity.User;
import az.abb.customer.feign.PaymentFeignClient;
import az.abb.customer.service.DocumentService;
import az.abb.customer.service.PaymentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/payment")
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, scheme = "bearer")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "APIs that delegate to Payment MS")
public class PaymentController {

    private final PaymentFeignClient paymentFeignClient;
    private final DocumentService documentService;
    private final PaymentHistoryService paymentHistoryService;

    @Operation(summary = "Upload a document for processing")
    @PostMapping(value = "/upload-doc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestPart("description") String description,
            @AuthenticationPrincipal UserDetails userDetails

    ) {
        User user = (User) userDetails;
        Long userId = user.getId();
        DocumentResponse response = documentService.processDocument(file, userId, description);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Convert currency with fee calculation")
    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convertCurrency(
            @RequestBody ConversionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = (User) userDetails;
        Long userId = user.getId();
        ConversionResponse response = paymentFeignClient.convertCurrency(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Payment operation")
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = (User) userDetails;
        Long userId = user.getId();
        return ResponseEntity.ok(paymentHistoryService.createPayment(request, userId));
    }

}
