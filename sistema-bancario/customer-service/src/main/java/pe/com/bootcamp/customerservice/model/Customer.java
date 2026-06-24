package pe.com.bootcamp.customerservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;

@Document("customers")
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class Customer {

    @Id
    private String id;
    private String documentNumber;
    private String documentType;
    private String firstName;
    private String lastName;
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime registrationDate;
    private boolean status;
}
