package br.com.chacarakairo.validatordoc.session;

import br.com.chacarakairo.validatordoc.session.api.CreateDocumentSessionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class DocumentSessionService {

    private static final Duration DEFAULT_SESSION_TTL = Duration.ofHours(24);

    private final DocumentSessionRepository repository;
    private final Clock clock;

    public DocumentSessionService(DocumentSessionRepository repository) {
        this(repository, Clock.systemUTC());
    }

    DocumentSessionService(DocumentSessionRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public DocumentSession create(CreateDocumentSessionRequest request) {
        Instant now = clock.instant();
        DocumentSession session = new DocumentSession(
                request.externalPersonId().trim(),
                request.documentType(),
                now,
                now.plus(DEFAULT_SESSION_TTL)
        );
        return repository.save(session);
    }

    @Transactional(readOnly = true)
    public DocumentSession findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DocumentSessionNotFoundException(id));
    }
}
