package ar.edu.huergo.clickservice.buscadorservicios.dto.servicio;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Comision.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComisionDTO {
    private Long id;

    @NotNull(message = "El identificador del pago es obligatorio")
    @Positive(message = "El identificador del pago debe ser positivo")
    private Long pagoId;

    @NotNull(message = "La tasa de comisión es obligatoria")
    @Positive(message = "La tasa de comisión debe ser positiva")
    private Double tasa;

    @NotNull(message = "La base de cálculo es obligatoria")
    @PositiveOrZero(message = "La base de cálculo no puede ser negativa")
    private Double base;

    @NotNull(message = "El monto de comisión es obligatorio")
    @PositiveOrZero(message = "El monto de comisión no puede ser negativo")
    private Double monto;

    @NotNull(message = "La fecha de la comisión es obligatoria")
    @PastOrPresent(message = "La fecha de la comisión no puede ser futura")
    private LocalDate fecha;
}
