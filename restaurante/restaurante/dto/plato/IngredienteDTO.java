package ar.edu.huergo.lcarera.restaurante.dto.plato;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para transferencia de datos de Ingrediente
 * Representa los datos que se exponen en la API REST
 */
public record IngredienteDTO(
    // El id puede ser nulo en creación; se completa al persistir
    Long id,
    // No permite vacío o solo espacios y exige un rango de longitud
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String nombre
) {
}