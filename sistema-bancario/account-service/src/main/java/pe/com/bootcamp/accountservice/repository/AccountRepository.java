package pe.com.bootcamp.accountservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.com.bootcamp.accountservice.model.Account;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    Mono<Long> countByAccountIdInAndAccountTypeAndStatus(
            List<String> ids,
            String accountType,
            Boolean status
    );

    Mono<Account> findByAccountNumber(String accountNumber);

}
