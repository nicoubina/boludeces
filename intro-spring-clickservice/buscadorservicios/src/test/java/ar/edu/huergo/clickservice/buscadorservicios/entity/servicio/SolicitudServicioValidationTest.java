package ar.edu.huergo.clickservice.buscadorservicios.entity.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class SolicitudServicioValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería detectar múltiples errores de validación")
    void deberiaDetectarMultiplesErroresDeValidacion() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setDescripcionProblema("Muy corto");
        solicitud.setDireccionServicio("Corta");
        solicitud.setFechaSolicitada(LocalDate.now().minusDays(1));
        solicitud.setFranjaHoraria("");
        solicitud.setPresupuestoMaximo(-150.0);
        solicitud.setComentariosAdicionales("X".repeat(520));

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 6);

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("servicio"));
        assertTrue(propiedadesConError.contains("cliente"));
        assertTrue(propiedadesConError.contains("descripcionProblema"));
        assertTrue(propiedadesConError.contains("direccionServicio"));
        assertTrue(propiedadesConError.contains("fechaSolicitada"));
        assertTrue(propiedadesConError.contains("franjaHoraria"));
        assertTrue(propiedadesConError.contains("presupuestoMaximo"));
        assertTrue(propiedadesConError.contains("comentariosAdicionales"));
    }

    @Test
    @DisplayName("Debería validar una solicitud completa")
    void deberiaValidarUnaSolicitudCompleta() {
        // Given
        SolicitudServicio solicitud = construirSolicitudValida();

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería inicializar estado y fecha de creación automáticamente")
    void deberiaInicializarEstadoYFechaDeCreacionAutomaticamente() {
        // Given
        SolicitudServicio solicitud = construirSolicitudValida();
        solicitud.setEstado(null);
        solicitud.setFechaCreacion(null);

        // When
        solicitud.onCreate();

        // Then
        assertNotNull(solicitud.getEstado());
        assertEquals(SolicitudServicio.EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertNotNull(solicitud.getFechaCreacion());
    }

    private SolicitudServicio construirSolicitudValida() {
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(new Servicio(1L, "Plomería", 30.0));
        solicitud.setCliente(crearUsuarioValido(1L, "cliente@example.com", "12345678"));
        solicitud.setDescripcionProblema("Necesito arreglar una pérdida de agua en la cocina");
        solicitud.setDireccionServicio("Calle Falsa 1234, Ciudad");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(15000.0);
        solicitud.setEstado(SolicitudServicio.EstadoSolicitud.ASIGNADA);
        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setComentariosAdicionales("Llamar antes de llegar");
        return solicitud;
    }

    private Usuario crearUsuarioValido(Long id, String username, String dni) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setDni(dni);
        usuario.setTelefono("+54 9 11 1234-5678");
        usuario.setCalle("Calle 1");
        usuario.setAltura(1234);
        usuario.setUsername(username);
        usuario.setPassword("contraseña_super_segura_123");
        return usuario;
    }
}