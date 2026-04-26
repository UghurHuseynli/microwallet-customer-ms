package az.abb.customer.feign.decoder;

import az.abb.customer.dto.ErrorResponse;
import az.abb.customer.exception.InsufficientFundsException;
import az.abb.customer.exception.InternalServerErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.coyote.BadRequestException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public FeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorBody = getResponseBody(response);

        try {
            // Parse payment ms error response into your error dto
            ErrorResponse errorResponse = objectMapper.readValue(errorBody, ErrorResponse.class);
            return switch (response.status()) {
                case 400 -> new BadRequestException(errorResponse.getDetail());
                case 404 -> new ResourceNotFoundException(errorResponse.getDetail());
                case 422 -> new InsufficientFundsException(errorResponse.getDetail());
                default  -> new InternalServerErrorException("Payment service error: " + errorResponse.getDetail());
            };

        } catch (JsonProcessingException e) {
            return new InternalServerErrorException("Unexpected error from payment service");
        }
    }

    private String getResponseBody(Response response) {
        try {
            if (response.body() == null) return "{}";
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "{}";
        }
    }
}