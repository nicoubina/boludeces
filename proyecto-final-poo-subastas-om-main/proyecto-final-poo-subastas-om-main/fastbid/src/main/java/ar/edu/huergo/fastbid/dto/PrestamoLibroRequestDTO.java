package ar.edu.huergo.fastbid.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrestamoLibroRequestDTO {

    @NotBlank(message = "El título del libro es obligatorio")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String tituloLibro;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 120, message = "El nombre de usuario no puede superar los 120 caracteres")
    private String nombreUsuario;

    @NotNull(message = "Los días de préstamo son obligatorios")
    @Min(value = 1, message = "Los días de préstamo deben ser al menos 1")
    @Max(value = 365, message = "Los días de préstamo no pueden superar 365")
    private Integer diasPrestamo;
}
