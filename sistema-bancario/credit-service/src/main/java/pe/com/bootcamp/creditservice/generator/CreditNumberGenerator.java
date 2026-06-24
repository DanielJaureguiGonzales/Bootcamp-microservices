package pe.com.bootcamp.creditservice.generator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CreditNumberGenerator {

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        long number = 1_000_000_000_000L +
                (long) (random.nextDouble() * 9_000_000_000_000L);

        return String.valueOf(number);
    }

}
