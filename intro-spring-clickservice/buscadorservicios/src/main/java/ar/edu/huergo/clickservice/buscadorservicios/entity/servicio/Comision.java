package ar.edu.huergo.clickservice.buscadorservicios.entity.servicio;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa la comisión aplicada a un pago realizado a un profesional.
 *
 * Una comisión se calcula en base a un pago existente, su tasa porcentual y el
 * monto final que retiene la plataforma.
 */
@Entity
@Table(name = "comisiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El identificador del pago es obligatorio")
    @Positive(message = "El identificador del pago debe ser positivo")
    @Column(name = "pago_id", nullable = false)
    private Long pagoId;

    @NotNull(message = "La tasa de comisión es obligatoria")
    @Positive(message = "La tasa de comisión debe ser positiva")
    @Column(nullable = false)
    private Double tasa;

    @NotNull(message = "La base de cálculo es obligatoria")
    @PositiveOrZero(message = "La base de cálculo no puede ser negativa")
    @Column(nullable = false)
    private Double base;

    @NotNull(message = "El monto de comisión es obligatorio")
    @PositiveOrZero(message = "El monto de comisión no puede ser negativo")
    @Column(nullable = false)
    private Double monto;

    @NotNull(message = "La fecha de la comisión es obligatoria")
    @PastOrPresent(message = "La fecha de la comisión no puede ser futura")
    @Column(nullable = false)
    private LocalDate fecha;
}
