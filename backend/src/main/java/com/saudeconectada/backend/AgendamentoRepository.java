
package com.saudeconectada.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, Long> {
}
