package br.com.chacarakairo.validatordoc.processing;

import java.time.Instant;
import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessingOutboxPublisher {

    private final ProcessingOutboxRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public ProcessingOutboxPublisher(ProcessingOutboxRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publish-delay-ms:1000}")
    @Transactional
    public void publishPending() {
        List<ProcessingOutboxEvent> events = repository
            .findByPublishedAtIsNullOrderByCreatedAtAsc(PageRequest.of(0, 50));

        for (ProcessingOutboxEvent event : events) {
            try {
                ProcessingJob job = new ProcessingJob(
                    event.getSessionId(),
                    ProcessingStrategy.valueOf(event.getStrategy()),
                    event.getCreatedAt().atOffset(java.time.ZoneOffset.UTC)
                );
                rabbitTemplate.convertAndSend(
                    RabbitProcessingConfiguration.EXCHANGE,
                    RabbitProcessingConfiguration.ROUTING_KEY,
                    job
                );
                event.markPublished(Instant.now());
            } catch (Exception exception) {
                event.markFailure(exception.getMessage());
            }
        }
    }
}
