package com.alura.foroDeAlura.controller;

import com.alura.foroDeAlura.dto.DatosNuevoUsuario;
import com.alura.foroDeAlura.model.Usuario;
import com.alura.foroDeAlura.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios, incluyendo registro y consulta.")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema.")
    public ResponseEntity<Usuario> registrarUsuario(
            @Parameter(description = "Datos del nuevo usuario a registrar", required = true)
            @Valid @RequestBody DatosNuevoUsuario datosNuevoUsuario) {
        Usuario usuarioCreado = usuarioService.registrarNuevoUsuario(datosNuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }
}
