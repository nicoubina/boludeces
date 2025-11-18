package ar.edu.huergo.clickservice.buscadorservicios.mapper.profesional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.profesional.ReseñaDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Reseña;

@Component
public class ReseñaMapper {

    /**
     * Convierte una entidad Reseña a ReseñaDTO
     */
    public ReseñaDTO toDTO(Reseña reseña) {
        if (reseña == null) {
            return null;
        }
        
        ReseñaDTO dto = new ReseñaDTO();
        dto.setId(reseña.getId());
        dto.setOrdenId(reseña.getOrdenId());
        dto.setRating(reseña.getRating());
        dto.setComentario(reseña.getComentario());
        dto.setFecha(reseña.getFecha());
        
        // Mapear IDs de las relaciones
        if (reseña.getUsuario() != null) {
            dto.setUsuarioId(reseña.getUsuario().getId());
            dto.setUsuarioUsername(reseña.getUsuario().getUsername());
        }
        
        if (reseña.getProfesional() != null) {
            dto.setProfesionalId(reseña.getProfesional().getId());
            dto.setProfesionalNombre(reseña.getProfesional().getNombreCompleto());
        }
        
        return dto;
    }

    /**
     * Convierte un ReseñaDTO a entidad Reseña
     * Nota: Las relaciones (Usuario, Profesional) deben ser establecidas en el Service
     */
    public Reseña toEntity(ReseñaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Reseña reseña = new Reseña();
        reseña.setId(dto.getId());
        reseña.setOrdenId(dto.getOrdenId());
        reseña.setRating(dto.getRating());
        reseña.setComentario(dto.getComentario());
        reseña.setFecha(dto.getFecha());
        
        // Las relaciones Usuario y Profesional se establecen en el Service
        // usando los IDs del DTO
        
        return reseña;
    }

    /**
     * Convierte una lista de entidades Reseña a lista de ReseñaDTO
     */
    public List<ReseñaDTO> toDTOList(List<Reseña> reseñas) {
        if (reseñas == null) {
            return new ArrayList<>();
        }
        return reseñas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de ReseñaDTO a lista de entidades Reseña
     */
    public List<Reseña> toEntityList(List<ReseñaDTO> reseñasDTO) {
        if (reseñasDTO == null) {
            return new ArrayList<>();
        }
        return reseñasDTO.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}