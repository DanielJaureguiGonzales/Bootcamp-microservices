package pe.com.bootcamp.accountservice.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.com.bootcamp.accountservice.model.Account;
import pe.com.bootcamp.accountservice.model.AccountParticipant;
import reactor.core.publisher.Flux;

@Repository
public interface AccountParticipantRepository extends ReactiveMongoRepository<AccountParticipant, String> {

    Flux<AccountParticipant> findByCustomerIdAndParticipantRoleAndStatus(String customerId, String participantRole, Boolean status);

    Flux<AccountParticipant> findByCustomerIdAndStatus(String customerId, Boolean status);
}

