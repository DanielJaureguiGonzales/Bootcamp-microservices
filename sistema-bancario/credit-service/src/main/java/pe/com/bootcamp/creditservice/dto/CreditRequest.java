package pe.com.bootcamp.creditservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreditRequest(
        String documentType,
        String documentNumber,

        @NotBlank(message = "Currency type is required")
        @Pattern(
                regexp = "01|02",
                message = "Currency type must be 01 for SOLES or 02 for DOLAR"
        )
        String currencyType,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be greater than or equal to 1.00")
        @Digits(integer = 12, fraction = 2, message = "Amount must have up to 12 integer digits and 2 decimal places")
        BigDecimal amount


) {
}
