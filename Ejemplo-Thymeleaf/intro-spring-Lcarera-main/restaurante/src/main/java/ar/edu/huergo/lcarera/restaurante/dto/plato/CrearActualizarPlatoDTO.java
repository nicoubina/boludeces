package ar.edu.huergo.lcarera.restaurante.dto.plato;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO para transferencia de datos de Plato
 * Representa los datos que se exponen en la API REST
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrearActualizarPlatoDTO extends PlatoDTO {
    @NotEmpty(message = "La lista de ingredientes no puede estar vacía")
    List<Long> ingredientesIds;

    public CrearActualizarPlatoDTO(Long id, String nombre, String descripcion, Double precio, List<Long> ingredientesIds) {
        super(id, nombre, descripcion, precio);
        this.ingredientesIds = ingredientesIds;
    }
}