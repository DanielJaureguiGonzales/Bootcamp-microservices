package pe.com.bootcamp.accountservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("account_participant")
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class AccountParticipant {

    @Id
    private String accountParticipantId;
    private String accountId;
    private String customerId;
    private String participantRole;
    private LocalDateTime registrationDate;
    private Boolean status;
}
