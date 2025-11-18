package ar.edu.huergo.clickservice.buscadorservicios.entity.profesional;

import java.time.LocalDateTime;
import java.util.Set;

import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un profesional en la plataforma ClickService.
 * 
 * Un profesional es un usuario que puede ofrecer uno o más servicios
 * y tiene información adicional como calificación, descripción y disponibilidad.
 */

@Table(name = "profesionales")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación uno a uno con Usuario
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, name = "nombre_completo")
    private String nombreCompleto;

    @NotBlank(message = "El teléfono es obligatorio")
    // Regex corregido: no permite múltiples signos + consecutivos
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,19}$", message = "El formato del teléfono no es válido")
    @Column(nullable = false, length = 20)
    private String telefono;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    // Calificación promedio del profesional (0.0 a 5.0)
    @NotNull(message = "La calificación no puede ser nula")
    @DecimalMin(value = "0.0", message = "La calificación debe ser mayor o igual a 0")
    @DecimalMax(value = "5.0", message = "La calificación debe ser menor o igual a 5")
    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

    // Número total de trabajos realizados
    @Min(value = 0, message = "El número de trabajos no puede ser negativo")
    @Column(name = "trabajos_realizados")
    private Integer trabajosRealizados = 0;

    // Si el profesional está disponible para trabajar
    @Column(nullable = false)
    private Boolean disponible = true;

    // Relación many-to-many con Servicio
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "profesional_servicio",
        joinColumns = @JoinColumn(name = "profesional_id"),
        inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private Set<Servicio> servicios;

    // Zona de trabajo (puede ser una dirección o descripción de la zona)
    @Size(max = 200, message = "La zona de trabajo no puede exceder los 200 caracteres")
    @Column(name = "zona_trabajo", length = 200)
    private String zonaTrabajo;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (calificacionPromedio == null) {
            calificacionPromedio = 0.0;
        }
        if (trabajosRealizados == null) {
            trabajosRealizados = 0;
        }
        if (disponible == null) {
            disponible = true;
        }
    }

}