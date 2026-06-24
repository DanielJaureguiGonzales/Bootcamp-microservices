package pe.com.bootcamp.accountservice.generator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        long number = 1_000_000_000_000L +
                (long) (random.nextDouble() * 9_000_000_000_000L);

        return String.valueOf(number);
    }

}
