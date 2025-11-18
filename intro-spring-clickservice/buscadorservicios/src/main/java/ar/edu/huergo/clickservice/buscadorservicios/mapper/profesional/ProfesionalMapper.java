package ar.edu.huergo.clickservice.buscadorservicios.mapper.profesional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.profesional.ProfesionalDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.ServicioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.security.UsuarioMapper;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.servicio.ServicioMapper;

@Component
public class ProfesionalMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;
    
    @Autowired
    private ServicioMapper servicioMapper;

    /**
     * Convierte una entidad Profesional a ProfesionalDTO
     */
    public ProfesionalDTO toDTO(Profesional profesional) {
        if (profesional == null) {
            return null;
        }

        ProfesionalDTO dto = new ProfesionalDTO();
        dto.setId(profesional.getId());
        dto.setUsuario(usuarioMapper.toDTO(profesional.getUsuario()));
        dto.setNombreCompleto(profesional.getNombreCompleto());
        dto.setTelefono(profesional.getTelefono());
        dto.setDescripcion(profesional.getDescripcion());
        dto.setCalificacionPromedio(profesional.getCalificacionPromedio());
        dto.setTrabajosRealizados(profesional.getTrabajosRealizados());
        dto.setDisponible(profesional.getDisponible());
        dto.setZonaTrabajo(profesional.getZonaTrabajo());
        dto.setFechaRegistro(profesional.getFechaRegistro());
        
        // Mapear servicios
        if (profesional.getServicios() != null) {
            Set<ServicioDTO> serviciosDTO = profesional.getServicios().stream()
                    .map(servicioMapper::toDTO)
                    .collect(Collectors.toSet());
            dto.setServicios(serviciosDTO);
            
            Set<Long> serviciosIds = profesional.getServicios().stream()
                    .map(Servicio::getId)
                    .collect(Collectors.toSet());
            dto.setServiciosIds(serviciosIds);
        }

        return dto;
    }

    /**
     * Convierte un ProfesionalDTO a entidad Profesional
     */
    public Profesional toEntity(ProfesionalDTO dto) {
        if (dto == null) {
            return null;
        }

        Profesional profesional = new Profesional();
        profesional.setId(dto.getId());
        profesional.setNombreCompleto(dto.getNombreCompleto());
        profesional.setTelefono(dto.getTelefono());
        profesional.setDescripcion(dto.getDescripcion());
        profesional.setCalificacionPromedio(dto.getCalificacionPromedio());
        profesional.setTrabajosRealizados(dto.getTrabajosRealizados());
        profesional.setDisponible(dto.getDisponible());
        profesional.setZonaTrabajo(dto.getZonaTrabajo());
        profesional.setFechaRegistro(dto.getFechaRegistro());

        return profesional;
    }

    /**
     * Convierte una lista de entidades Profesional a lista de ProfesionalDTO
     */
    public List<ProfesionalDTO> toDTOList(List<Profesional> profesionales) {
        if (profesionales == null) {
            return null;
        }
        
        return profesionales.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de ProfesionalDTO a lista de entidades Profesional
     */
    public List<Profesional> toEntityList(List<ProfesionalDTO> profesionalesDTO) {
        if (profesionalesDTO == null) {
            return null;
        }
        
        return profesionalesDTO.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}