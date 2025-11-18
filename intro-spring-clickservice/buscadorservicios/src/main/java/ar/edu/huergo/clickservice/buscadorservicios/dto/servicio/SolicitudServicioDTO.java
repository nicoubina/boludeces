package ar.edu.huergo.clickservice.buscadorservicios.dto.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ar.edu.huergo.clickservice.buscadorservicios.dto.security.UsuarioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio.EstadoSolicitud;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir datos de SolicitudServicio entre capas
 * Representa los datos que se exponen en la API REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudServicioDTO {

    private Long id;

    @NotNull(message = "El servicio es obligatorio")
    private ServicioDTO servicio;

    @NotNull(message = "El cliente es obligatorio")
    private UsuarioDTO cliente;

    @NotBlank(message = "La descripción del problema es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String descripcionProblema;

    @NotBlank(message = "La dirección del servicio es obligatoria")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    private String direccionServicio;

    @NotNull(message = "La fecha solicitada es obligatoria")
    @Future(message = "La fecha solicitada debe ser futura")
    private LocalDate fechaSolicitada;

    @NotBlank(message = "La franja horaria es obligatoria")
    @Size(max = 50, message = "La franja horaria no puede exceder los 50 caracteres")
    private String franjaHoraria;

    @NotNull(message = "El presupuesto máximo es obligatorio")
    @Positive(message = "El presupuesto máximo debe ser mayor a 0")
    private Double presupuestoMaximo;

    private EstadoSolicitud estado;

    private LocalDateTime fechaCreacion;

    @Size(max = 500, message = "Los comentarios no pueden exceder los 500 caracteres")
    private String comentariosAdicionales;
}