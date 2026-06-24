package pe.com.bootcamp.accountservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DocumentNumbersRequest(
        List<String> documentNumbers
) {

}
