package pe.com.bootcamp.accountservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.com.bootcamp.accountservice.model.Transaction;

@Repository
public interface TransactionsRepository extends ReactiveMongoRepository<Transaction, String> {
}
