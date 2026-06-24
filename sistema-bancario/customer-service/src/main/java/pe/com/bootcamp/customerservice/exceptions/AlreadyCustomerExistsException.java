package pe.com.bootcamp.customerservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyCustomerExistsException extends RuntimeException{

    public AlreadyCustomerExistsException(String message) {
        super(message);
    }
}
