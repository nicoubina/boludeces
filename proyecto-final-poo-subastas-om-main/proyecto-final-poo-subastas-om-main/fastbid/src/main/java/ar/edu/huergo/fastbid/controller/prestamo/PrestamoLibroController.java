package ar.edu.huergo.fastbid.controller.prestamo;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ar.edu.huergo.fastbid.dto.PrestamoLibroRequestDTO;
import ar.edu.huergo.fastbid.dto.PrestamoLibroResponseDTO;
import ar.edu.huergo.fastbid.dto.ResumenUsuarioDTO;
import ar.edu.huergo.fastbid.entity.prestamo.PrestamoLibro;
import ar.edu.huergo.fastbid.mapper.PrestamoLibroMapper;
import ar.edu.huergo.fastbid.service.PrestamoLibroService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoLibroController {

    @Autowired
    private PrestamoLibroService prestamoLibroService;

    @Autowired
    private PrestamoLibroMapper prestamoLibroMapper;

    @GetMapping
    public ResponseEntity<List<PrestamoLibroResponseDTO>> obtenerPrestamos() {
        List<PrestamoLibro> prestamos = prestamoLibroService.obtenerTodos();
        return ResponseEntity.ok(prestamoLibroMapper.toDtoList(prestamos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoLibroResponseDTO> obtenerPrestamoPorId(@PathVariable Long id) {
        PrestamoLibro prestamo = prestamoLibroService.obtenerPorId(id);
        return ResponseEntity.ok(prestamoLibroMapper.toDto(prestamo));
    }

    @PostMapping
    public ResponseEntity<PrestamoLibroResponseDTO> crearPrestamo(@Valid @RequestBody PrestamoLibroRequestDTO request) {
        PrestamoLibro prestamo = prestamoLibroMapper.toEntity(request);
        PrestamoLibro guardado = prestamoLibroService.crear(prestamo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(location).body(prestamoLibroMapper.toDto(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestamoLibroResponseDTO> actualizarPrestamo(
            @PathVariable Long id,
            @Valid @RequestBody PrestamoLibroRequestDTO request) {
        PrestamoLibro detalles = prestamoLibroMapper.toEntity(request);
        PrestamoLibro actualizado = prestamoLibroService.actualizar(id, detalles);
        return ResponseEntity.ok(prestamoLibroMapper.toDto(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoLibroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/devolver")
    public ResponseEntity<PrestamoLibroResponseDTO> marcarComoDevuelto(@PathVariable Long id) {
        PrestamoLibro actualizado = prestamoLibroService.marcarDevuelto(id);
        return ResponseEntity.ok(prestamoLibroMapper.toDto(actualizado));
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<PrestamoLibroResponseDTO>> obtenerPrestamosVencidos() {
        List<PrestamoLibro> vencidos = prestamoLibroService.obtenerVencidos();
        return ResponseEntity.ok(prestamoLibroMapper.toDtoList(vencidos));
    }

    @GetMapping("/usuario/{nombreUsuario}")
    public ResponseEntity<List<PrestamoLibroResponseDTO>> obtenerHistorialPorUsuario(@PathVariable String nombreUsuario) {
        List<PrestamoLibro> historial = prestamoLibroService.obtenerHistorialPorUsuario(nombreUsuario);
        return ResponseEntity.ok(prestamoLibroMapper.toDtoList(historial));
    }

    @GetMapping("/usuario/{nombreUsuario}/resumen")
    public ResponseEntity<ResumenUsuarioDTO> obtenerResumenPorUsuario(@PathVariable String nombreUsuario) {
        ResumenUsuarioDTO resumen = prestamoLibroService.obtenerResumenPorUsuario(nombreUsuario);
        return ResponseEntity.ok(resumen);
    }
}
