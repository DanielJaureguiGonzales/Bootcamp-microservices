package pe.com.bootcamp.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AccountRequest(
      /*
        @NotNull(message = "Initial amount is required")
        @DecimalMin(value = "0.00", message = "Initial amount must be greater than or equal to zero")
        BigDecimal initialAmount,
        Integer allowedTransactionDay,
        List<AccountParticipantRequest> participants*/

        @NotBlank(message = "Account type is required")
        @Pattern(
                regexp = "01|02|03",
                message = "Account type must be 01 for SAVINGS, 02 for CHECKING or 03 for FIXED_TERM"
        )
        String accountType,

        @NotBlank(message = "Document type is required")
        @Pattern(
                regexp = "01|02",
                message = "Document type must be 01 for PERSONAL or 02 for BUSINESS"
        )
        String documentType,

        String documentNumber,

        @NotNull(message = "Initial amount is required")
        @DecimalMin(value = "0.00", message = "Initial amount must be greater than or equal to zero")
        BigDecimal initialAmount,

        Boolean flagFreeCommisionMant,
        Integer maxMovMon,
        String initialDate,
        Integer cantDays,
        List<AccountParticipantRequest> participants
) {
}
