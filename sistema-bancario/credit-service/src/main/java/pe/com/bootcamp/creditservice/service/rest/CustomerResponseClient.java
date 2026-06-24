package pe.com.bootcamp.creditservice.service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.com.bootcamp.creditservice.dto.CustomerResponse;
import pe.com.bootcamp.creditservice.exceptions.ResourceNotFoundException;
import pe.com.bootcamp.creditservice.service.client.CustomerClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerResponseClient {

    private final CustomerClient customerClient;


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
