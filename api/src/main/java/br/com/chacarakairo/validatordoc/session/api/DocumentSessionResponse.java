package br.com.chacarakairo.validatordoc.session.api;

import br.com.chacarakairo.validatordoc.document.DocumentType;
import br.com.chacarakairo.validatordoc.session.DocumentSession;
import br.com.chacarakairo.validatordoc.session.DocumentSessionStatus;

import java.time.Instant;
import java.util.UUID;

public record DocumentSessionResponse(
        UUID id,
        String externalPersonId,
        DocumentType documentType,
        DocumentSessionStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant expiresAt
) {
    public static DocumentSessionResponse from(DocumentSession session) {
        return new DocumentSessionResponse(
                session.getId(),
                session.getExternalPersonId(),
                session.getDocumentType(),
                session.getStatus(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                session.getExpiresAt()
        );
    }
}
