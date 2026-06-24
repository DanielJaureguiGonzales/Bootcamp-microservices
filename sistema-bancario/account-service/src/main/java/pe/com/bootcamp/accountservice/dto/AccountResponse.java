package pe.com.bootcamp.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
public record AccountResponse(
        String accountId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        Boolean status
) {
}
