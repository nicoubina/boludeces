package ar.edu.huergo.clickservice.buscadorservicios.entity.profesional;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad que representa un bloque de disponibilidad en la agenda de un profesional.
 *
 * Cada {@link AgendaSlot} indica un rango horario en el que el profesional puede
 * aceptar nuevos trabajos.
 */
@Entity
@Table(name = "agenda_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "profesional")
public class AgendaSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "profesional_id", nullable = false)
    @NotNull(message = "El profesional es obligatorio")
    private Profesional profesional;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha y hora de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private Boolean disponible = Boolean.TRUE;

    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isRangoHorarioValido() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }
        return fechaFin.isAfter(fechaInicio);
    }

    @PrePersist
    @PreUpdate
    private void ensureDefaults() {
        if (disponible == null) {
            disponible = Boolean.TRUE;
        }
    }
}
