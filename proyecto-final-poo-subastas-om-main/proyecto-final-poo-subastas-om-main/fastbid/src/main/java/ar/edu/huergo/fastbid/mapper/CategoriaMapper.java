package ar.edu.huergo.fastbid.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.fastbid.dto.CategoriaDTO;
import ar.edu.huergo.fastbid.entity.Categoria;

@Component
public class CategoriaMapper {

    public CategoriaDTO toDto(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaDTO(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.isActiva()
        );
    }

    public Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) {
            return null;
        }

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(dto.idCategoria());
        categoria.setNombre(dto.nombre());
        categoria.setDescripcion(dto.descripcion());
        categoria.setActiva(dto.activa());

        return categoria;
    }

    public List<CategoriaDTO> toDtoList(List<Categoria> categorias) {
        if (categorias == null) {
            return new ArrayList<>();
        }
        return categorias.stream().map(this::toDto).collect(Collectors.toList());
    }
}