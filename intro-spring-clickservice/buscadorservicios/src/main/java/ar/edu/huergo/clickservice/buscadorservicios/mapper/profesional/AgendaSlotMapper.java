package ar.edu.huergo.clickservice.buscadorservicios.mapper.profesional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.profesional.AgendaSlotDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.AgendaSlot;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;

@Component
public class AgendaSlotMapper {

    public AgendaSlotDTO toDTO(AgendaSlot agendaSlot) {
        if (agendaSlot == null) {
            return null;
        }
        AgendaSlotDTO dto = new AgendaSlotDTO();
        dto.setId(agendaSlot.getId());
        dto.setProfesionalId(agendaSlot.getProfesional() != null ? agendaSlot.getProfesional().getId() : null);
        dto.setFechaInicio(agendaSlot.getFechaInicio());
        dto.setFechaFin(agendaSlot.getFechaFin());
        dto.setDisponible(Boolean.TRUE.equals(agendaSlot.getDisponible()));
        return dto;
    }

    public AgendaSlot toEntity(AgendaSlotDTO dto) {
        if (dto == null) {
            return null;
        }
        AgendaSlot agendaSlot = new AgendaSlot();
        agendaSlot.setId(dto.getId());
        if (dto.getProfesionalId() != null) {
            Profesional profesional = new Profesional();
            profesional.setId(dto.getProfesionalId());
            agendaSlot.setProfesional(profesional);
        }
        agendaSlot.setFechaInicio(dto.getFechaInicio());
        agendaSlot.setFechaFin(dto.getFechaFin());
        agendaSlot.setDisponible(dto.getDisponible() == null ? Boolean.TRUE : dto.getDisponible());
        return agendaSlot;
    }

    public List<AgendaSlotDTO> toDTOList(List<AgendaSlot> agendaSlots) {
        if (agendaSlots == null) {
            return new ArrayList<>();
        }
        return agendaSlots.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AgendaSlot> toEntityList(List<AgendaSlotDTO> agendaSlotsDTO) {
        if (agendaSlotsDTO == null) {
            return new ArrayList<>();
        }
        return agendaSlotsDTO.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
