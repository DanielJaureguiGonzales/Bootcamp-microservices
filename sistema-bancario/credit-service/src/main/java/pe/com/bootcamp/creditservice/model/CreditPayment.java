package pe.com.bootcamp.creditservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
@Document("credit_payment")
public class CreditPayment {

    @Id
    private String paymentId;
    private String creditId;
    private String paymentMethod;
    private BigDecimal amount;
    private String description;
    private LocalDateTime paymentDate;
    private Boolean status;

}
