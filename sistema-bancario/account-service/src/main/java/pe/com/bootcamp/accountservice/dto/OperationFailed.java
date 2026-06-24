package pe.com.bootcamp.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperationFailed(
        String customerId,
        String accountId,
        BigDecimal amount,
        String operation,
        String reasonFail,
        LocalDateTime date
) {
}
