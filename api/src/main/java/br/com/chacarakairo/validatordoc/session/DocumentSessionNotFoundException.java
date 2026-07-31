package br.com.chacarakairo.validatordoc.session;

import java.util.UUID;

public class DocumentSessionNotFoundException extends RuntimeException {

    public DocumentSessionNotFoundException(UUID id) {
        super("Sessão documental não encontrada: " + id);
    }
}
