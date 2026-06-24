package pe.com.bootcamp.creditservice.service.client;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import pe.com.bootcamp.creditservice.dto.CustomerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerClient {


    @GetExchange("/internal/customer/document-number")
    Mono<CustomerResponse> getCustomerResponse(
            @RequestParam String documentType,
            @RequestParam String documentNumber
    );
}
