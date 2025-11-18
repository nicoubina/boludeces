package ar.edu.huergo.clickservice.buscadorservicios.dto.profesional;

import java.time.LocalDateTime;
import java.util.Set;

import ar.edu.huergo.clickservice.buscadorservicios.dto.security.UsuarioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.ServicioDTO;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir datos de Profesional entre capas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionalDTO {

    private Long id;

    private UsuarioDTO usuario;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El teléfono es obligatorio")
    // Regex corregido: no permite múltiples signos + consecutivos
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,19}$", message = "El formato del teléfono no es válido")
    private String telefono;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @DecimalMin(value = "0.0", message = "La calificación debe ser mayor o igual a 0")
    @DecimalMax(value = "5.0", message = "La calificación debe ser menor o igual a 5")
    private Double calificacionPromedio;

    @Min(value = 0, message = "El número de trabajos no puede ser negativo")
    private Integer trabajosRealizados;

    private Boolean disponible;

    // Solo los IDs de los servicios para evitar referencias circulares
    private Set<Long> serviciosIds;

    // Información básica de servicios para mostrar
    private Set<ServicioDTO> servicios;

    @Size(max = 200, message = "La zona de trabajo no puede exceder los 200 caracteres")
    private String zonaTrabajo;

    private LocalDateTime fechaRegistro;
}