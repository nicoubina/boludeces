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

import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.ComisionDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Comision;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.servicio.ComisionMapper;
import ar.edu.huergo.clickservice.buscadorservicios.service.servicio.ComisionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comisiones")
public class ComisionController {

    @Autowired
    private ComisionService comisionService;

    @Autowired
    private ComisionMapper comisionMapper;

    @GetMapping
    public ResponseEntity<List<ComisionDTO>> obtenerTodasLasComisiones() {
        List<Comision> comisiones = comisionService.obtenerTodasLasComisiones();
        List<ComisionDTO> comisionesDTO = comisionMapper.toDTOList(comisiones);
        return ResponseEntity.ok(comisionesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComisionDTO> obtenerComisionPorId(@PathVariable Long id) {
        Comision comision = comisionService.obtenerComisionPorId(id);
        return ResponseEntity.ok(comisionMapper.toDTO(comision));
    }

    @PostMapping
    public ResponseEntity<ComisionDTO> crearComision(@Valid @RequestBody ComisionDTO comisionDTO) {
        Comision comision = comisionMapper.toEntity(comisionDTO);
        Comision comisionCreada = comisionService.crearComision(comision);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(comisionCreada.getId()).toUri();
        return ResponseEntity.created(location).body(comisionMapper.toDTO(comisionCreada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComisionDTO> actualizarComision(@PathVariable Long id,
            @Valid @RequestBody ComisionDTO comisionDTO) {
        Comision comision = comisionMapper.toEntity(comisionDTO);
        Comision comisionActualizada = comisionService.actualizarComision(id, comision);
        return ResponseEntity.ok(comisionMapper.toDTO(comisionActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComision(@PathVariable Long id) {
        comisionService.eliminarComision(id);
        return ResponseEntity.noContent().build();
    }
}
