package pe.com.bootcamp.accountservice.dto;

public record CustomerResponse(
        String id,
        String documentType,
        String documentNumber,
        String firstName,
        String lastName,
        String companyName,
        String email,
        String phone,
        String address
) {
}
