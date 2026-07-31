package br.com.chacarakairo.validatordoc.storage;

import br.com.chacarakairo.validatordoc.document.DocumentSlot;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentFileResponse(
    UUID id,
    DocumentSlot slot,
    String originalFilename,
    String mediaType,
    long sizeBytes,
    String sha256,
    OffsetDateTime createdAt
) {
    public static DocumentFileResponse from(StoredDocumentFile file) {
        return new DocumentFileResponse(file.getId(), file.getSlot(), file.getOriginalFilename(),
            file.getMediaType(), file.getSizeBytes(), file.getSha256(), file.getCreatedAt());
    }
}
