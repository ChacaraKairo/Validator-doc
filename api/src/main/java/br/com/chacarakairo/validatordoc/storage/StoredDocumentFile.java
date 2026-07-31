package br.com.chacarakairo.validatordoc.storage;

import br.com.chacarakairo.validatordoc.document.DocumentSlot;
import br.com.chacarakairo.validatordoc.session.DocumentSession;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_files", uniqueConstraints = {
    @UniqueConstraint(name = "uk_document_files_session_slot", columnNames = {"session_id", "slot"}),
    @UniqueConstraint(name = "uk_document_files_session_sha256", columnNames = {"session_id", "sha256"})
})
public class StoredDocumentFile {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private DocumentSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DocumentSlot slot;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "media_type", nullable = false, length = 100)
    private String mediaType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(nullable = false, length = 64)
    private String sha256;

    @Column(name = "storage_bucket", nullable = false, length = 100)
    private String storageBucket;

    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected StoredDocumentFile() {
    }

    public StoredDocumentFile(UUID id, DocumentSession session, DocumentSlot slot, String originalFilename,
                              String mediaType, long sizeBytes, String sha256, String storageBucket,
                              String storageKey, OffsetDateTime createdAt) {
        this.id = id;
        this.session = session;
        this.slot = slot;
        this.originalFilename = originalFilename;
        this.mediaType = mediaType;
        this.sizeBytes = sizeBytes;
        this.sha256 = sha256;
        this.storageBucket = storageBucket;
        this.storageKey = storageKey;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public DocumentSession getSession() { return session; }
    public DocumentSlot getSlot() { return slot; }
    public String getOriginalFilename() { return originalFilename; }
    public String getMediaType() { return mediaType; }
    public long getSizeBytes() { return sizeBytes; }
    public String getSha256() { return sha256; }
    public String getStorageBucket() { return storageBucket; }
    public String getStorageKey() { return storageKey; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
