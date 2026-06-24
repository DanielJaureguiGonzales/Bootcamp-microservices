package pe.com.bootcamp.accountservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pe.com.bootcamp.accountservice.dto.*;
import pe.com.bootcamp.accountservice.exceptions.BusinessValidationException;
import pe.com.bootcamp.accountservice.exceptions.ResourceNotFoundException;
import pe.com.bootcamp.accountservice.generator.AccountNumberGenerator;
import pe.com.bootcamp.accountservice.model.Account;
import pe.com.bootcamp.accountservice.model.AccountParticipant;
import pe.com.bootcamp.accountservice.model.Transaction;
import pe.com.bootcamp.accountservice.repository.AccountParticipantRepository;
import pe.com.bootcamp.accountservice.repository.AccountRepository;
import pe.com.bootcamp.accountservice.repository.TransactionsRepository;
import pe.com.bootcamp.accountservice.service.AccountService;
import pe.com.bootcamp.accountservice.service.rest.CustomerResponseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountParticipantRepository accountParticipantRepository;
    private final TransactionsRepository transactionsRepository;
    private final CustomerResponseClient client;
    private final AccountNumberGenerator accountNumberGenerator;


    @Override
    public Mono<AccountResponse> createAccountCustomer(AccountRequest accountRequest) {

        return validateAccountRequest(accountRequest)
                .then(client.getCustomerResponse(accountRequest))
                .flatMap(customerResponse -> {

                    log.info("Petición de cliente recibido {}", customerResponse);

                    return validateAccountByCustomerType(customerResponse, accountRequest)
                            .flatMap(validationResult ->
                                    createAccountAndParticipant(
                                            customerResponse,
                                            accountRequest,
                                            validationResult
                                    )
                            );
                })
                .map(account -> new AccountResponse(
                        account.getAccountId(),
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getBalance(),
                        account.getStatus()
                ));


    }

    private Mono<Void> validateAccountRequest(AccountRequest request) {

        Map<String, String> errors = new HashMap<>();

        validateDocument(
                errors,
                "documentType",
                "documentNumber",
                request.documentType(),
                request.documentNumber()
        );

        List<AccountParticipantRequest> participants =
                request.participants() == null ? List.of() : request.participants();

        Set<String> documentNumbers = new HashSet<>();

        if (request.documentNumber() != null && !request.documentNumber().isBlank()) {
            documentNumbers.add(request.documentNumber().trim());
        }

        for (int i = 0; i < participants.size(); i++) {

            AccountParticipantRequest participant = participants.get(i);

            if (participant == null) {
                errors.put("participants[" + i + "]", "Participant is required");
                continue;
            }

            String prefix = "participants[" + i + "]";

            validateDocument(
                    errors,
                    prefix + ".documentType",
                    prefix + ".documentNumber",
                    participant.documentType(),
                    participant.documentNumber()
            );

            validateParticipantRole(
                    errors,
                    prefix + ".participantRole",
                    participant.participantRole()
            );

            if (participant.documentNumber() != null && !participant.documentNumber().isBlank()) {

                String participantDocumentNumber = participant.documentNumber().trim();

                if (!documentNumbers.add(participantDocumentNumber)) {
                    errors.put(
                            prefix + ".documentNumber",
                            "Participant document number must not be duplicated or equal to the main customer document number"
                    );
                }
            }
        }

        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(errors));
        }

        return Mono.empty();
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

    private void validateParticipantRole(
            Map<String, String> errors,
            String participantRoleField,
            String participantRole
    ) {

        if (participantRole == null || participantRole.isBlank()) {
            errors.put(participantRoleField, "Participant role is required");
            return;
        }

        String cleanRole = participantRole.toUpperCase().trim();

        if (!List.of("HOLDER", "AUTHORIZED_SIGNER").contains(cleanRole)) {
            errors.put(
                    participantRoleField,
                    "Participant role must be HOLDER or AUTHORIZED_SIGNER"
            );
        }
    }



    @Override
    public Mono<OperationCompleted> transactions(OperationRequest operationRequest, String idOperation) {

        return validateOperationRequest(operationRequest)
                .then(client.getCustomerResponseByCustomer(
                        operationRequest.documentNumber(),
                        operationRequest.documentType()
                ))
                .flatMap(customerResponse -> {
                    String operation = idOperation.toUpperCase().trim();

                    return accountRepository.findByAccountNumber(operationRequest.accountNumber())
                            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account","accountNumber",
                                    operationRequest.accountNumber())))
                            .flatMap(account -> switch (operation){
                                case "DEPOSIT" ->  depositOperation(operationRequest, account,
                                        operation,
                                        customerResponse.id());
                                case "WITHDRAW" -> withDrawOperation(operationRequest, account, operation,
                                        customerResponse.id());
                                default -> Mono.error(new RuntimeException("Invalid Operation Id "+ idOperation));
                            });


                });

    }

    @Override
    public Flux<AccountResponse> getAccountByDocumentNumber(String documentNumber, String documentType) {

        Map<String, String> errors = new HashMap<>();

        validateDocument(
                errors,
                "documentType",
                "documentNumber",
                documentType,
                documentNumber
        );

        if (!errors.isEmpty()) {
            return Flux.error(new BusinessValidationException(errors));
        }


        return client.getCustomerResponseByCustomer(documentNumber, documentType)
                .flatMapMany(customerResponse -> accountParticipantRepository.findByCustomerIdAndStatus(customerResponse.id(), true))
                .flatMap(accountParticipant -> accountRepository.findById(accountParticipant.getAccountId()))
                .map(account -> AccountResponse.builder()
                        .accountId(account.getAccountId())
                        .accountNumber(account.getAccountNumber())
                        .accountType(account.getAccountType())
                        .balance(account.getBalance())
                        .status(account.getStatus())
                        .build());

    }

    private Mono<Void> validateOperationRequest(OperationRequest request) {

        Map<String, String> errors = new HashMap<>();

        validateDocument(
                errors,
                "documentType",
                "documentNumber",
                request.documentType(),
                request.documentNumber()
        );


        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(errors));
        }

        return Mono.empty();
    }

    private Mono<OperationCompleted> withDrawOperation(OperationRequest operationRequest, Account account, String operation,
                                                       String customerId) {

        if (operationRequest.amount().compareTo(account.getBalance()) > 0){
            return Mono.error(new RuntimeException("Insufficient balance"));
        }

        account.setBalance(account.getBalance().subtract(operationRequest.amount()));

        return saveTransaction(operationRequest, account, operation, customerId);

    }

    private Mono<OperationCompleted> depositOperation(OperationRequest operationRequest, Account account,
                                                      String operation,
                                                      String customerId) {

        account.setBalance(account.getBalance().add(operationRequest.amount()));
        return saveTransaction(operationRequest, account, operation, customerId);

    }

    @NonNull
    private Mono<OperationCompleted> saveTransaction(OperationRequest operationRequest, Account account,
                                                    String operation,
                                       String customerId) {
        return accountRepository.save(account).flatMap(accountSaved -> {
                    Transaction newTransaction = Transaction.builder()
                            .transactionDate(LocalDateTime.now())
                            .accountId(accountSaved.getAccountId())
                            .transactionType(operation)
                            .customerId(customerId)
                            .status(true)
                            .amount(operationRequest.amount())
                            .build();
                    return transactionsRepository.save(newTransaction);

                })
                .map(transaction ->
                        OperationCompleted.builder()
                                .accountId(transaction.getAccountId())
                                .customerId(transaction.getCustomerId())
                                .operation(operation)
                                .transactionDate(transaction.getTransactionDate())
                                .amount(transaction.getAmount())
                                .build());
    }




    private Mono<AccountValidationResult> validateAccountByCustomerType(
            CustomerResponse customerResponse,
            AccountRequest accountRequest
    ) {
        String customerType = customerResponse.documentType().toUpperCase().trim();

        return switch (customerType) {
            // 02 -> BUSINESS
            case "02" -> validateAccountBusinessCustomer(accountRequest)
                    .map(AccountValidationResult::new);

            // 01 -> PERSONAL
            case "01" -> validateAccountMaxPerCustomer(customerResponse, accountRequest)
                    .thenReturn(AccountValidationResult.empty());

            default -> Mono.error(new RuntimeException("Invalid customer type: " + customerType));
        };
    }

    private Mono<List<CustomerSummaryResponse>> validateAccountBusinessCustomer(AccountRequest request) {

        String accountType = request.accountType().toUpperCase().trim();

        if (!"02".equals(accountType)) {
            return Mono.error(new RuntimeException(
                    "Business customers can only create checking accounts"
            ));
        }
        List<AccountParticipantRequest> participants =
                request.participants() == null ? List.of() : request.participants();

        if (participants.isEmpty()) {
            return Mono.just(List.of());
        }

        List<String> documentNumbers = participants.stream()
                .map(AccountParticipantRequest::documentNumber)
                .map(String::trim)
                .distinct()
                .toList();

        return
                client.getCustomerSummaryList(
                        DocumentNumbersRequest.builder()
                                .documentNumbers(documentNumbers)
                                .build()
                )
                .flatMap(customers -> {

                    if (customers.size() != documentNumbers.size()) {
                        return Mono.error(new RuntimeException(
                                "Deben existir todos los usuarios en el sistema"
                        ));
                    }

                    return Mono.just(customers);
                });
    }



    private Mono<Void> validateAccountMaxPerCustomer(CustomerResponse customer, AccountRequest request) {

        // 01 -> SAVINGS
        // 02 -> CHECKING
        // 03 -> FIXED_TERM
        if ("03".equals(request.accountType())) {
            return Mono.empty();
        }

        return accountParticipantRepository.findByCustomerIdAndParticipantRoleAndStatus(customer.id(),"HOLDER", true)
                .switchIfEmpty(Mono.empty())
                .map(AccountParticipant::getAccountId)
                .collectList()
                .flatMap(ids ->
                    accountRepository.countByAccountIdInAndAccountTypeAndStatus(ids, request.accountType(),true)
                ).flatMap(count -> {
                    if (count>0){

                        String accountTypeName = switch (request.accountType()) {
                            case "01" -> "SAVINGS";
                            case "02" -> "CHECKING";
                            default -> "UNKNOWN";
                        };

                        return Mono.error(new RuntimeException("No se permite tener más cuentas de tipo: " +
                                accountTypeName
                                ));
                    }
                    return Mono.empty();
                })
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .then();

    }

    private Mono<Account> createAccountAndParticipant(
            CustomerResponse customer,
            AccountRequest accountRequest,
            AccountValidationResult validationResult
    ) {
        LocalDateTime initialDateFixTerm = null;

        if ("03".equals(accountRequest.accountType())){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate fecha = LocalDate.parse(accountRequest.initialDate(), formatter);

            initialDateFixTerm = fecha.atStartOfDay();
        }


        Account account = Account.builder()
                .accountNumber(accountNumberGenerator.generate())
                .accountType(accountRequest.accountType())
                .balance(accountRequest.initialAmount())
                .status(true)
                .openingDate(LocalDateTime.now())
                .flagFreeCommisionMant(accountRequest.flagFreeCommisionMant())
                .maxMovMon(accountRequest.maxMovMon())
                .initialDate(initialDateFixTerm)
                .cantDays(accountRequest.cantDays())
                .build();

        return accountRepository.save(account)
                .flatMap(savedAccount ->
                        createParticipants(
                                savedAccount,
                                customer,
                                accountRequest,
                                validationResult
                        ).thenReturn(savedAccount)
                );
    }

    private Mono<Void> createParticipants(
            Account savedAccount,
            CustomerResponse mainCustomer,
            AccountRequest accountRequest,
            AccountValidationResult validationResult
    ) {
        String customerType = mainCustomer.documentType().trim();

        return switch (customerType) {
            // 01 -> PERSONAL
            case "01" -> createPersonalParticipants(savedAccount, mainCustomer);
            // 02 -> BUSINESS
            case "02" -> createBusinessParticipants(
                    savedAccount,
                    mainCustomer,
                    accountRequest,
                    validationResult
            );
            default -> Mono.error(new RuntimeException("Invalid customer type"));
        };
    }

    private Mono<Void> createPersonalParticipants(
            Account savedAccount,
            CustomerResponse mainCustomer
    ) {
        AccountParticipant participant = buildParticipant(
                savedAccount.getAccountId(),
                mainCustomer.id(),
                "HOLDER"
        );

        return accountParticipantRepository.save(participant).then();
    }


    private AccountParticipant buildParticipant(String accountId, String customerId, String participantRole) {

        AccountParticipant participant = new AccountParticipant();
        participant.setAccountId(accountId);
        participant.setCustomerId(customerId);
        participant.setParticipantRole(participantRole);
        participant.setRegistrationDate(LocalDateTime.now());
        participant.setStatus(true);

        return participant;

    }

    private Mono<Void> createBusinessParticipants(
            Account savedAccount,
            CustomerResponse mainCustomer,
            AccountRequest accountRequest,
            AccountValidationResult validationResult
    ) {
        List<AccountParticipant> participantsToSave = new ArrayList<>();

        AccountParticipant mainHolder = buildParticipant(
                savedAccount.getAccountId(),
                mainCustomer.id(),
                "HOLDER"
        );

        participantsToSave.add(mainHolder);

        List<AccountParticipantRequest> requestParticipants =
                accountRequest.participants() == null
                        ? List.of()
                        : accountRequest.participants();

        Map<String, CustomerSummaryResponse> customerMap =
                validationResult.participantCustomers()
                        .stream()
                        .collect(Collectors.toMap(
                                CustomerSummaryResponse::documentNumber,
                                Function.identity()
                        ));

        requestParticipants.forEach(participantRequest -> {

            CustomerSummaryResponse participantCustomer =
                    customerMap.get(participantRequest.documentNumber().trim());

            AccountParticipant participant = buildParticipant(
                    savedAccount.getAccountId(),
                    participantCustomer.id(),
                    participantRequest.participantRole()
            );

            participantsToSave.add(participant);
        });

        return accountParticipantRepository.saveAll(participantsToSave).then();
    }



}
