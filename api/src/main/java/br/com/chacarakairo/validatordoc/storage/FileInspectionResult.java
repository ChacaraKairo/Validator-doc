package br.com.chacarakairo.validatordoc.storage;

public record FileInspectionResult(
    String detectedMediaType,
    String sha256,
    long sizeBytes
) {
}
