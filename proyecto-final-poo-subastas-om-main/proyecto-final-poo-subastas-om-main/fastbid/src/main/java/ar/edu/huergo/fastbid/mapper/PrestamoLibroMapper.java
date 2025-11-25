package ar.edu.huergo.fastbid.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.huergo.fastbid.dto.PrestamoLibroRequestDTO;
import ar.edu.huergo.fastbid.dto.PrestamoLibroResponseDTO;
import ar.edu.huergo.fastbid.entity.prestamo.PrestamoLibro;

@Component
public class PrestamoLibroMapper {

    public PrestamoLibro toEntity(PrestamoLibroRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        PrestamoLibro entity = new PrestamoLibro();
        entity.setTituloLibro(dto.getTituloLibro());
        entity.setNombreUsuario(dto.getNombreUsuario());
        entity.setDiasPrestamo(dto.getDiasPrestamo());
        return entity;
    }

    public PrestamoLibroResponseDTO toDto(PrestamoLibro entity) {
        if (entity == null) {
            return null;
        }
        PrestamoLibroResponseDTO dto = new PrestamoLibroResponseDTO();
        dto.setId(entity.getId());
        dto.setTituloLibro(entity.getTituloLibro());
        dto.setNombreUsuario(entity.getNombreUsuario());
        dto.setFechaPrestamo(entity.getFechaPrestamo());
        dto.setFechaDevolucion(entity.getFechaDevolucion());
        dto.setDevuelto(entity.getDevuelto());
        return dto;
    }

    public List<PrestamoLibroResponseDTO> toDtoList(List<PrestamoLibro> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
