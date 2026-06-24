package pe.com.bootcamp.creditservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.com.bootcamp.creditservice.model.CreditConsumption;
import pe.com.bootcamp.creditservice.model.CreditPayment;

@Repository
public interface CreditConsumptionRepository extends ReactiveMongoRepository<CreditConsumption, String> {

}
