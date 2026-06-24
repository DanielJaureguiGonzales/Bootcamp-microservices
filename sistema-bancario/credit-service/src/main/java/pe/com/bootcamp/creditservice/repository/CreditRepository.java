package pe.com.bootcamp.creditservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.com.bootcamp.creditservice.model.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {

    Flux<Credit> findByCustomerIdAndStatus(String customerId, Boolean status);
    Mono<Boolean> existsByCustomerIdAndStatus(String customerId, Boolean status);
    Mono<Credit> findByCreditNumberAndStatus(String creditNumber, Boolean status);
}
