package br.com.chacarakairo.validatordoc.session.api;

import br.com.chacarakairo.validatordoc.document.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDocumentSessionRequest(
        @NotBlank @Size(max = 120) String externalPersonId,
        @NotNull DocumentType documentType
) {
}
