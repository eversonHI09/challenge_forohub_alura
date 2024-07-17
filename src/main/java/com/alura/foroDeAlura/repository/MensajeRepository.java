package com.alura.foroDeAlura.repository;

import com.alura.foroDeAlura.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    void deleteById(Long id);
}
