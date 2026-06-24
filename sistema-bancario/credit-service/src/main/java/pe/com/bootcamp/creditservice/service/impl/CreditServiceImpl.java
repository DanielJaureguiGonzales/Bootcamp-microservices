package pe.com.bootcamp.creditservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.com.bootcamp.creditservice.dto.*;
import pe.com.bootcamp.creditservice.exceptions.BusinessValidationException;
import pe.com.bootcamp.creditservice.exceptions.PersonalCreditAlreadyExistsException;
import pe.com.bootcamp.creditservice.exceptions.ResourceNotFoundException;
import pe.com.bootcamp.creditservice.generator.CreditNumberGenerator;
import pe.com.bootcamp.creditservice.model.Credit;
import pe.com.bootcamp.creditservice.model.CreditConsumption;
import pe.com.bootcamp.creditservice.model.CreditPayment;
import pe.com.bootcamp.creditservice.model.CurrencyName;
import pe.com.bootcamp.creditservice.repository.CreditConsumptionRepository;
import pe.com.bootcamp.creditservice.repository.CreditPaymentRepository;
import pe.com.bootcamp.creditservice.repository.CreditRepository;
import pe.com.bootcamp.creditservice.service.CreditService;
import pe.com.bootcamp.creditservice.service.rest.CustomerResponseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final CreditConsumptionRepository creditConsumptionRepository;
    private final CreditPaymentRepository creditPaymentRepository;
    private final CustomerResponseClient client;
    private final CreditNumberGenerator creditNumberGenerator;

    @Override
    public Flux<CreditResponse> getAllCreditsByCustomerId(String customerId) {

        return creditRepository.findByCustomerIdAndStatus(customerId, true)
                .switchIfEmpty(Flux.error(new ResourceNotFoundException("Credit", "customerId", customerId)))
                .map(this::mapToCreditResponse);
    }

    @Override
    public Mono<CreditResponse> createCreditRequest(CreditRequest creditRequest) {
        Map<String, String> errors = new HashMap<>();
        validateDocument(
                errors,
                "documentType",
                "documentNumber",
                creditRequest.documentType(),
                creditRequest.documentNumber()
        );

        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(errors));
        }

        return client.getCustomerResponseByCustomer(creditRequest.documentNumber(), creditRequest.documentType())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer","documentNumber",
                        creditRequest.documentNumber())))
                .flatMap(customerResponse -> validateCustomer(customerResponse)
                        .thenReturn(customerResponse))
                .flatMap(customerResponse -> createCredit(creditRequest, customerResponse));
    }

    @Override
    public Mono<CreditConsumptionResponse> registerConsumption(CreditConsumptionRequest consumptionRequest) {
        Map<String, String> errors = new HashMap<>();
        validateDocument(
                errors,
                "documentType",
                "documentNumber",
                consumptionRequest.documentType(),
                consumptionRequest.documentNumber()
        );

        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(errors));
        }

        return client.getCustomerResponseByCustomer(consumptionRequest.documentNumber(), consumptionRequest.documentType())
                .flatMap(customerResponse ->
                        creditRepository.findByCreditNumberAndStatus(consumptionRequest.creditNumber(), true)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                                        "Credit",
                                        "creditNumber",
                                        consumptionRequest.creditNumber()
                                )))
                                .flatMap(credit -> validateAndConsumeCreditCard(
                                        credit,
                                        customerResponse.id(),
                                        consumptionRequest
                                ))
                );
    }

    @Override
    public Mono<CreditPaymentResponse> payCredit(CreditPaymentRequest creditPaymentRequest) {
        Map<String, String> errors = new HashMap<>();
        validateDocument(
                errors,
                "documentType",
                "documentNumber",
                creditPaymentRequest.documentType(),
                creditPaymentRequest.documentNumber()
        );

        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(errors));
        }

        return client.getCustomerResponseByCustomer(creditPaymentRequest.documentNumber(),
                creditPaymentRequest.documentType())
                .flatMap(customerResponse -> creditRepository
                        .findByCreditNumberAndStatus(creditPaymentRequest.creditNumber(), true)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                                "Credit",
                                "creditNumber",
                                creditPaymentRequest.creditNumber()
                        )))
                        .flatMap(credit -> validateAndPayCredit(credit, customerResponse.id(), creditPaymentRequest))
                );

    }

    private Mono<CreditPaymentResponse> validateAndPayCredit(
            Credit credit,
            String customerId,
            CreditPaymentRequest request
    ) {

        // ESTO PARA ASEGURAR QUE EL USUARIO USE SU CREDITO Y NO DE OTRO.
        if (!customerId.equals(credit.getCustomerId())) {
            return Mono.error(new RuntimeException(
                    "Credit product does not belong to customer"
            ));
        }

        BigDecimal currentDebt = credit.getUsedAmount() == null
                ? BigDecimal.ZERO
                : credit.getUsedAmount();

        // EVITAR QUE PAGUE UN CREDITO SIN PENDIENTES
        if (currentDebt.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new RuntimeException(
                    "Credit product has no pending debt"
            ));
        }

        // EVITAR QUE EL PAGO SEA MAYOR A LA DEUDA ACTUAL
        if (request.amount().compareTo(currentDebt) > 0) {
            return Mono.error(new RuntimeException(
                    "Payment amount cannot be greater than current debt"
            ));
        }

        BigDecimal previousUsedAmount = credit.getUsedAmount();
        BigDecimal newUsedAmount = previousUsedAmount.subtract(request.amount());

        BigDecimal newBalance = credit.getCreditLimit().subtract(newUsedAmount);

        credit.setUsedAmount(newUsedAmount);
        credit.setBalance(newBalance);

        CreditPayment payment = new CreditPayment();
        payment.setCreditId(credit.getCreditId());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setAmount(request.amount());
        payment.setDescription(request.description());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(true);

        return creditRepository.save(credit)
                .then(creditPaymentRepository.save(payment))
                .thenReturn(new CreditPaymentResponse(
                        credit.getCreditNumber(),
                        request.amount(),
                        currentDebt,
                        newUsedAmount,
                        "COMPLETED",
                        "Credit payment completed successfully"
                ));
    }


    private Mono<CreditConsumptionResponse> validateAndConsumeCreditCard(
            Credit credit,
            String customerId,
            CreditConsumptionRequest request
    ) {

        if (!customerId.equals(credit.getCustomerId())) {
            return Mono.error(new RuntimeException(
                    "Credit card does not belong to customer"
            ));
        }


        BigDecimal usedAmount = credit.getUsedAmount();

        BigDecimal availableAmount = credit.getCreditLimit().subtract(usedAmount);

        if (request.amount().compareTo(availableAmount) > 0) {
            return Mono.error(new RuntimeException(
                    "Insufficient credit limit"
            ));
        }

        BigDecimal previousUsedAmount = credit.getUsedAmount();
        BigDecimal newUsedAmount = previousUsedAmount.add(request.amount());

        BigDecimal newBalance = credit.getCreditLimit().subtract(newUsedAmount);

        credit.setUsedAmount(newUsedAmount);
        credit.setBalance(newBalance);

        CreditConsumption consumption = new CreditConsumption();
        consumption.setCreditId(credit.getCreditId());
        consumption.setCustomerId(customerId);
        consumption.setAmount(request.amount());
        consumption.setMerchantName(request.merchantName());
        consumption.setDescription(request.description());
        consumption.setConsumptionDate(LocalDateTime.now());
        consumption.setStatus(true);

        return creditRepository.save(credit)
                .then(creditConsumptionRepository.save(consumption))
                .thenReturn(new CreditConsumptionResponse(
                        credit.getCreditNumber(),
                        request.amount(),
                        credit.getCreditLimit(),
                        newUsedAmount,
                        newBalance,
                        "COMPLETED",
                        "Credit card consumption completed successfully"
                ));
    }

    private void validateDocument(
            Map<String, String> errors,
            String documentTypeField,
            String documentNumberField,
            String documentType,
            String documentNumber
    ) {

        if (documentType == null || documentType.isBlank()) {
            errors.put(documentTypeField, "Document type is required");
            return;
        }

        String cleanDocumentType = documentType.trim();

        if (!List.of("01", "02").contains(cleanDocumentType)) {
            errors.put(
                    documentTypeField,
                    "Document type must be 01 for PERSONAL or 02 for BUSINESS"
            );
            return;
        }

        if (documentNumber == null || documentNumber.isBlank()) {
            errors.put(documentNumberField, "Document number is required");
            return;
        }

        String cleanDocumentNumber = documentNumber.trim();

        if ("01".equals(cleanDocumentType) && !cleanDocumentNumber.matches("^[0-9]{8}$")) {
            errors.put(
                    documentNumberField,
                    "Personal document number must contain exactly 8 digits"
            );
        }

        if ("02".equals(cleanDocumentType) && !cleanDocumentNumber.matches("^[0-9]{11}$")) {
            errors.put(
                    documentNumberField,
                    "Business document number must contain exactly 11 digits"
            );
        }
    }

    private Mono<Void> validateCustomer(CustomerResponse customerResponse){

        if ("02".equals(customerResponse.documentType())){
            return Mono.empty();
        }

        return creditRepository.existsByCustomerIdAndStatus(customerResponse.id(), true)
                .flatMap(existCustomerCreatedBefore -> {
                    if (existCustomerCreatedBefore){
                        return Mono.error(new PersonalCreditAlreadyExistsException(customerResponse.id()));
                    }
                    return Mono.empty();
                });
    }

    private Mono<CreditResponse> createCredit(CreditRequest creditRequest, CustomerResponse customerResponse){
        return Mono.just(new Credit())
                .doOnNext(credit ->
                    mapCreditByCreditRequestAndCustomerResponse(creditRequest, customerResponse, credit))
                .flatMap(creditRepository::save)
                .map(this::mapToCreditResponse);


    }

    private CreditResponse mapToCreditResponse(Credit credit) {

        return CreditResponse.builder()
                .creditId(credit.getCreditId())
                .customerId(credit.getCustomerId())
                .creditNumber(credit.getCreditNumber())
                .balance(credit.getBalance())
                .creditLimit(credit.getCreditLimit())
                .usedAmount(credit.getUsedAmount())
                .currencyName(credit.getCurrencyName())
                .exchangeRate(credit.getExchangeRate())
                .annualInterestRate(credit.getAnnualInterestRate())
                .build();

    }

    private void mapCreditByCreditRequestAndCustomerResponse(CreditRequest creditRequest, CustomerResponse customerResponse, Credit credit) {
        credit.setCustomerId(customerResponse.id());
        credit.setCreditNumber(creditNumberGenerator.generate());
        credit.setBalance(creditRequest.amount());
        credit.setUsedAmount(BigDecimal.ZERO);
        credit.setCreditLimit(creditRequest.amount());
        credit.setCurrencyType(creditRequest.currencyType());
        credit.setCurrencyName(CurrencyName.getNameByCode(creditRequest.currencyType()));
        if (CurrencyName.SOLES.name().equals(credit.getCurrencyName())){
            credit.setExchangeRate(BigDecimal.ONE);
            credit.setAnnualInterestRate(new BigDecimal("87.76"));
        } else if (CurrencyName.DOLAR.name().equals(credit.getCurrencyName())) {
            credit.setExchangeRate(new BigDecimal("3.38"));
            credit.setAnnualInterestRate(new BigDecimal("87.76"));
        }
        credit.setOpeningDate(LocalDateTime.now());
        credit.setStatus(true);
    }
}
