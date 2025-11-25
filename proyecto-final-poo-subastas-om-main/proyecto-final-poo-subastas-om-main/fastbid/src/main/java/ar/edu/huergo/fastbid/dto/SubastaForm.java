package ar.edu.huergo.fastbid.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubastaForm {

    @NotNull(message = "Selecciona un producto")
    private Long productoId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;

    @NotNull(message = "El precio inicial es obligatorio")
    @Positive(message = "El precio inicial debe ser mayor a 0")
    private Double precioInicial;

    @NotNull(message = "El incremento mínimo es obligatorio")
    @Positive(message = "El incremento mínimo debe ser mayor a 0")
    private Double incrementoMinimo;

    @Positive(message = "La compra inmediata debe ser mayor a 0")
    private Double compraInmediata;
}
