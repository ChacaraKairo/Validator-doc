package br.com.chacarakairo.validatordoc.storage;

import br.com.chacarakairo.validatordoc.document.DocumentRequirements;
import br.com.chacarakairo.validatordoc.document.DocumentSlot;
import br.com.chacarakairo.validatordoc.processing.ProcessingJob;
import br.com.chacarakairo.validatordoc.processing.ProcessingQueue;
import br.com.chacarakairo.validatordoc.processing.ProcessingStrategy;
import br.com.chacarakairo.validatordoc.session.DocumentSession;
import br.com.chacarakairo.validatordoc.session.DocumentSessionRepository;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentUploadService {

    private final DocumentSessionRepository sessionRepository;
    private final StoredDocumentFileRepository fileRepository;
    private final DocumentFileInspector inspector;
    private final ObjectStorage storage;
    private final ProcessingQueue queue;

    public DocumentUploadService(DocumentSessionRepository sessionRepository,
                                 StoredDocumentFileRepository fileRepository,
                                 DocumentFileInspector inspector,
                                 ObjectStorage storage,
                                 ProcessingQueue queue) {
        this.sessionRepository = sessionRepository;
        this.fileRepository = fileRepository;
        this.inspector = inspector;
        this.storage = storage;
        this.queue = queue;
    }

    @Transactional
    public StoredDocumentFile upload(UUID sessionId, DocumentSlot slot, MultipartFile multipartFile) {
        DocumentSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Sessão documental não encontrada."));

        Set<String> acceptedTypes = DocumentRequirements.acceptedMediaTypes(session.getDocumentType(), slot);
        if (acceptedTypes.isEmpty()) {
            throw new IllegalArgumentException("Slot não permitido para este tipo documental.");
        }

        InspectedFile inspected = inspector.inspect(multipartFile);
        if (!acceptedTypes.contains(inspected.inspection().detectedMediaType())) {
            throw new IllegalArgumentException("Formato não aceito para este slot.");
        }
        if (fileRepository.existsBySessionIdAndSha256(sessionId, inspected.inspection().sha256())) {
            throw new IllegalArgumentException("Este arquivo já foi enviado para a sessão.");
        }
        if (fileRepository.findBySessionIdAndSlot(sessionId, slot).isPresent()) {
            throw new IllegalArgumentException("O slot já possui um arquivo.");
        }

        UUID fileId = UUID.randomUUID();
        String objectKey = "sessions/%s/%s/%s".formatted(sessionId, slot.name().toLowerCase(), fileId);
        StoredObject object = storage.put(objectKey, inspected.inspection().detectedMediaType(),
            inspected.inspection().sizeBytes(), new ByteArrayInputStream(inspected.bytes()));

        StoredDocumentFile storedFile = new StoredDocumentFile(
            fileId,
            session,
            slot,
            sanitizeFilename(multipartFile.getOriginalFilename()),
            inspected.inspection().detectedMediaType(),
            inspected.inspection().sizeBytes(),
            inspected.inspection().sha256(),
            object.bucket(),
            object.key(),
            OffsetDateTime.now()
        );

        try {
            StoredDocumentFile saved = fileRepository.saveAndFlush(storedFile);
            updateSessionIfComplete(session);
            return saved;
        } catch (RuntimeException exception) {
            storage.delete(object.key());
            throw exception;
        }
    }

    private void updateSessionIfComplete(DocumentSession session) {
        Set<DocumentSlot> uploadedSlots = fileRepository.findBySessionId(session.getId()).stream()
            .map(StoredDocumentFile::getSlot)
            .collect(Collectors.toSet());

        if (DocumentRequirements.isComplete(session.getDocumentType(), uploadedSlots)) {
            session.markUploaded(Instant.now());
            sessionRepository.save(session);
            queue.publish(new ProcessingJob(
                session.getId(),
                ProcessingStrategy.forType(session.getDocumentType()),
                OffsetDateTime.now()
            ));
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "document";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
