package br.com.chacarakairo.validatordoc.document;

import java.util.Set;

public record RequiredDocumentSlot(
    DocumentSlot slot,
    Set<String> acceptedMediaTypes
) {
}
