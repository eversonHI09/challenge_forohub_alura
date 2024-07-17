package com.alura.foroDeAlura.repository;

import com.alura.foroDeAlura.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository <Usuario, Long>{
    UserDetails findByEmail(String username);
}
