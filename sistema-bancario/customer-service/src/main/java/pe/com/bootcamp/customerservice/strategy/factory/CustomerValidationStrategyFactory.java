package pe.com.bootcamp.customerservice.strategy.factory;

import org.springframework.stereotype.Component;
import pe.com.bootcamp.customerservice.strategy.CustomerValidationStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CustomerValidationStrategyFactory {

    private final Map<String, CustomerValidationStrategy> strategies;

    public CustomerValidationStrategyFactory(List<CustomerValidationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        CustomerValidationStrategy::getDocumentType,
                        Function.identity()
                ));
    }

    public CustomerValidationStrategy getStrategy(String documentType) {
        CustomerValidationStrategy strategy = strategies.get(documentType);

        if (strategy == null) {
            throw new RuntimeException("Unsupported document type: " + documentType);
        }

        return strategy;
    }
}