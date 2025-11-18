package ar.edu.huergo.clickservice.buscadorservicios.controller.profesional;

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

import ar.edu.huergo.clickservice.buscadorservicios.dto.profesional.ReseñaDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Reseña;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.profesional.ReseñaMapper;
import ar.edu.huergo.clickservice.buscadorservicios.service.profesional.ReseñaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resenas")
public class ReseñaController {
    
    @Autowired
    private ReseñaService reseñaService;

    @Autowired
    private ReseñaMapper reseñaMapper;

    @GetMapping
    public ResponseEntity<List<ReseñaDTO>> obtenerTodasLasReseñas() {
        List<Reseña> reseñas = reseñaService.obtenerTodasLasReseñas();
        List<ReseñaDTO> reseñasDTO = reseñaMapper.toDTOList(reseñas);
        return ResponseEntity.ok(reseñasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReseñaDTO> obtenerReseñaPorId(@PathVariable Long id) {
        Reseña reseña = reseñaService.obtenerReseñaPorId(id);
        ReseñaDTO reseñaDTO = reseñaMapper.toDTO(reseña);
        return ResponseEntity.ok(reseñaDTO);
    }

    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<ReseñaDTO>> obtenerReseñasPorProfesional(@PathVariable Long profesionalId) {
        List<Reseña> reseñas = reseñaService.obtenerReseñasPorProfesional(profesionalId);
        List<ReseñaDTO> reseñasDTO = reseñaMapper.toDTOList(reseñas);
        return ResponseEntity.ok(reseñasDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReseñaDTO>> obtenerReseñasPorUsuario(@PathVariable Long usuarioId) {
        List<Reseña> reseñas = reseñaService.obtenerReseñasPorUsuario(usuarioId);
        List<ReseñaDTO> reseñasDTO = reseñaMapper.toDTOList(reseñas);
        return ResponseEntity.ok(reseñasDTO);
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<ReseñaDTO> obtenerReseñaPorOrden(@PathVariable Long ordenId) {
        Reseña reseña = reseñaService.obtenerReseñaPorOrden(ordenId);
        ReseñaDTO reseñaDTO = reseñaMapper.toDTO(reseña);
        return ResponseEntity.ok(reseñaDTO);
    }

    @PostMapping
    public ResponseEntity<ReseñaDTO> crearReseña(@Valid @RequestBody ReseñaDTO reseñaDTO) {
        Reseña reseña = reseñaMapper.toEntity(reseñaDTO);
        Reseña reseñaCreada = reseñaService.crearReseña(
            reseña, 
            reseñaDTO.getUsuarioId(), 
            reseñaDTO.getProfesionalId()
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reseñaCreada.getId())
                .toUri();
        return ResponseEntity.created(location).body(reseñaMapper.toDTO(reseñaCreada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReseñaDTO> actualizarReseña(
            @PathVariable Long id,
            @Valid @RequestBody ReseñaDTO reseñaDTO) {
        Reseña reseña = reseñaMapper.toEntity(reseñaDTO);
        Reseña reseñaActualizada = reseñaService.actualizarReseña(id, reseña);
        return ResponseEntity.ok(reseñaMapper.toDTO(reseñaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReseña(@PathVariable Long id) {
        reseñaService.eliminarReseña(id);
        return ResponseEntity.noContent().build();
    }
}