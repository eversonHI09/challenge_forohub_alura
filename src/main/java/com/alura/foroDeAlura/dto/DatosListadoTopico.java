package com.alura.foroDeAlura.dto;

import com.alura.foroDeAlura.model.Topico;
import com.alura.foroDeAlura.model.Curso;

import java.util.List;
import java.util.stream.Collectors;

public record DatosListadoTopico(
        Long id,
        String titulo,
        List<DatosListadoMensaje> mensajes,
        String status,
        Curso curso) {

    public DatosListadoTopico(Topico topico){
        this(topico.getId(),
                topico.getTitulo(),
                topico.getMensajes().stream()
                        .map(DatosListadoMensaje::new)
                        .collect(Collectors.toList()),
                topico.getStatus(),
                topico.getCurso());
    }
}
