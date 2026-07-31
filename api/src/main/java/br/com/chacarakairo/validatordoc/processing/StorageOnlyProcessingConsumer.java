package br.com.chacarakairo.validatordoc.processing;

import br.com.chacarakairo.validatordoc.session.DocumentSession;
import br.com.chacarakairo.validatordoc.session.DocumentSessionRepository;
import java.time.Instant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StorageOnlyProcessingConsumer {

    private final DocumentSessionRepository sessionRepository;

    public StorageOnlyProcessingConsumer(DocumentSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @RabbitListener(queues = RabbitProcessingConfiguration.QUEUE)
    @Transactional
    public void consume(ProcessingJob job) {
        if (job.strategy() != ProcessingStrategy.STORAGE_ONLY) {
            return;
        }
        DocumentSession session = sessionRepository.findById(job.sessionId())
            .orElseThrow(() -> new IllegalArgumentException("Sessão documental não encontrada para processamento."));
        session.markValid(Instant.now());
        sessionRepository.save(session);
    }
}
