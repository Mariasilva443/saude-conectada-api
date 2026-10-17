package com.saudeconectada.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FilaAtendimentoRepository
        extends JpaRepository<FilaAtendimento, Long> {

    List<FilaAtendimento> findByStatusOrderByDataHoraEntradaAsc(
            String status
    );
}