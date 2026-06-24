package pe.com.bootcamp.accountservice.dto;

public record CustomerSummaryResponse(
        String id,
        String documentNumber,
        Boolean status
) {
}
