package pe.com.bootcamp.customerservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.com.bootcamp.customerservice.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    Flux<Customer> findAllByStatus(Boolean status);
    Mono<Customer> findByIdAndStatus(String id, boolean status);
    Mono<Customer> findByDocumentNumberAndStatus(String documentNumber, boolean status);
    Mono<Boolean> existsByDocumentNumberAndStatus(String documentNumber, boolean status);
    Mono<Customer> findByDocumentNumberAndDocumentTypeAndStatus(String documentNumber, String documentType,
                                                                boolean status);
    Flux<Customer> findByDocumentNumberInAndStatus(List<String> documentNumbers, boolean status);
}

