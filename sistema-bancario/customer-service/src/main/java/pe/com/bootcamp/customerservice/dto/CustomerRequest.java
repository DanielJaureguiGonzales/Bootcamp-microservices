package pe.com.bootcamp.customerservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(

        @NotBlank(message = "Document type is required")
        @Pattern(
                regexp = "01|02",
                message = "Document type must be 01 for PERSONAL or 02 for BUSINESS"
        )
        String documentType,

        @NotBlank(message = "Document number is required")
        String documentNumber,

        String firstName,

        String lastName,

        String companyName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Address is required")
        String address
) {
}