package pe.com.bootcamp.creditservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreditConsumptionRequest(

        String documentType,


        String documentNumber,

        @NotBlank(message = "Credit number is required")
        @Size(
                min = 13,
                message = "Credit number invalid"
        )
        String creditNumber,

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
        BigDecimal amount,

        @NotBlank(message = "Merchant name is required")
        @Size(
                min = 2,
                max = 100,
                message = "Merchant name must contain between 2 and 100 characters"
        )
        String merchantName,

        @Size(
                max = 255,
                message = "Description must contain up to 255 characters"
        )
        String description
) {
}
