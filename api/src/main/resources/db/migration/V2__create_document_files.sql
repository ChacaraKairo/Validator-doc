CREATE TABLE document_files (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES document_sessions(id) ON DELETE CASCADE,
    slot VARCHAR(32) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    media_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    sha256 VARCHAR(64) NOT NULL,
    storage_bucket VARCHAR(100) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_document_files_session_slot UNIQUE (session_id, slot),
    CONSTRAINT uk_document_files_session_sha256 UNIQUE (session_id, sha256)
);

CREATE INDEX idx_document_files_session_id ON document_files(session_id);
CREATE INDEX idx_document_files_sha256 ON document_files(sha256);
