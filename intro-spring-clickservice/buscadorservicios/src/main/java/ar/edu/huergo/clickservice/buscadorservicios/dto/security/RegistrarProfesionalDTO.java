package ar.edu.huergo.clickservice.buscadorservicios.dto.security;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro de un nuevo profesional
 * Incluye tanto datos de usuario como datos específicos del profesional
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarProfesionalDTO {

    // Datos de usuario
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no puede exceder los 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "El apellido no puede exceder los 60 caracteres")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{7,10}$", message = "El DNI debe tener entre 7 y 10 dígitos")
    private String dni;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{16,}$", 
             message = "La contraseña debe tener al menos 16 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
    private String password;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 120, message = "La calle no puede exceder los 120 caracteres")
    private String calle;

    @NotNull(message = "La altura es obligatoria")
    @Positive(message = "La altura debe ser un número positivo")
    private Integer altura;

    // Datos específicos del profesional
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El teléfono es obligatorio")
    // Regex corregido: no permite múltiples signos + consecutivos
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,19}$", message = "El formato del teléfono no es válido")
    private String telefono;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @Size(max = 200, message = "La zona de trabajo no puede exceder los 200 caracteres")
    private String zonaTrabajo;

    // IDs de los servicios que puede ofrecer el profesional
    @NotEmpty(message = "Debe seleccionar al menos un servicio")
    private Set<Long> serviciosIds;

    public void setDni(String dni) {
        this.dni = dni;
    }
}