package pe.com.bootcamp.customerservice.strategy.impl;

import org.springframework.stereotype.Component;
import pe.com.bootcamp.customerservice.dto.CustomerRequest;
import pe.com.bootcamp.customerservice.exceptions.BusinessValidationException;
import pe.com.bootcamp.customerservice.model.constants.DocumentTypes;
import pe.com.bootcamp.customerservice.strategy.CustomerValidationStrategy;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class PersonalCustomerValidationStrategy implements CustomerValidationStrategy {

    @Override
    public String getDocumentType() {
        return DocumentTypes.PERSONAL;
    }

    @Override
    public Mono<Void> validate(CustomerRequest request) {

        Map<String, String> errors = new HashMap<>();

        if (!request.documentNumber().matches("^[0-9]{8}$")) {
            errors.put(
                    "documentNumber",
                    "Personal customer document number must contain exactly 8 digits"
            );
        }

        if (request.firstName() == null || request.firstName().isBlank()) {
            errors.put(
                    "firstName",
                    "First name is required for personal customers"
            );
        }

        if (request.lastName() == null || request.lastName().isBlank()) {
            errors.put(
                    "lastName",
                    "Last name is required for personal customers"
            );
        }

        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(errors));
        }

        return Mono.empty();
    }
}
