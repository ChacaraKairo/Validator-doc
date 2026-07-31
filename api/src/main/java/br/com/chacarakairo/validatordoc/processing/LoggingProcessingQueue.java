package br.com.chacarakairo.validatordoc.processing;

import br.com.chacarakairo.validatordoc.session.DocumentSession;
import br.com.chacarakairo.validatordoc.session.DocumentSessionRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoggingProcessingQueue implements ProcessingQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingProcessingQueue.class);
    private final DocumentSessionRepository sessionRepository;

    public LoggingProcessingQueue(DocumentSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    @Transactional
    public void publish(ProcessingJob job) {
        LOGGER.info("processing_job_prepared sessionId={} strategy={} requestedAt={}",
            job.sessionId(), job.strategy(), job.requestedAt());

        if (!"STORAGE_ONLY".equals(job.strategy())) {
            return;
        }

        DocumentSession session = sessionRepository.findById(job.sessionId())
            .orElseThrow(() -> new IllegalArgumentException("Sessão documental não encontrada para processamento."));

        session.markValid(Instant.now());
        sessionRepository.save(session);

        LOGGER.info("storage_only_completed sessionId={}", job.sessionId());
    }
}
