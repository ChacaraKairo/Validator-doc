package br.com.chacarakairo.validatordoc.storage;

import br.com.chacarakairo.validatordoc.document.DocumentSlot;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredDocumentFileRepository extends JpaRepository<StoredDocumentFile, UUID> {
    List<StoredDocumentFile> findBySessionId(UUID sessionId);
    Optional<StoredDocumentFile> findBySessionIdAndSlot(UUID sessionId, DocumentSlot slot);
    boolean existsBySessionIdAndSha256(UUID sessionId, String sha256);
}
