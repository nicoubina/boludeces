package ar.edu.huergo.fastbid.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.huergo.fastbid.dto.ProductoInventarioRequestDTO;
import ar.edu.huergo.fastbid.dto.ProductoInventarioResponseDTO;
import ar.edu.huergo.fastbid.entity.inventario.ProductoInventario;

@Component
public class ProductoInventarioMapper {

    public ProductoInventario toEntity(ProductoInventarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        ProductoInventario entity = new ProductoInventario();
        entity.setNombre(dto.getNombre());
        entity.setCategoria(dto.getCategoria());
        entity.setPrecio(dto.getPrecio());
        return entity;
    }

    public ProductoInventarioResponseDTO toDto(ProductoInventario entity) {
        if (entity == null) {
            return null;
        }
        ProductoInventarioResponseDTO dto = new ProductoInventarioResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCategoria(entity.getCategoria());
        dto.setPrecio(entity.getPrecio());
        dto.setStock(entity.getStock());
        return dto;
    }

    public List<ProductoInventarioResponseDTO> toDtoList(List<ProductoInventario> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
