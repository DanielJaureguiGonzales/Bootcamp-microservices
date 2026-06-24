package pe.com.bootcamp.customerservice.dto;

import java.util.List;

public record DocumentNumbersRequest(
        List<String> documentNumbers
) {

}
