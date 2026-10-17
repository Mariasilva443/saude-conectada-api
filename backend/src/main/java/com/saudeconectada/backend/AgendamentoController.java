
package com.saudeconectada.backend;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoRepository agendamentoRepository;
    private final PacienteRepository pacienteRepository;

    public AgendamentoController(
            AgendamentoRepository agendamentoRepository,
            PacienteRepository pacienteRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public List<Agendamento> listar() {
        return agendamentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(
            @PathVariable Long id) {
        return agendamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static class NovoAgendamento {
        @jakarta.validation.constraints.NotNull
        public Long pacienteId;

        @jakarta.validation.constraints.NotNull
        public java.time.LocalDate data;

        @jakarta.validation.constraints.NotNull
        public java.time.LocalTime horario;

        @jakarta.validation.constraints.NotBlank
        public String tipoConsulta;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody NovoAgendamento dados) {

        var resultadoPaciente =
                pacienteRepository.findById(dados.pacienteId);

        if (resultadoPaciente.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Paciente não encontrado.");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(resultadoPaciente.get());
        agendamento.setData(dados.data);
        agendamento.setHorario(dados.horario);
        agendamento.setTipoConsulta(dados.tipoConsulta);
        agendamento.setStatus("AGENDADO");

        Agendamento salvo = agendamentoRepository.save(agendamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Agendamento dados) {

        var resultado = agendamentoRepository.findById(id);

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Agendamento agendamento = resultado.get();
        agendamento.setData(dados.getData());
        agendamento.setHorario(dados.getHorario());
        agendamento.setTipoConsulta(dados.getTipoConsulta());

        return ResponseEntity.ok(
                agendamentoRepository.save(agendamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        var resultado = agendamentoRepository.findById(id);

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Agendamento agendamento = resultado.get();
        agendamento.setStatus("CANCELADO");
        agendamentoRepository.save(agendamento);

        return ResponseEntity.noContent().build();
    }
}
