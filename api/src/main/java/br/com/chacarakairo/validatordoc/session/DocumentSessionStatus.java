package br.com.chacarakairo.validatordoc.session;

public enum DocumentSessionStatus {
    WAITING_UPLOAD,
    UPLOADED,
    QUEUED,
    PROCESSING,
    VALID,
    INVALID,
    REVIEW_REQUIRED,
    FAILED,
    EXPIRED
}
