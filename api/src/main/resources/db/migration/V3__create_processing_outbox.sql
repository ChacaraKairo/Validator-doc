CREATE TABLE processing_outbox (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES document_sessions(id),
    strategy VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0,
    last_error VARCHAR(1000)
);

CREATE INDEX idx_processing_outbox_pending
    ON processing_outbox (created_at)
    WHERE published_at IS NULL;
