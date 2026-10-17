
package com.saudeconectada.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final PacienteRepository pacienteRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final AtendimentoRepository atendimentoRepository;

    public RelatorioController(
            PacienteRepository pacienteRepository,
            AgendamentoRepository agendamentoRepository,
            AtendimentoRepository atendimentoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.atendimentoRepository = atendimentoRepository;
    }

    @GetMapping("/resumo")
    public Map<String, Long> resumo() {
        return Map.of(
                "totalPacientes", pacienteRepository.count(),
                "totalAgendamentos", agendamentoRepository.count(),
                "totalAtendimentos", atendimentoRepository.count()
        );
    }
}
