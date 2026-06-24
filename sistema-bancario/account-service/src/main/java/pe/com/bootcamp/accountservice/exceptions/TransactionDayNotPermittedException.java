package pe.com.bootcamp.accountservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionDayNotPermittedException extends RuntimeException{

    public TransactionDayNotPermittedException(String message) {
        super(message);
    }
}
