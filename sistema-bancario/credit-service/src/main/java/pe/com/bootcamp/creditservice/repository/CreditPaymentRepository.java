package pe.com.bootcamp.creditservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.com.bootcamp.creditservice.model.Credit;
import pe.com.bootcamp.creditservice.model.CreditPayment;

@Repository
public interface CreditPaymentRepository extends ReactiveMongoRepository<CreditPayment, String> {
}
