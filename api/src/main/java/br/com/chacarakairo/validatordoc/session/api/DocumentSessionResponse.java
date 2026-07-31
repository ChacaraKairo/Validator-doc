package br.com.chacarakairo.validatordoc.session.api;

import br.com.chacarakairo.validatordoc.document.DocumentRequirements;
import br.com.chacarakairo.validatordoc.document.DocumentSlot;
import br.com.chacarakairo.validatordoc.document.DocumentType;
import br.com.chacarakairo.validatordoc.document.RequiredDocumentSlot;
import br.com.chacarakairo.validatordoc.session.DocumentSession;
import br.com.chacarakairo.validatordoc.session.DocumentSessionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record DocumentSessionResponse(
        UUID id,
        String externalPersonId,
        DocumentType documentType,
        DocumentSessionStatus status,
        List<RequiredDocumentSlot> allowedSlots,
        List<Set<DocumentSlot>> completionOptions,
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
                DocumentRequirements.forType(session.getDocumentType()),
                DocumentRequirements.completionOptions(session.getDocumentType()),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                session.getExpiresAt()
        );
    }
}
