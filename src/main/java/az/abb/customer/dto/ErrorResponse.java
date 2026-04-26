package az.abb.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String detail;
    private int status;
    private String title;
    private String timestamp;
    private String instance;
}