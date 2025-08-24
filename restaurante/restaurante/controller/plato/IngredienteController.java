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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ar.edu.huergo.lcarera.restaurante.dto.plato.IngredienteDTO;
import ar.edu.huergo.lcarera.restaurante.mapper.plato.IngredienteMapper;
import ar.edu.huergo.lcarera.restaurante.service.plato.IngredienteService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    @Autowired
    private IngredienteService ingredienteService;

    @Autowired
    private IngredienteMapper ingredienteMapper;

    @GetMapping
    public ResponseEntity<List<IngredienteDTO>> obtenerTodosLosIngredientes() {
        return ResponseEntity.ok(ingredienteMapper.toDTOList(ingredienteService.obtenerTodosLosIngredientes()));
    }

    @PostMapping
    public ResponseEntity<IngredienteDTO> crearIngrediente(@RequestBody @Valid IngredienteDTO ingredienteDTO) {
        IngredienteDTO ingredienteCreado = ingredienteMapper.toDTO(ingredienteService.crearIngrediente(ingredienteMapper.toEntity(ingredienteDTO)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(ingredienteCreado.id())
            .toUri();
        return ResponseEntity.created(location).body(ingredienteCreado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteDTO> obtenerIngredientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingredienteMapper.toDTO(ingredienteService.obtenerIngredientePorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredienteDTO> actualizarIngrediente(@PathVariable Long id, @RequestBody @Valid IngredienteDTO ingredienteDTO) {
        return ResponseEntity.ok(ingredienteMapper.toDTO(ingredienteService.actualizarIngrediente(id, ingredienteMapper.toEntity(ingredienteDTO))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIngrediente(@PathVariable Long id) {
        ingredienteService.eliminarIngrediente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nombre")
    public ResponseEntity<List<IngredienteDTO>> obtenerIngredientesPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(ingredienteMapper.toDTOList(ingredienteService.obtenerIngredientesPorNombre(nombre)));
    }
}
