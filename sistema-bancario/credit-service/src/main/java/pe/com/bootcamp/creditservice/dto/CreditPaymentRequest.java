package pe.com.bootcamp.creditservice.dto;

import jakarta.validation.constraints.*;


import java.math.BigDecimal;

public record CreditPaymentRequest(

        String documentType,


        String documentNumber,

        @NotBlank(message = "Credit number is required")
        @Size(
                min = 5,
                max = 30,
                message = "Credit number must contain between 5 and 30 characters"
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

        @NotBlank(message = "Payment method is required")
        @Pattern(
                regexp = "CASH",
                message = "Payment method must be CASH"
        )
        String paymentMethod,

        @Size(
                max = 255,
                message = "Description must contain up to 255 characters"
        )
        String description
) {
}
