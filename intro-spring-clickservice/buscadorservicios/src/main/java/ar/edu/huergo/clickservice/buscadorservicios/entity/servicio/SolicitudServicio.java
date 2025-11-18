package ar.edu.huergo.clickservice.buscadorservicios.entity.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una solicitud de servicio en la plataforma ClickService.
 * 
 * Una solicitud es creada por un cliente cuando necesita un servicio específico,
 * incluye descripción del problema, presupuesto máximo, fecha solicitada y dirección.
 */
@Entity
@Table(name = "solicitudes_servicio")
@Data // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudServicio {

    // JPA: clave primaria autogenerada con estrategia IDENTITY
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Servicio - muchas solicitudes pueden ser del mismo servicio
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_id", nullable = false)
    @NotNull(message = "El servicio es obligatorio")
    private Servicio servicio;

    // Relación con Usuario (Cliente) - un cliente puede hacer muchas solicitudes
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Usuario cliente;

    // Descripción del problema o trabajo a realizar
    @NotBlank(message = "La descripción del problema es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    @Column(nullable = false, name = "descripcion_problema", length = 1000)
    private String descripcionProblema;

    // Por ahora usamos String para dirección, después se puede hacer FK a Dirección
    @NotBlank(message = "La dirección del servicio es obligatoria")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    @Column(nullable = false, name = "direccion_servicio", length = 200)
    private String direccionServicio;

    // Fecha en la que se solicita el servicio
    @NotNull(message = "La fecha solicitada es obligatoria")
    @Future(message = "La fecha solicitada debe ser futura")
    @Column(nullable = false, name = "fecha_solicitada")
    private LocalDate fechaSolicitada;

    // Franja horaria preferida (mañana, tarde, noche)
    @NotBlank(message = "La franja horaria es obligatoria")
    @Size(max = 50, message = "La franja horaria no puede exceder los 50 caracteres")
    @Column(nullable = false, name = "franja_horaria", length = 50)
    private String franjaHoraria;

    // Presupuesto máximo que el cliente está dispuesto a pagar
    @NotNull(message = "El presupuesto máximo es obligatorio")
    @Positive(message = "El presupuesto máximo debe ser mayor a 0")
    @Column(nullable = false, name = "presupuesto_maximo")
    private Double presupuestoMaximo;

    // Estado de la solicitud
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado;

    // Fecha y hora de creación de la solicitud
    @Column(nullable = false, name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Comentarios adicionales del cliente (opcional)
    @Size(max = 500, message = "Los comentarios no pueden exceder los 500 caracteres")
    @Column(name = "comentarios_adicionales", length = 500)
    private String comentariosAdicionales;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoSolicitud.PENDIENTE;
        }
    }

    /**
     * Enum que define los posibles estados de una solicitud de servicio
     */
    public enum EstadoSolicitud {
        PENDIENTE("Pendiente de asignación"),
        ASIGNADA("Asignada a un profesional"),
        EN_PROCESO("En proceso de realización"),
        COMPLETADA("Completada exitosamente"),
        CANCELADA("Cancelada por el cliente"),
        EXPIRADA("Expirada sin asignación");

        private final String descripcion;

        EstadoSolicitud(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}