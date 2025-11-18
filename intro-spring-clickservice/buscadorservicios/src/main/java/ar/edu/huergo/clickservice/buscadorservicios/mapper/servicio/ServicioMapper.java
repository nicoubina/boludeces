package ar.edu.huergo.clickservice.buscadorservicios.mapper.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.ServicioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;

@Component
public class ServicioMapper {

    /**
     * Convierte una entidad Servicio a ServicioDTO
     */
    public ServicioDTO toDTO(Servicio servicio) {
        if (servicio == null) {
            return null;
        }
        return new ServicioDTO(
            servicio.getId(), 
            servicio.getNombre(), 
            servicio.getPrecioHora()
        );
    }

    /**
     * Convierte un ServicioDTO a entidad Servicio
     */
    public Servicio toEntity(ServicioDTO dto) {
        if (dto == null) {
            return null;
        }
        Servicio servicio = new Servicio();
        servicio.setId(dto.getId());
        servicio.setNombre(dto.getNombre());
        servicio.setPrecioHora(dto.getPrecioHora());
        return servicio;
    }

    /**
     * Convierte una lista de entidades Servicio a lista de ServicioDTO
     */
    public List<ServicioDTO> toDTOList(List<Servicio> servicios) {
        if (servicios == null) {
            return new ArrayList<>();
        }
        return servicios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de ServicioDTO a lista de entidades Servicio
     */
    public List<Servicio> toEntityList(List<ServicioDTO> serviciosDTO) {
        if (serviciosDTO == null) {
            return new ArrayList<>();
        }
        return serviciosDTO.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}