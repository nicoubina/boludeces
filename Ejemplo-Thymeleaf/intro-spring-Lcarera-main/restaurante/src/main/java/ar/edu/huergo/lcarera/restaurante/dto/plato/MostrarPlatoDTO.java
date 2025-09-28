package ar.edu.huergo.lcarera.restaurante.dto.plato;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO para transferencia de datos de Plato
 * Representa los datos que se exponen en la API REST
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MostrarPlatoDTO extends PlatoDTO {
    List<IngredienteDTO> ingredientes;

    public MostrarPlatoDTO(Long id, String nombre, String descripcion, Double precio, List<IngredienteDTO> ingredientes) {
        super(id, nombre, descripcion, precio);
        this.ingredientes = ingredientes;
    }
}