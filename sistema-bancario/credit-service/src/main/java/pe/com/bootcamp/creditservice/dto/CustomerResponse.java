package pe.com.bootcamp.creditservice.dto;

public record CustomerResponse(
        String id,
        String documentNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        String documentType
) {
}
