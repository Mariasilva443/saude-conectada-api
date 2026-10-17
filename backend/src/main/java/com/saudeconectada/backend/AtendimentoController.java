
package com.saudeconectada.backend;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
public class AtendimentoController {

    private final AtendimentoRepository atendimentoRepository;
    private final PacienteRepository pacienteRepository;

    public AtendimentoController(
            AtendimentoRepository atendimentoRepository,
            PacienteRepository pacienteRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public static class AtendimentoDTO {

        @NotNull(message = "O ID do paciente é obrigatório")
        public Long pacienteId;

        @NotBlank(message = "A descrição é obrigatória")
        public String descricao;

        public String profissional;
    }

    @GetMapping
    public List<Atendimento> listar() {
        return atendimentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atendimento> buscarPorId(
            @PathVariable Long id) {
        return atendimentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> listarPorPaciente(
            @PathVariable Long pacienteId) {

        if (!pacienteRepository.existsById(pacienteId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Paciente não encontrado.");
        }

        return ResponseEntity.ok(
                atendimentoRepository
                        .findByPacienteIdOrderByDataHoraDesc(pacienteId)
        );
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody AtendimentoDTO dados) {

        var resultadoPaciente =
                pacienteRepository.findById(dados.pacienteId);

        if (resultadoPaciente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Paciente não encontrado.");
        }

        Atendimento atendimento = new Atendimento();
        atendimento.setPaciente(resultadoPaciente.get());
        atendimento.setDataHora(LocalDateTime.now());
        atendimento.setDescricao(dados.descricao);
        atendimento.setProfissional(dados.profissional);

        Atendimento salvo = atendimentoRepository.save(atendimento);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}
