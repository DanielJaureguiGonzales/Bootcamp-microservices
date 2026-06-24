package pe.com.bootcamp.creditservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyAccountExistsException extends RuntimeException{

    public AlreadyAccountExistsException(String message) {
        super(message);
    }
}
