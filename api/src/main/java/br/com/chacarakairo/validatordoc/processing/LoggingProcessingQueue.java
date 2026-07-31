package br.com.chacarakairo.validatordoc.processing;

import org.springframework.stereotype.Component;

@Component
public class LoggingProcessingQueue implements ProcessingQueue {

    private final ProcessingOutboxRepository repository;

    public LoggingProcessingQueue(ProcessingOutboxRepository repository) {
        this.repository = repository;
    }

    @Override
    public void publish(ProcessingJob job) {
        repository.save(new ProcessingOutboxEvent(job));
    }
}
