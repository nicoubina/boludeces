
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

import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.ServicioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.servicio.ServicioMapper;
import ar.edu.huergo.clickservice.buscadorservicios.service.servicio.ServicioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    @Autowired
    private ServicioService servicioService;

    @Autowired
    private ServicioMapper servicioMapper;

    @GetMapping
    public ResponseEntity<List<ServicioDTO>> obtenerTodosLosServicios() {
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        List<ServicioDTO> serviciosDTO = servicioMapper.toDTOList(servicios);
        return ResponseEntity.ok(serviciosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioDTO> obtenerServicioPorId(@PathVariable Long id) {
        Servicio servicio = servicioService.obtenerServicioPorId(id);
        ServicioDTO servicioDTO = servicioMapper.toDTO(servicio);
        return ResponseEntity.ok(servicioDTO);
    }

    @PostMapping
    public ResponseEntity<ServicioDTO> crearServicio(@Valid @RequestBody ServicioDTO servicioDTO) {
        Servicio servicio = servicioMapper.toEntity(servicioDTO);
        Servicio servicioCreado = servicioService.crearServicio(servicio);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(servicioCreado.getId()).toUri();
        return ResponseEntity.created(location).body(servicioMapper.toDTO(servicioCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioDTO> actualizarServicio(@PathVariable Long id,
            @Valid @RequestBody ServicioDTO servicioDTO) {
        Servicio servicio = servicioMapper.toEntity(servicioDTO);
        Servicio servicioActualizado = servicioService.actualizarServicio(id, servicio);
        return ResponseEntity.ok(servicioMapper.toDTO(servicioActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id) {
        servicioService.eliminarServicio(id);
        return ResponseEntity.noContent().build();
    }
}