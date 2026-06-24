package pe.com.bootcamp.customerservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.com.bootcamp.customerservice.dto.CustomerRequest;
import pe.com.bootcamp.customerservice.dto.CustomerResponse;
import pe.com.bootcamp.customerservice.dto.CustomerSummaryResponse;
import pe.com.bootcamp.customerservice.dto.DocumentNumbersRequest;
import pe.com.bootcamp.customerservice.exceptions.AlreadyCustomerExistsException;
import pe.com.bootcamp.customerservice.exceptions.ResourceNotFoundException;
import pe.com.bootcamp.customerservice.mapper.CustomerMapper;
import pe.com.bootcamp.customerservice.model.Customer;
import pe.com.bootcamp.customerservice.repository.CustomerRepository;
import pe.com.bootcamp.customerservice.service.CustomerService;
import pe.com.bootcamp.customerservice.strategy.CustomerValidationStrategy;
import pe.com.bootcamp.customerservice.strategy.factory.CustomerValidationStrategyFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerValidationStrategyFactory validationStrategyFactory;

    @Override
    public Flux<CustomerResponse> findAll() {
        return customerRepository.findAllByStatus(true)
                .map(customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> findById(String id) {
        return customerRepository.findByIdAndStatus(id,true)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer", "Id", id)))
                .map(customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> findByDocumentNumber(String documentNumber) {
        return customerRepository.findByDocumentNumberAndStatus(documentNumber,true)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer", "documentNumber", documentNumber)))
                .map(customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> createCustomer(CustomerRequest customerRequest) {

        CustomerValidationStrategy strategy = validationStrategyFactory.getStrategy(customerRequest.documentType());

        return strategy.validate(customerRequest)
                .then(customerRepository.existsByDocumentNumberAndStatus(customerRequest.documentNumber(), true))
                .flatMap(exists -> {

                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new AlreadyCustomerExistsException(String.format("Customer already " +
                                "exists with %s: %s", "documentNumber", customerRequest.documentNumber())));
                    }
                    Customer customer = customerMapper.toCustomer(customerRequest);
                    customer.setRegistrationDate(LocalDateTime.now());
                    customer.setStatus(true);

                    return customerRepository.save(customer);
                })
                .map(customerMapper::toCustomerResponse);

    }

    @Override
    public Mono<CustomerResponse> updateCustomer(String id, CustomerRequest customerRequest) {
        return customerRepository.findByIdAndStatus(id,true)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer", "Id", id)))
                .flatMap(customer -> {
                    customerMapper.updateCustomer(customer, customerRequest);
                    return customerRepository.save(customer);
                })
                .map(customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<Void> deleteCustomer(String id) {
        return customerRepository.findByIdAndStatus(id,true)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer", "Id", id)))
                .flatMap(customer -> {
                                customer.setStatus(false);
                                return customerRepository.save(customer);
                            }
                        )
                .then();
    }

    @Override
    public Mono<CustomerResponse> findByDocumentNumberAndDocumentType(String documentNumber, String documentType) {
        return customerRepository.findByDocumentNumberAndDocumentTypeAndStatus(documentNumber, documentType, true)
                .map(customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<List<CustomerSummaryResponse>> findByDocumentNumbers(DocumentNumbersRequest documentNumbersRequests) {
        List<String> uniqueDocumentNumbers = documentNumbersRequests.documentNumbers().stream()
                .map(String::trim)
                .distinct()
                .toList();
        return customerRepository.findByDocumentNumberInAndStatus(uniqueDocumentNumbers, true)
                .map(customer -> new CustomerSummaryResponse(
                        customer.getId(),
                        customer.getDocumentNumber(),
                        customer.isStatus()
                ))
                .collectList()
                .flatMap(customers ->{

                    if (!(customers.size() == uniqueDocumentNumbers.size())){
                        return Mono.error(new RuntimeException(
                                "Some customers do not exist or are inactive"
                        ));
                    }
                    return Mono.just(customers);


                });
    }


}
