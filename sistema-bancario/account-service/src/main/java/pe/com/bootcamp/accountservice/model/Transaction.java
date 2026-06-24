package pe.com.bootcamp.accountservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document("transactions")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    private String transactionId;
    private String accountId;
    private String customerId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private Boolean status;

}
