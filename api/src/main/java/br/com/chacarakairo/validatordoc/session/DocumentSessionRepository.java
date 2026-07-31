package br.com.chacarakairo.validatordoc.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentSessionRepository extends JpaRepository<DocumentSession, UUID> {
}
