package pe.com.bootcamp.accountservice.dto;

import java.util.List;

public record AccountValidationResult(
        List<CustomerSummaryResponse> participantCustomers
) {
    public static AccountValidationResult empty(){
        return new AccountValidationResult(List.of());
    }
}
