package br.com.chacarakairo.validatordoc.processing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processing_outbox")
public class ProcessingOutboxEvent {

    @Id
    private UUID id;
    @Column(name = "session_id", nullable = false)
    private UUID sessionId;
    @Column(nullable = false, length = 50)
    private String strategy;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "published_at")
    private Instant publishedAt;
    @Column(nullable = false)
    private int attempts;
    @Column(name = "last_error", length = 1000)
    private String lastError;

    protected ProcessingOutboxEvent() {
    }

    public ProcessingOutboxEvent(ProcessingJob job) {
        this.id = UUID.randomUUID();
        this.sessionId = job.sessionId();
        this.strategy = job.strategy().name();
        this.createdAt = job.requestedAt().toInstant();
    }

    public void markPublished(Instant now) {
        this.publishedAt = now;
        this.lastError = null;
    }

    public void markFailure(String error) {
        this.attempts += 1;
        this.lastError = error == null ? "unknown" : error.substring(0, Math.min(error.length(), 1000));
    }

    public UUID getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public String getStrategy() { return strategy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public int getAttempts() { return attempts; }
}
