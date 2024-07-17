package com.alura.foroDeAlura.service;

import com.alura.foroDeAlura.dto.DatosNuevoUsuario;
import com.alura.foroDeAlura.model.Usuario;
import com.alura.foroDeAlura.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrarNuevoUsuario(DatosNuevoUsuario datosNuevoUsuario) {
        // Crear una instancia de Usuario con los datos del DTO
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(datosNuevoUsuario.nombre());
        nuevoUsuario.setEmail(datosNuevoUsuario.email());
        // Encriptar la contraseña antes de guardarla en la base de datos
        nuevoUsuario.setClave(passwordEncoder.encode(datosNuevoUsuario.clave()));

        // Guardar el nuevo usuario en la base de datos
        return usuarioRepository.save(nuevoUsuario);
    }
}
