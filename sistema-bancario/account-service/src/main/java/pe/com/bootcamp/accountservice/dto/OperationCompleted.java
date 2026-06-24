package pe.com.bootcamp.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OperationCompleted(
        String customerId,
        String accountId,
        BigDecimal amount,
        String operation,
        LocalDateTime transactionDate
) {
}
