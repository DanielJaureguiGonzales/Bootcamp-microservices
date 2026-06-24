package pe.com.bootcamp.creditservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreditConsumptionResponse(
        String creditNumber,
        BigDecimal consumptionAmount,
        BigDecimal creditLimit,
        BigDecimal usedAmount,
        BigDecimal availableAmount,
        String status,
        String message
) {
}
