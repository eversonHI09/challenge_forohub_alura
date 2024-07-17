package com.alura.foroDeAlura.service;

import com.alura.foroDeAlura.controller.TopicoController;
import com.alura.foroDeAlura.dto.*;
import com.alura.foroDeAlura.model.Curso;
import com.alura.foroDeAlura.model.Mensaje;
import com.alura.foroDeAlura.model.Topico;
import com.alura.foroDeAlura.repository.TopicoRepository;
import com.alura.foroDeAlura.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private MensajeRepository mensajeRepository;


    public Topico registrarTopico(DatosRegistroTopico datosRegistroTopico) {
        // Verificar si ya existe un tópico con el mismo título y mensaje
        if (topicoRepository.existsByTituloAndMensajes_contenido(datosRegistroTopico.titulo(), datosRegistroTopico.mensaje())) {
            // Lanzar una excepción indicando que el tópico ya existe
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tópico ya existe.");
        }
        // Si no existe, proceder a guardar el nuevo tópico
        Topico nuevoTopico = new Topico(datosRegistroTopico);
        return topicoRepository.save(nuevoTopico);
    }


    public Page<DatosListadoTopico> listarTopicos(Pageable paginacion) {
        return topicoRepository.findAllActive(paginacion).map(DatosListadoTopico::new);
    }

    public PagedModel<EntityModel<DatosListadoTopico>> convertirAPagedModel(Page<DatosListadoTopico> topicosPage,
                                                                            PagedResourcesAssembler<DatosListadoTopico> pagedResourcesAssembler,
                                                                            Pageable paginacion) {
        return pagedResourcesAssembler.toModel(topicosPage,
                topico -> EntityModel.of(topico,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TopicoController.class)
                                .listadoTopicos(paginacion)).withSelfRel()));
    }


    public Page<DatosListadoTopico> buscarTopicosPorCurso(String nombreCurso, Pageable paginacion) {
        Curso curso;
        try {
            // Convertir el String a Curso enum
            curso = Curso.valueOf(nombreCurso.toUpperCase()); // Convertir el String a Curso enum
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso inválido");
        }
        return topicoRepository.findByCursoAndStatusNotClosed(curso, paginacion).map(DatosListadoTopico::new);
    }


    public Optional<Topico> buscarTopicoPorId(Long id) {

        return topicoRepository.findById(id);
    }

    @Transactional
    public void actualizarTopico(Long id, DatosActualizarTopico datosActualizarTopico) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Actualizar el tópico con los nuevos datos
        topico.actualizarTopico(datosActualizarTopico);

        // Guardar los cambios en el repositorio
        topicoRepository.save(topico);
    }

    public DatosListadoMensaje obtenerUltimoMensaje(Long id) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Obtener el último mensaje agregado al tópico (asumiendo que está en la última posición)
        List<Mensaje> mensajes = topico.getMensajes();
        if (!mensajes.isEmpty()) {
            Mensaje ultimoMensaje = mensajes.get(mensajes.size() - 1);
            return new DatosListadoMensaje(
                    ultimoMensaje.getId(),
                    ultimoMensaje.getContenido(),
                    ultimoMensaje.getFecha(),
                    ultimoMensaje.getAutor()
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay mensajes en el tópico");
        }
    }

    @Transactional
    public DatosListadoMensaje agregarMensaje(Long id, DatosNuevoMensaje datosNuevoMensaje) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Crear un nuevo mensaje con los datos proporcionados
        Mensaje nuevoMensaje = new Mensaje(datosNuevoMensaje);

        // Añadir el mensaje al tópico
        topico.agregarMensaje(nuevoMensaje);

        // Guardar el mensaje y el tópico en los repositorios
        topicoRepository.save(topico);
        return new DatosListadoMensaje(nuevoMensaje);
    }


    @Transactional
    public void cerrarTopico(Long id) {
        // Buscar el tópico por su ID
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Marcar el tópico como "CERRADO"
        topico.cerrarTopico();

        // Guardar el estado actualizado del tópico en el repositorio
        topicoRepository.save(topico);
    }


    @Transactional
    public void eliminarMensaje(Long idTopico, Long idMensaje) {
        // Buscar el tópico por su ID en el repositorio
        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Buscar el mensaje dentro de los mensajes del tópico
        Mensaje mensaje = topico.getMensajes().stream()
                .filter(m -> m.getId().equals(idMensaje))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensaje no encontrado"));

        // Remover el mensaje de la lista de mensajes del tópico
        topico.getMensajes().remove(mensaje);

        // Guardar los cambios en el repositorio del tópico
        topicoRepository.save(topico);

        // Eliminar el mensaje de la base de datos
        mensajeRepository.deleteById(idMensaje);
    }
}
