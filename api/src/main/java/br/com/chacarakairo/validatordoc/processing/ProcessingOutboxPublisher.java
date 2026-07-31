package br.com.chacarakairo.validatordoc.processing;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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
        this.rabbitTemplate.setMandatory(true);
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
                CorrelationData correlation = new CorrelationData(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend(
                    RabbitProcessingConfiguration.EXCHANGE,
                    RabbitProcessingConfiguration.ROUTING_KEY,
                    job,
                    correlation
                );
                CorrelationData.Confirm confirm = correlation.getFuture().get(10, TimeUnit.SECONDS);
                if (!confirm.isAck()) {
                    throw new IllegalStateException("RabbitMQ rejeitou a publicação: " + confirm.getReason());
                }
                event.markPublished(Instant.now());
            } catch (Exception exception) {
                event.markFailure(exception.getMessage());
            }
        }
    }
}
