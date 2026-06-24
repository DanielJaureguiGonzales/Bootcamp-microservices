package pe.com.bootcamp.creditservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document("credits")
@AllArgsConstructor
@NoArgsConstructor
@ToString @Getter @Setter
public class Credit {

    @Id
    private String creditId;
    private String customerId;
    private String creditNumber;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private BigDecimal usedAmount;
    private String currencyType;
    private String currencyName;
    private BigDecimal exchangeRate;
    private BigDecimal annualInterestRate;
    private Boolean status;
    private LocalDateTime openingDate;

}
