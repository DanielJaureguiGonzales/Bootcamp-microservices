package pe.com.bootcamp.customerservice.service;

import pe.com.bootcamp.customerservice.dto.CustomerRequest;
import pe.com.bootcamp.customerservice.dto.CustomerResponse;
import pe.com.bootcamp.customerservice.dto.CustomerSummaryResponse;
import pe.com.bootcamp.customerservice.dto.DocumentNumbersRequest;
import pe.com.bootcamp.customerservice.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerService {

    Flux<CustomerResponse> findAll();
    Mono<CustomerResponse> findById(String id);
    Mono<CustomerResponse> findByDocumentNumber(String documentNumber);
    Mono<CustomerResponse> createCustomer(CustomerRequest customerRequest);
    Mono<CustomerResponse> updateCustomer(String id, CustomerRequest customerRequest);
    Mono<Void> deleteCustomer(String id);

    Mono<List<CustomerSummaryResponse>> findByDocumentNumbers(DocumentNumbersRequest documentNumbers);
    Mono<CustomerResponse> findByDocumentNumberAndDocumentType(String documentNumber, String documentType);
}
