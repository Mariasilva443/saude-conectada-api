package com.saudeconectada.backend;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/fila-atendimento")
public class FilaAtendimentoController {

    private final FilaAtendimentoRepository filaRepository;
    private final PacienteRepository pacienteRepository;

    private static final Set<String> STATUS_VALIDOS = Set.of(
            "AGUARDANDO",
            "EM_ATENDIMENTO",
            "ATENDIDO",
            "CANCELADO"
    );

    public FilaAtendimentoController(
            FilaAtendimentoRepository filaRepository,
            PacienteRepository pacienteRepository) {

        this.filaRepository = filaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public static class FilaDTO {

        @NotNull(message = "O ID do paciente é obrigatório")
        public Long pacienteId;

        public String observacao;
    }

    public static class StatusDTO {

        @NotBlank(message = "O status é obrigatório")
        public String status;
    }

    // Lista todos os registros da fila.
    // Também permite filtrar pelo status.
    @GetMapping
    public List<FilaAtendimento> listar(
            @RequestParam(required = false) String status) {

        if (status != null && !status.isBlank()) {
            return filaRepository.findByStatusOrderByDataHoraEntradaAsc(
                    status.trim().toUpperCase()
            );
        }

        return filaRepository.findAll();
    }

    // Busca um registro específico da fila.
    @GetMapping("/{id}")
    public ResponseEntity<FilaAtendimento> buscarPorId(
            @PathVariable Long id) {

        return filaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Adiciona um paciente à fila de atendimento.
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody FilaDTO dados) {

        var resultadoPaciente =
                pacienteRepository.findById(dados.pacienteId);

        if (resultadoPaciente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Paciente não encontrado.");
        }

        FilaAtendimento fila = new FilaAtendimento();

        fila.setPaciente(resultadoPaciente.get());
        fila.setDataHoraEntrada(LocalDateTime.now());
        fila.setStatus("AGUARDANDO");
        fila.setObservacao(dados.observacao);

        FilaAtendimento salvo = filaRepository.save(fila);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Atualiza o status de um paciente na fila.
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusDTO dados) {

        var resultado = filaRepository.findById(id);

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String novoStatus = dados.status.trim().toUpperCase();

        if (!STATUS_VALIDOS.contains(novoStatus)) {
            return ResponseEntity.badRequest().body(
                    "Status inválido. Use: AGUARDANDO, "
                    + "EM_ATENDIMENTO, ATENDIDO ou CANCELADO."
            );
        }

        FilaAtendimento fila = resultado.get();
        fila.setStatus(novoStatus);

        FilaAtendimento atualizado = filaRepository.save(fila);

        return ResponseEntity.ok(atualizado);
    }
}