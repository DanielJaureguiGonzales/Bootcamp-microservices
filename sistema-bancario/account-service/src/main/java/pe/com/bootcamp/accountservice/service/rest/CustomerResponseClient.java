package pe.com.bootcamp.accountservice.service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.com.bootcamp.accountservice.dto.*;
import pe.com.bootcamp.accountservice.exceptions.ResourceNotFoundException;
import pe.com.bootcamp.accountservice.service.client.CustomerClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerResponseClient {

    private final CustomerClient customerClient;

    public Mono<CustomerResponse> getCustomerResponse(AccountRequest accountRequest){
        return getCustomerResponseByCustomer(accountRequest.documentNumber(), accountRequest.documentType());
    }

    public Mono<List<CustomerSummaryResponse>> getCustomerSummaryList(DocumentNumbersRequest request){
        return customerClient.searchCustomerReponse(request);
    }

    public Mono<CustomerResponse> getCustomerResponseByCustomer(String documentNumber, String documentType){
        return customerClient.getCustomerResponse(documentNumber, documentType)
                .onErrorMap(
                        WebClientResponseException.NotFound.class,
                        ex -> new ResourceNotFoundException(
                                "Customer",
                                "documentNumber",
                                documentNumber
                        )
                )
                .doOnError(throwable -> log.error("Error en obtener el cliente: {}", String.valueOf(throwable)));
    }



}
