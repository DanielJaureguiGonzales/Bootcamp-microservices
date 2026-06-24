package pe.com.bootcamp.customerservice.dto;

public record CustomerSummaryResponse(
        String id,
        String documentNumber,
        Boolean status
) {
}
