package br.com.chacarakairo.validatordoc.processing;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingOutboxRepository extends JpaRepository<ProcessingOutboxEvent, UUID> {
    List<ProcessingOutboxEvent> findByPublishedAtIsNullOrderByCreatedAtAsc(Pageable pageable);
}
