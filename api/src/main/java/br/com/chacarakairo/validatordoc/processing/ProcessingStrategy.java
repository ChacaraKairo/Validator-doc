package br.com.chacarakairo.validatordoc.processing;

import br.com.chacarakairo.validatordoc.document.DocumentType;

public final class ProcessingStrategy {
    private ProcessingStrategy() {
    }

    public static String forType(DocumentType type) {
        return type == DocumentType.RESIDENCE_PROOF ? "STORAGE_ONLY" : "PENDING_INTEGRATION";
    }
}
