package pe.com.bootcamp.accountservice.dto;


public record AccountParticipantRequest(
        String documentType,
        String documentNumber,
        String participantRole
) {
}
