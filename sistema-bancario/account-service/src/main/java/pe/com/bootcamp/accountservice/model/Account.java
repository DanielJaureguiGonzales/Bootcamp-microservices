package pe.com.bootcamp.accountservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document("accounts")
@Builder
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id
    private String accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Boolean status;
    private LocalDateTime openingDate;
    private Boolean flagFreeCommisionMant;
    private Integer maxMovMon;
    private LocalDateTime initialDate;
    private Integer cantDays;



}
