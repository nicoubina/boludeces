package ar.edu.huergo.clickservice.buscadorservicios.service.profesional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.AgendaSlot;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.AgendaSlotRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ProfesionalRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AgendaSlotService {

    @Autowired
    private AgendaSlotRepository agendaSlotRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    public List<AgendaSlot> obtenerTodosLosAgendaSlots() {
        return agendaSlotRepository.findAll();
    }

    public List<AgendaSlot> obtenerAgendaSlotsPorProfesional(Long profesionalId) {
        return agendaSlotRepository.findByProfesionalId(profesionalId);
    }

    public AgendaSlot obtenerAgendaSlotPorId(Long id) {
        return agendaSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AgendaSlot no encontrado"));
    }

    public AgendaSlot crearAgendaSlot(AgendaSlot agendaSlot) {
        Profesional profesional = obtenerProfesionalValido(agendaSlot);
        agendaSlot.setProfesional(profesional);
        if (agendaSlot.getDisponible() == null) {
            agendaSlot.setDisponible(Boolean.TRUE);
        }
        return agendaSlotRepository.save(agendaSlot);
    }

    public AgendaSlot actualizarAgendaSlot(Long id, AgendaSlot agendaSlotActualizado) {
        AgendaSlot agendaSlotExistente = obtenerAgendaSlotPorId(id);

        if (agendaSlotActualizado.getProfesional() != null && agendaSlotActualizado.getProfesional().getId() != null) {
            Profesional profesionalActualizado = profesionalRepository
                    .findById(agendaSlotActualizado.getProfesional().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
            agendaSlotExistente.setProfesional(profesionalActualizado);
        }

        agendaSlotExistente.setFechaInicio(agendaSlotActualizado.getFechaInicio());
        agendaSlotExistente.setFechaFin(agendaSlotActualizado.getFechaFin());
        agendaSlotExistente
                .setDisponible(agendaSlotActualizado.getDisponible() == null ? Boolean.TRUE
                        : agendaSlotActualizado.getDisponible());

        return agendaSlotRepository.save(agendaSlotExistente);
    }

    public void eliminarAgendaSlot(Long id) {
        AgendaSlot agendaSlot = obtenerAgendaSlotPorId(id);
        agendaSlotRepository.delete(agendaSlot);
    }

    private Profesional obtenerProfesionalValido(AgendaSlot agendaSlot) {
        if (agendaSlot.getProfesional() == null || agendaSlot.getProfesional().getId() == null) {
            throw new EntityNotFoundException("Profesional no encontrado");
        }

        Long profesionalId = agendaSlot.getProfesional().getId();
        return profesionalRepository.findById(profesionalId)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
    }
}
