package ar.edu.huergo.clickservice.buscadorservicios.controller.servicio;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.SolicitudServicioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio.EstadoSolicitud;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.servicio.SolicitudServicioMapper;
import ar.edu.huergo.clickservice.buscadorservicios.service.servicio.SolicitudServicioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudServicioController {

    @Autowired
    private SolicitudServicioService solicitudServicioService;

    @Autowired
    private SolicitudServicioMapper solicitudServicioMapper;

    @GetMapping
    public ResponseEntity<List<SolicitudServicioDTO>> obtenerTodasLasSolicitudes() {
        List<SolicitudServicio> solicitudes = solicitudServicioService.obtenerTodasLasSolicitudes();
        List<SolicitudServicioDTO> solicitudesDTO = solicitudServicioMapper.toDTOList(solicitudes);
        return ResponseEntity.ok(solicitudesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudServicioDTO> obtenerSolicitudPorId(@PathVariable Long id) {
        SolicitudServicio solicitud = solicitudServicioService.obtenerSolicitudPorId(id);
        SolicitudServicioDTO solicitudDTO = solicitudServicioMapper.toDTO(solicitud);
        return ResponseEntity.ok(solicitudDTO);
    }

    @PostMapping
    public ResponseEntity<SolicitudServicioDTO> crearSolicitud(@Valid @RequestBody SolicitudServicioDTO solicitudDTO) {
        SolicitudServicio solicitud = solicitudServicioMapper.toEntity(solicitudDTO);
        SolicitudServicio solicitudCreada = solicitudServicioService.crearSolicitud(solicitud);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(solicitudCreada.getId()).toUri();
        return ResponseEntity.created(location).body(solicitudServicioMapper.toDTO(solicitudCreada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudServicioDTO> actualizarSolicitud(@PathVariable Long id,
            @Valid @RequestBody SolicitudServicioDTO solicitudDTO) {
        SolicitudServicio solicitud = solicitudServicioMapper.toEntity(solicitudDTO);
        SolicitudServicio solicitudActualizada = solicitudServicioService.actualizarSolicitud(id, solicitud);
        return ResponseEntity.ok(solicitudServicioMapper.toDTO(solicitudActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        solicitudServicioService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<SolicitudServicioDTO>> obtenerSolicitudesPorCliente(@PathVariable Long clienteId) {
        List<SolicitudServicio> solicitudes = solicitudServicioService.obtenerSolicitudesPorCliente(clienteId);
        List<SolicitudServicioDTO> solicitudesDTO = solicitudServicioMapper.toDTOList(solicitudes);
        return ResponseEntity.ok(solicitudesDTO);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudServicioDTO>> obtenerSolicitudesPorEstado(@PathVariable EstadoSolicitud estado) {
        List<SolicitudServicio> solicitudes = solicitudServicioService.obtenerSolicitudesPorEstado(estado);
        List<SolicitudServicioDTO> solicitudesDTO = solicitudServicioMapper.toDTOList(solicitudes);
        return ResponseEntity.ok(solicitudesDTO);
    }

    @GetMapping("/servicio/{servicioId}/estado/{estado}")
    public ResponseEntity<List<SolicitudServicioDTO>> obtenerSolicitudesPorServicioYEstado(
            @PathVariable Long servicioId, @PathVariable EstadoSolicitud estado) {
        List<SolicitudServicio> solicitudes = solicitudServicioService.obtenerSolicitudesPorServicioYEstado(servicioId, estado);
        List<SolicitudServicioDTO> solicitudesDTO = solicitudServicioMapper.toDTOList(solicitudes);
        return ResponseEntity.ok(solicitudesDTO);
    }

    @PutMapping("/{id}/estado/{estado}")
    public ResponseEntity<SolicitudServicioDTO> cambiarEstadoSolicitud(@PathVariable Long id, 
            @PathVariable EstadoSolicitud estado) {
        SolicitudServicio solicitudActualizada = solicitudServicioService.cambiarEstadoSolicitud(id, estado);
        return ResponseEntity.ok(solicitudServicioMapper.toDTO(solicitudActualizada));
    }
}