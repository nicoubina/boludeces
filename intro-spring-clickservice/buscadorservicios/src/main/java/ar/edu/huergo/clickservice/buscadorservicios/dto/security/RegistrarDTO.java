package ar.edu.huergo.clickservice.buscadorservicios.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistrarDTO(
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 60, message = "El nombre no puede superar los 60 caracteres")
        String nombre,
        @NotBlank(message = "El apellido es requerido")
        @Size(max = 60, message = "El apellido no puede superar los 60 caracteres")
        String apellido,
        @NotBlank(message = "El DNI es requerido")
        @Pattern(regexp = "^\\d{7,10}$", message = "El DNI debe tener entre 7 y 10 dígitos")
        String dni,
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,19}$", message = "El formato del teléfono no es válido")
        String telefono,
        @NotBlank(message = "La calle es requerida")
        @Size(max = 120, message = "La calle no puede superar los 120 caracteres")
        String calle,
        @NotNull(message = "La altura es requerida")
        @Positive(message = "La altura debe ser un número positivo")
        Integer altura,
        @NotBlank(message = "El nombre de usuario es requerido")
        @Email(message = "El nombre de usuario debe ser un email válido")
        String username,
        @NotBlank(message = "La contraseña es requerida")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{16,}$", message = "La contraseña debe tener al menos 16 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
        String password,
        @NotBlank(message = "La verificación de contraseña es requerida")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{16,}$", message = "La verificación de contraseña debe tener al menos 16 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
        String verificacionPassword) {
}
