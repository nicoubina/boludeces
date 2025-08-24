package ar.edu.huergo.lcarera.restaurante.dto.plato;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO para transferencia de datos de Plato
 * Representa los datos que se exponen en la API REST
 */
@Data
@AllArgsConstructor
public abstract class PlatoDTO {
    Long id;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String nombre;
    String descripcion;
    @Positive(message = "El precio debe ser positivo")
    @NotNull(message = "El precio no puede ser nulo")
    Double precio;
}