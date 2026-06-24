package pe.com.bootcamp.accountservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OperationRequest(

        @NotBlank(message = "Document type is required")
        @Pattern(
                regexp = "01|02",
                message = "Document type must be 01 for PERSONAL or 02 for BUSINESS"
        )
        String documentType,

        @NotBlank(message = "Account number is required")
        @Pattern(
                regexp = "^[0-9]{13}$",
                message = "Account number must contain exactly 13 digits"
        )
        String accountNumber,

        String documentNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than zero"
        )
        @Digits(
                integer = 12,
                fraction = 2,
                message = "Amount must have up to 12 integer digits and 2 decimal places"
        )
        BigDecimal amount
) {
}
