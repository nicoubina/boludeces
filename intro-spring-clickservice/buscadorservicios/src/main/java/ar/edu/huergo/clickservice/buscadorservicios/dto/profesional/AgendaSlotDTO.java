package ar.edu.huergo.clickservice.buscadorservicios.dto.profesional;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para exponer la disponibilidad horaria de un profesional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaSlotDTO {

    private Long id;

    @NotNull(message = "El identificador del profesional es obligatorio")
    private Long profesionalId;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha y hora de fin es obligatoria")
    private LocalDateTime fechaFin;

    private Boolean disponible = Boolean.TRUE;

    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isRangoHorarioValido() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }
        return fechaFin.isAfter(fechaInicio);
    }
}
