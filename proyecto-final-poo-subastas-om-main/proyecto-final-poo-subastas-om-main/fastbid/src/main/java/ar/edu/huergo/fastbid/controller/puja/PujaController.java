package ar.edu.huergo.fastbid.controller.puja;

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

import ar.edu.huergo.fastbid.dto.PujaDTO;
import ar.edu.huergo.fastbid.entity.subastas.Puja;
import ar.edu.huergo.fastbid.mapper.PujaMapper;
import ar.edu.huergo.fastbid.service.PujaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pujas")
public class PujaController {

    @Autowired
    private PujaService pujaService;

    @Autowired
    private PujaMapper pujaMapper;

    @GetMapping
    public ResponseEntity<List<PujaDTO>> obtenerPujas() {
        List<Puja> pujas = pujaService.obtenerPujas();
        List<PujaDTO> pujasDto = pujaMapper.toDtoList(pujas);
        return ResponseEntity.ok(pujasDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PujaDTO> obtenerPujaPorId(@PathVariable Long id) {
        Puja puja = pujaService.obtenerPujaPorId(id);
        PujaDTO pujaDto = pujaMapper.toDto(puja);
        return ResponseEntity.ok(pujaDto);
    }

    @PostMapping
    public ResponseEntity<PujaDTO> crearPuja(@Valid @RequestBody PujaDTO pujaDto) {
        Puja puja = pujaMapper.toEntity(pujaDto);
        Puja pujaCreada = pujaService.crearPuja(puja);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pujaCreada.getIdPuja()).toUri();
        return ResponseEntity.created(location).body(pujaMapper.toDto(pujaCreada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PujaDTO> actualizarPuja(@PathVariable Long id, @Valid @RequestBody PujaDTO pujaDto) {
        Puja puja = pujaMapper.toEntity(pujaDto);
        Puja pujaActualizada = pujaService.actualizarPuja(id, puja);
        return ResponseEntity.ok(pujaMapper.toDto(pujaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPuja(@PathVariable Long id) {
        pujaService.eliminarPuja(id);
        return ResponseEntity.noContent().build();
    }
}
