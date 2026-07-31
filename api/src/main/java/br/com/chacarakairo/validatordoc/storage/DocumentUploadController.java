package br.com.chacarakairo.validatordoc.storage;

import br.com.chacarakairo.validatordoc.document.DocumentSlot;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/document-sessions/{sessionId}/files")
public class DocumentUploadController {

    private final DocumentUploadService service;

    public DocumentUploadController(DocumentUploadService service) {
        this.service = service;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<DocumentFileResponse> upload(
        @PathVariable UUID sessionId,
        @RequestParam DocumentSlot slot,
        @RequestPart("file") MultipartFile file
    ) {
        StoredDocumentFile saved = service.upload(sessionId, slot, file);
        return ResponseEntity.created(URI.create("/api/v1/document-sessions/%s/files/%s".formatted(sessionId, saved.getId())))
            .body(DocumentFileResponse.from(saved));
    }
}
