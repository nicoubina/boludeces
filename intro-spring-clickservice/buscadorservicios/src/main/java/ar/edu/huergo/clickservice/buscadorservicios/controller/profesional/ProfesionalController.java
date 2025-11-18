package ar.edu.huergo.clickservice.buscadorservicios.controller.profesional;

import java.net.URI;
import java.util.List;
import java.util.Set;

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

import ar.edu.huergo.clickservice.buscadorservicios.dto.profesional.ProfesionalDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.profesional.ProfesionalMapper;
import ar.edu.huergo.clickservice.buscadorservicios.service.profesional.ProfesionalService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {

    @Autowired
    private ProfesionalService profesionalService;

    @Autowired
    private ProfesionalMapper profesionalMapper;

    @GetMapping
    public ResponseEntity<List<ProfesionalDTO>> obtenerTodosLosProfesionales() {
        List<Profesional> profesionales = profesionalService.obtenerTodosLosProfesionales();
        List<ProfesionalDTO> profesionalesDTO = profesionalMapper.toDTOList(profesionales);
        return ResponseEntity.ok(profesionalesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesionalDTO> obtenerProfesionalPorId(@PathVariable Long id) {
        Profesional profesional = profesionalService.obtenerProfesionalPorId(id);
        ProfesionalDTO profesionalDTO = profesionalMapper.toDTO(profesional);
        return ResponseEntity.ok(profesionalDTO);
    }

    @PostMapping
    public ResponseEntity<ProfesionalDTO> crearProfesional(@Valid @RequestBody ProfesionalDTO profesionalDTO) {
        Profesional profesional = profesionalMapper.toEntity(profesionalDTO);
        Profesional profesionalCreado = profesionalService.crearProfesional(profesional);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(profesionalCreado.getId()).toUri();
        return ResponseEntity.created(location).body(profesionalMapper.toDTO(profesionalCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesionalDTO> actualizarProfesional(@PathVariable Long id,
            @Valid @RequestBody ProfesionalDTO profesionalDTO) {
        Profesional profesional = profesionalMapper.toEntity(profesionalDTO);
        Profesional profesionalActualizado = profesionalService.actualizarProfesional(id, profesional);
        return ResponseEntity.ok(profesionalMapper.toDTO(profesionalActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProfesional(@PathVariable Long id) {
        profesionalService.eliminarProfesional(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ProfesionalDTO>> obtenerProfesionalesDisponibles() {
        List<Profesional> profesionales = profesionalService.obtenerProfesionalesDisponibles();
        List<ProfesionalDTO> profesionalesDTO = profesionalMapper.toDTOList(profesionales);
        return ResponseEntity.ok(profesionalesDTO);
    }

    @GetMapping("/por-servicio/{servicioId}")
    public ResponseEntity<List<ProfesionalDTO>> obtenerProfesionalesPorServicio(@PathVariable Long servicioId) {
        List<Profesional> profesionales = profesionalService.obtenerProfesionalesPorServicio(servicioId);
        List<ProfesionalDTO> profesionalesDTO = profesionalMapper.toDTOList(profesionales);
        return ResponseEntity.ok(profesionalesDTO);
    }

    @PutMapping("/{id}/servicios")
    public ResponseEntity<ProfesionalDTO> asignarServicios(@PathVariable Long id,
            @RequestBody Set<Long> serviciosIds) {
        Profesional profesionalActualizado = profesionalService.asignarServicios(id, serviciosIds);
        return ResponseEntity.ok(profesionalMapper.toDTO(profesionalActualizado));
    }
}