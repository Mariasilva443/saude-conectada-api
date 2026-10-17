
package com.saudeconectada.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository
        extends JpaRepository<Paciente, Long> {
    
    boolean existsByCpf(String cpf);
}
