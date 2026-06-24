package pe.com.bootcamp.creditservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonalCreditAlreadyExistsException extends RuntimeException {
    public PersonalCreditAlreadyExistsException(String customerId) {
        super("The personal customer already has an existing credit. customerId: " + customerId);
    }
}