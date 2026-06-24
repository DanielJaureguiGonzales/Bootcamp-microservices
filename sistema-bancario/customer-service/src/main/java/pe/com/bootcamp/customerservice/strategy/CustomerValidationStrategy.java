package pe.com.bootcamp.customerservice.strategy;

import pe.com.bootcamp.customerservice.dto.CustomerRequest;
import reactor.core.publisher.Mono;

public interface CustomerValidationStrategy {

    String getDocumentType();

    Mono<Void> validate(CustomerRequest customerRequest);

}
