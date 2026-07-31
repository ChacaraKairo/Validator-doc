package br.com.chacarakairo.validatordoc.processing;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProcessingJob(UUID sessionId, String strategy, OffsetDateTime requestedAt) {
}
