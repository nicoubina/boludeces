package ar.edu.huergo.fastbid.entity.prestamo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prestamos_libros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoLibro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del libro es obligatorio")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    @Column(nullable = false, length = 200)
    private String tituloLibro;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 120, message = "El nombre de usuario no puede superar los 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombreUsuario;

    @NotNull(message = "Los días de préstamo son obligatorios")
    @Min(value = 1, message = "Los días de préstamo deben ser al menos 1")
    @Max(value = 365, message = "Los días de préstamo no pueden superar 365")
    @Column(nullable = false)
    private Integer diasPrestamo;

    @NotNull(message = "La fecha de préstamo es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    @NotNull(message = "La fecha de devolución es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaDevolucion;

    @NotNull(message = "El estado de devolución es obligatorio")
    @Column(nullable = false)
    private Boolean devuelto = Boolean.FALSE;
}
