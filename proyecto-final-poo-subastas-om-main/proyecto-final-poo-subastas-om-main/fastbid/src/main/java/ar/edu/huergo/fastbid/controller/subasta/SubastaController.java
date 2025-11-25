package ar.edu.huergo.fastbid.controller.subasta;

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

import ar.edu.huergo.fastbid.dto.SubastaDTO;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.mapper.SubastaMapper;
import ar.edu.huergo.fastbid.service.SubastaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subastas")
public class SubastaController {

    @Autowired
    private SubastaService subastaService;

    @Autowired
    private SubastaMapper subastaMapper;

    @GetMapping
    public ResponseEntity<List<SubastaDTO>> obtenerSubastas() {
        List<Subasta> subastas = subastaService.obtenerSubastas();
        List<SubastaDTO> subastasDto = subastaMapper.toDtoList(subastas);
        return ResponseEntity.ok(subastasDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubastaDTO> obtenerSubastaPorId(@PathVariable Long id) {
        Subasta subasta = subastaService.obtenerSubastaPorId(id);
        SubastaDTO subastaDto = subastaMapper.toDto(subasta);
        return ResponseEntity.ok(subastaDto);
    }

    @PostMapping
    public ResponseEntity<SubastaDTO> crearSubasta(@Valid @RequestBody SubastaDTO subastaDto) {
        Subasta subasta = subastaMapper.toEntity(subastaDto);
        Subasta subastaCreada = subastaService.crearSubasta(subasta);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(subastaCreada.getIdSubasta()).toUri();
        return ResponseEntity.created(location).body(subastaMapper.toDto(subastaCreada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubastaDTO> actualizarSubasta(@PathVariable Long id, @Valid @RequestBody SubastaDTO subastaDto) {
        Subasta subasta = subastaMapper.toEntity(subastaDto);
        Subasta subastaActualizada = subastaService.actualizarSubasta(id, subasta);
        return ResponseEntity.ok(subastaMapper.toDto(subastaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSubasta(@PathVariable Long id) {
        subastaService.eliminarSubasta(id);
        return ResponseEntity.noContent().build();
    }
}
