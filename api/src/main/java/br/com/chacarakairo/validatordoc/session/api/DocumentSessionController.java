package br.com.chacarakairo.validatordoc.session.api;

import br.com.chacarakairo.validatordoc.session.DocumentSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/document-sessions")
public class DocumentSessionController {

    private final DocumentSessionService service;

    public DocumentSessionController(DocumentSessionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentSessionResponse create(@Valid @RequestBody CreateDocumentSessionRequest request) {
        return DocumentSessionResponse.from(service.create(request));
    }

    @GetMapping("/{id}")
    public DocumentSessionResponse findById(@PathVariable UUID id) {
        return DocumentSessionResponse.from(service.findById(id));
    }
}
