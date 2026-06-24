package pe.com.bootcamp.creditservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document("credit_consumption")
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class CreditConsumption {
    @Id
    private String consumptionId;
    private String creditId;
    private String customerId;
    private BigDecimal amount;
    private String merchantName;
    private String description;
    private LocalDateTime consumptionDate;
    private Boolean status;

}
