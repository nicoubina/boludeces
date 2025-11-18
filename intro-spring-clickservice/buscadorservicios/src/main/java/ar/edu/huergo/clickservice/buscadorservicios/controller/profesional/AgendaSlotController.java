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

import ar.edu.huergo.clickservice.buscadorservicios.dto.profesional.AgendaSlotDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.AgendaSlot;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.profesional.AgendaSlotMapper;
import ar.edu.huergo.clickservice.buscadorservicios.service.profesional.AgendaSlotService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agenda-slots")
public class AgendaSlotController {

    @Autowired
    private AgendaSlotService agendaSlotService;

    @Autowired
    private AgendaSlotMapper agendaSlotMapper;

    @GetMapping
    public ResponseEntity<List<AgendaSlotDTO>> obtenerTodosLosAgendaSlots() {
        List<AgendaSlot> agendaSlots = agendaSlotService.obtenerTodosLosAgendaSlots();
        return ResponseEntity.ok(agendaSlotMapper.toDTOList(agendaSlots));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaSlotDTO> obtenerAgendaSlotPorId(@PathVariable Long id) {
        AgendaSlot agendaSlot = agendaSlotService.obtenerAgendaSlotPorId(id);
        return ResponseEntity.ok(agendaSlotMapper.toDTO(agendaSlot));
    }

    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<AgendaSlotDTO>> obtenerAgendaSlotsPorProfesional(@PathVariable Long profesionalId) {
        List<AgendaSlot> agendaSlots = agendaSlotService.obtenerAgendaSlotsPorProfesional(profesionalId);
        return ResponseEntity.ok(agendaSlotMapper.toDTOList(agendaSlots));
    }

    @PostMapping
    public ResponseEntity<AgendaSlotDTO> crearAgendaSlot(@Valid @RequestBody AgendaSlotDTO agendaSlotDTO) {
        AgendaSlot agendaSlot = agendaSlotMapper.toEntity(agendaSlotDTO);
        AgendaSlot agendaSlotCreado = agendaSlotService.crearAgendaSlot(agendaSlot);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(agendaSlotCreado.getId()).toUri();
        return ResponseEntity.created(location).body(agendaSlotMapper.toDTO(agendaSlotCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaSlotDTO> actualizarAgendaSlot(@PathVariable Long id,
            @Valid @RequestBody AgendaSlotDTO agendaSlotDTO) {
        AgendaSlot agendaSlot = agendaSlotMapper.toEntity(agendaSlotDTO);
        AgendaSlot agendaSlotActualizado = agendaSlotService.actualizarAgendaSlot(id, agendaSlot);
        return ResponseEntity.ok(agendaSlotMapper.toDTO(agendaSlotActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAgendaSlot(@PathVariable Long id) {
        agendaSlotService.eliminarAgendaSlot(id);
        return ResponseEntity.noContent().build();
    }
}
