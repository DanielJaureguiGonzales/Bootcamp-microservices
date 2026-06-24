package pe.com.bootcamp.customerservice.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.bootcamp.customerservice.dto.CustomerResponse;
import pe.com.bootcamp.customerservice.dto.CustomerSummaryResponse;
import pe.com.bootcamp.customerservice.dto.DocumentNumbersRequest;
import pe.com.bootcamp.customerservice.model.Customer;
import pe.com.bootcamp.customerservice.service.CustomerService;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/internal/customer")
@RestController
@RequiredArgsConstructor
public class InternalController {

    private final CustomerService customerService;

    @PostMapping("/document-numbers/search")
    public Mono<List<CustomerSummaryResponse>> searchCustomerReponse(@RequestBody @Valid DocumentNumbersRequest documentNumbersRequests) {
        return customerService.findByDocumentNumbers(documentNumbersRequests);

    }

    @GetMapping("/document-number")
    public Mono<CustomerResponse> getCustomerResponse(
            @RequestParam String documentType,
            @RequestParam String documentNumber
    ){
        return customerService.findByDocumentNumberAndDocumentType(documentType, documentNumber);
    }
}
