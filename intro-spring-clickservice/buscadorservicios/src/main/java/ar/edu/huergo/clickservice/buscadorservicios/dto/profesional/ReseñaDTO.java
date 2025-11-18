package ar.edu.huergo.clickservice.buscadorservicios.dto.profesional;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Reseña
 * Representa los datos que se exponen en la API REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReseñaDTO {
    
    private Long id;
    
    @NotNull(message = "El ID de la orden es obligatorio")
    private Long ordenId;
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;
    
    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres")
    private String comentario;
    
    private LocalDateTime fecha;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
    
    @NotNull(message = "El ID del profesional es obligatorio")
    private Long profesionalId;
    
    // Campos adicionales para mostrar información completa
    private String usuarioUsername;
    private String profesionalNombre;
}