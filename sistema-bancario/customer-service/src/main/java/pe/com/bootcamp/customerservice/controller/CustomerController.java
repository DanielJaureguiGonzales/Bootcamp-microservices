package pe.com.bootcamp.customerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.bootcamp.customerservice.dto.CustomerRequest;
import pe.com.bootcamp.customerservice.dto.CustomerResponse;
import pe.com.bootcamp.customerservice.service.CustomerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Mono<ResponseEntity<Flux<CustomerResponse>>> findAll() {
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(customerService.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerResponse>> findById(@PathVariable String id) {
        return customerService.findById(id)
                .map(customer -> ResponseEntity.status(HttpStatus.OK).body(customer));
    }

    @GetMapping("/documentNumber/{documentNumber}")
    public Mono<ResponseEntity<CustomerResponse>> findByDocumentNumber(@PathVariable String documentNumber) {
        return customerService.findByDocumentNumber(documentNumber)
                .map(customer -> ResponseEntity.status(HttpStatus.OK).body(customer));
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerResponse>> createCustomer(@RequestBody @Valid CustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest)
                .map(customer -> ResponseEntity.status(HttpStatus.OK).body(customer));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(@PathVariable String id, @RequestBody @Valid CustomerRequest customerRequest) {
        return customerService.updateCustomer(id, customerRequest)
                .map(customer -> ResponseEntity.status(HttpStatus.OK).body(customer));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateCustomer(@PathVariable String id) {
        return customerService.deleteCustomer(id)
                .thenReturn(ResponseEntity.noContent().build());
    }


}
