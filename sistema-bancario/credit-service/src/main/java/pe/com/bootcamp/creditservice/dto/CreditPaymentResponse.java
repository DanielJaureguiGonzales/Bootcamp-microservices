package pe.com.bootcamp.creditservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreditPaymentResponse(
        String creditNumber,
        BigDecimal paymentAmount,
        BigDecimal previousDebt,
        BigDecimal remainingDebt,
        String status,
        String message
) {
}
