package pe.com.bootcamp.creditservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.bootcamp.creditservice.dto.*;
import pe.com.bootcamp.creditservice.service.CreditService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping
    public Mono<ResponseEntity<CreditResponse>> createCredit(@Valid @RequestBody CreditRequest creditRequest) {

        return creditService.createCreditRequest(creditRequest)
                .map(creditResponse -> ResponseEntity.status(HttpStatus.CREATED).body(creditResponse));

    }

    @GetMapping("/customer/{customerId}")
    public Mono<ResponseEntity<List<CreditResponse>>> findAllCreditsByCustomerId(@PathVariable
                                                                                     String customerId){
        return creditService.getAllCreditsByCustomerId(customerId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping("consumption")
    public Mono<ResponseEntity<CreditConsumptionResponse>> registerConsumption(@Valid @RequestBody
                                                                                   CreditConsumptionRequest
                                                                                           consumptionRequest) {

        return creditService.registerConsumption(consumptionRequest)
                .map(creditConsumptionResponse -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(creditConsumptionResponse));

    }

    @PostMapping("payment")
    public Mono<ResponseEntity<CreditPaymentResponse>> payCredit(@Valid @RequestBody
                                                                     CreditPaymentRequest creditPaymentRequest) {

        return creditService.payCredit(creditPaymentRequest)
                .map(creditConsumptionResponse -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(creditConsumptionResponse));

    }

}
