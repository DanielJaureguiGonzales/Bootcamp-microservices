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
public class BusinessCustomerValidationStrategy implements CustomerValidationStrategy {

    @Override
    public String getDocumentType() {
        return DocumentTypes.BUSINESS;
    }

    @Override
    public Mono<Void> validate(CustomerRequest request) {

        Map<String, String> errors = new HashMap<>();

        if (!request.documentNumber().matches("^[0-9]{11}$")) {
            errors.put(
                    "documentNumber",
                    "Business customer document number must contain exactly 11 digits"
            );
        }

        if (request.companyName() == null || request.companyName().isBlank()) {
            errors.put(
                    "companyName",
                    "Company name is required for business customers"
            );
        }

        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(errors));
        }

        return Mono.empty();
    }
}
