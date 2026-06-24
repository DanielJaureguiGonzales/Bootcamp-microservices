package pe.com.bootcamp.creditservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CreditResponse(
        String creditId,
        String customerId,
        String creditNumber,
        BigDecimal balance,
        BigDecimal creditLimit,
        BigDecimal usedAmount,
        String currencyName,
        BigDecimal exchangeRate,
        BigDecimal annualInterestRate) {
}
