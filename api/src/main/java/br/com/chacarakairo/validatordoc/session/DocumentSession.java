package br.com.chacarakairo.validatordoc.session;

import br.com.chacarakairo.validatordoc.document.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_sessions")
public class DocumentSession {

    @Id
    private UUID id;

    @Column(name = "external_person_id", nullable = false, length = 120)
    private String externalPersonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 60)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentSessionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Version
    private long version;

    protected DocumentSession() {
    }

    public DocumentSession(String externalPersonId, DocumentType documentType, Instant now, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.externalPersonId = externalPersonId;
        this.documentType = documentType;
        this.status = DocumentSessionStatus.WAITING_UPLOAD;
        this.createdAt = now;
        this.updatedAt = now;
        this.expiresAt = expiresAt;
    }

    public void markUploaded(Instant now) {
        this.status = DocumentSessionStatus.UPLOADED;
        this.updatedAt = now;
    }

    public UUID getId() { return id; }
    public String getExternalPersonId() { return externalPersonId; }
    public DocumentType getDocumentType() { return documentType; }
    public DocumentSessionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getExpiresAt() { return expiresAt; }
}
