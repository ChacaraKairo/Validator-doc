package br.com.chacarakairo.validatordoc.processing;

public interface ProcessingQueue {
    void publish(ProcessingJob job);
}
