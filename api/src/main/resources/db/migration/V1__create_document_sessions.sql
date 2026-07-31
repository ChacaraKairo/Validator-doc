CREATE TABLE document_sessions (
    id UUID PRIMARY KEY,
    external_person_id VARCHAR(120) NOT NULL,
    document_type VARCHAR(60) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_document_sessions_external_person
    ON document_sessions (external_person_id);

CREATE INDEX idx_document_sessions_status
    ON document_sessions (status);

CREATE INDEX idx_document_sessions_expires_at
    ON document_sessions (expires_at);
