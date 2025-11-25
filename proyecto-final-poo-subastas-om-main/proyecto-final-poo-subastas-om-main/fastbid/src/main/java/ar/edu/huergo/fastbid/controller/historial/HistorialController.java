package ar.edu.huergo.fastbid.controller.historial;

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

import ar.edu.huergo.fastbid.dto.HistorialDTO;
import ar.edu.huergo.fastbid.entity.historial.Historial;
import ar.edu.huergo.fastbid.mapper.HistorialMapper;
import ar.edu.huergo.fastbid.service.HistorialService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/historiales")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @Autowired
    private HistorialMapper historialMapper;

    @GetMapping
    public ResponseEntity<List<HistorialDTO>> obtenerHistoriales() {
        List<Historial> historiales = historialService.obtenerHistoriales();
        return ResponseEntity.ok(historialMapper.toDtoList(historiales));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialDTO> obtenerHistorialPorId(@PathVariable Long id) {
        Historial historial = historialService.obtenerHistorialPorId(id);
        return ResponseEntity.ok(historialMapper.toDto(historial));
    }

    @PostMapping
    public ResponseEntity<HistorialDTO> crearHistorial(@Valid @RequestBody HistorialDTO historialDto) {
        Historial historial = historialMapper.toEntity(historialDto);
        Historial historialCreado = historialService.crearHistorial(historial);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(historialCreado.getIdHistorial()).toUri();
        return ResponseEntity.created(location).body(historialMapper.toDto(historialCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialDTO> actualizarHistorial(@PathVariable Long id,
            @Valid @RequestBody HistorialDTO historialDto) {
        Historial historial = historialMapper.toEntity(historialDto);
        Historial historialActualizado = historialService.actualizarHistorial(id, historial);
        return ResponseEntity.ok(historialMapper.toDto(historialActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long id) {
        historialService.eliminarHistorial(id);
        return ResponseEntity.noContent().build();
    }
}
