package ar.edu.huergo.lcarera.restaurante.controller.plato;

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

import ar.edu.huergo.lcarera.restaurante.dto.plato.CrearActualizarPlatoDTO;
import ar.edu.huergo.lcarera.restaurante.dto.plato.MostrarPlatoDTO;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;
import ar.edu.huergo.lcarera.restaurante.mapper.plato.PlatoMapper;
import ar.edu.huergo.lcarera.restaurante.service.plato.PlatoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/platos")
public class PlatoController {
    @Autowired
    private PlatoService platoService;

    @Autowired
    private PlatoMapper platoMapper;

    @GetMapping
    public ResponseEntity<List<MostrarPlatoDTO>> obtenerTodosLosPlatos() {
        List<Plato> platos = platoService.obtenerTodosLosPlatos();
        List<MostrarPlatoDTO> platosDTO = platoMapper.toDTOList(platos);
        return ResponseEntity.ok(platosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MostrarPlatoDTO> obtenerPlatoPorId(@PathVariable Long id) {
        Plato plato = platoService.obtenerPlatoPorId(id);
        MostrarPlatoDTO platoDTO = platoMapper.toDTO(plato);
        return ResponseEntity.ok(platoDTO);
    }

    @PostMapping
    public ResponseEntity<MostrarPlatoDTO> crearPlato(@Valid @RequestBody CrearActualizarPlatoDTO platoDTO) {
        Plato plato = platoMapper.toEntity(platoDTO);
        Plato platoCreado = platoService.crearPlato(plato, platoDTO.getIngredientesIds());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(platoCreado.getId()).toUri();
        return ResponseEntity.created(location).body(platoMapper.toDTO(platoCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MostrarPlatoDTO> actualizarPlato(@PathVariable Long id,
            @Valid @RequestBody CrearActualizarPlatoDTO platoDTO) {
        Plato plato = platoMapper.toEntity(platoDTO);
        Plato platoActualizado =
                platoService.actualizarPlato(id, plato, platoDTO.getIngredientesIds());
        return ResponseEntity.ok(platoMapper.toDTO(platoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlato(@PathVariable Long id) {
        platoService.eliminarPlato(id);
        return ResponseEntity.noContent().build();
    }
}
