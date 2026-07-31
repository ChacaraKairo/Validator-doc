package br.com.chacarakairo.validatordoc.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingProcessingQueue implements ProcessingQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingProcessingQueue.class);

    @Override
    public void publish(ProcessingJob job) {
        LOGGER.info("processing_job_prepared sessionId={} strategy={} requestedAt={}",
            job.sessionId(), job.strategy(), job.requestedAt());
    }
}
