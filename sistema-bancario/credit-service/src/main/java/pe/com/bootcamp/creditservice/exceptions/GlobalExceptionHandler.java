package pe.com.bootcamp.creditservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationException(
            WebExchangeBindException ex,
            ServerWebExchange exchange
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields"
        );

        problemDetail.setTitle("Invalid request");
        problemDetail.setType(URI.create("https://api.customer-service.com/errors/validation-error"));
        problemDetail.setInstance(URI.create(exchange.getRequest().getURI().getPath()));

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        problemDetail.setProperty("errorCode", "VALIDATION_ERROR");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ProblemDetail handleBusinessValidationException(
            BusinessValidationException ex,
            ServerWebExchange exchange
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setTitle("Invalid request");
        problemDetail.setType(URI.create("https://api.customer-service.com/errors/validation-error"));
        problemDetail.setInstance(URI.create(exchange.getRequest().getURI().getPath()));
        problemDetail.setProperty("errorCode", "VALIDATION_ERROR");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("errors", ex.getErrors());

        return problemDetail;
    }



    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex, ServerWebExchange exchange){

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Resource not found");
        problemDetail.setType(URI.create("https://api.customer-service.com/errors/resource-not-found"));
        problemDetail.setInstance(URI.create(exchange.getRequest().getURI().getPath()));

        problemDetail.setProperty("errorCode", "RESOURCE_NOT_FOUND");
        problemDetail.setProperty("timestamp", LocalDateTime.now());


        return problemDetail;

    }

    @ExceptionHandler(PersonalCreditAlreadyExistsException.class)
    public ProblemDetail handlePersonalCreditAlreadyExists(
            PersonalCreditAlreadyExistsException ex,
            ServerWebExchange exchange) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Personal credit already exists");
        problemDetail.setType(URI.create("https://api.account-service.com/errors/personal-credit-already-exists"));
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(
                URI.create(exchange.getRequest().getPath().value())
        );

        problemDetail.setProperty("errorCode", "PERSONAL_CREDIT_ALREADY_EXISTS");
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }

    @ExceptionHandler(AlreadyAccountExistsException.class)
    public ProblemDetail handleAlreadyCustomerExistsException(AlreadyAccountExistsException ex, ServerWebExchange exchange){

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());

        problemDetail.setTitle("account already exists");
        problemDetail.setType(URI.create("https://api.account-service.com/errors/resource-already-exists"));
        problemDetail.setInstance(URI.create(exchange.getRequest().getURI().getPath()));

        problemDetail.setProperty("errorCode", "RESOURCE_ALREADY_EXISTS");
        problemDetail.setProperty("timestamp", LocalDateTime.now());


        return problemDetail;

    }



    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex, ServerWebExchange exchange){

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

        problemDetail.setTitle("Internal server error");
        problemDetail.setType(URI.create("https://api.customer-service.com/errors/internal_server_error"));
        problemDetail.setInstance(URI.create(exchange.getRequest().getURI().getPath()));

        problemDetail.setProperty("errorCode", "INTERNAL_SERVER_ERROR");
        problemDetail.setProperty("timestamp", LocalDateTime.now());


        return problemDetail;

    }

}
