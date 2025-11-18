package ar.edu.huergo.clickservice.buscadorservicios.entity.profesional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ProfesionalValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería detectar errores en profesional inválido")
    void deberiaDetectarErroresEnProfesionalInvalido() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setUsuario(crearUsuarioBasico(1L));
        profesional.setNombreCompleto("Jo");
        profesional.setTelefono("123");
        profesional.setCalificacionPromedio(6.0);
        profesional.setTrabajosRealizados(-2);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 4);

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("nombreCompleto"));
        assertTrue(propiedadesConError.contains("telefono"));
        assertTrue(propiedadesConError.contains("calificacionPromedio"));
        assertTrue(propiedadesConError.contains("trabajosRealizados"));
    }

    @Test
    @DisplayName("Debería aceptar un profesional completo y válido")
    void deberiaAceptarUnProfesionalCompletoYValido() {
        // Given
        Profesional profesional = construirProfesionalValido();

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería rechazar teléfonos con formato inválido")
    void deberiaRechazarTelefonosConFormatoInvalido() {
        // Given
        Profesional profesional = construirProfesionalValido();
        
        String[] telefonosInvalidos = {
            "++54 11 5555-4444", // Múltiples signos +
            "abc12345", // Letras
            "123", // Muy corto
            "123456789012345678901", // Muy largo
            "+54 11 5555 4444 extra", // Caracteres extra
            "", // Vacío
            "   " // Solo espacios
        };

        for (String telefono : telefonosInvalidos) {
            profesional.setTelefono(telefono);

            // When
            Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

            // Then
            assertFalse(violaciones.isEmpty(), 
                "El teléfono " + telefono + " debería ser inválido");
            assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefono")));
        }
    }

    @Test
    @DisplayName("Debería validar rangos de calificación")
    void deberiaValidarRangosDeCalificacion() {
        // Given
        Profesional profesional = construirProfesionalValido();
        
        // Test invalid ratings
        Double[] calificacionesInvalidas = {
            -0.1, // Menor al mínimo
            5.1,  // Mayor al máximo
            null  // Nulo
        };

        for (Double calificacion : calificacionesInvalidas) {
            profesional.setCalificacionPromedio(calificacion);

            // When
            Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

            // Then
            assertFalse(violaciones.isEmpty(),
                "La calificación " + calificacion + " debería ser inválida");
            assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("calificacionPromedio")));
        }

        // Test valid ratings
        Double[] calificacionesValidas = {
            0.0,  // Mínimo
            2.5,  // Medio
            5.0   // Máximo
        };

        for (Double calificacion : calificacionesValidas) {
            profesional.setCalificacionPromedio(calificacion);
            
            // When
            Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

            // Then
            assertTrue(violaciones.isEmpty(),
                "La calificación " + calificacion + " debería ser válida");
        }
    }

    @Test
    @DisplayName("Debería validar longitud de descripción")
    void deberiaValidarLongitudDeDescripcion() {
        // Given
        Profesional profesional = construirProfesionalValido();
        
        // Test descripción demasiado larga (501 caracteres)
        String descripcionLarga = "a".repeat(501);
        profesional.setDescripcion(descripcionLarga);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("descripcion")));

        // Test descripción válida
        profesional.setDescripcion("a".repeat(500));
        violaciones = validator.validate(profesional);
        assertTrue(violaciones.isEmpty());
    }

    private Profesional construirProfesionalValido() {
        Profesional profesional = new Profesional();
        profesional.setUsuario(crearUsuarioBasico(2L));
        profesional.setNombreCompleto("María González");
        profesional.setTelefono("+54 9 11 4321-5678");
        profesional.setDescripcion("Especialista en instalaciones eléctricas y reparaciones hogareñas");
        profesional.setCalificacionPromedio(4.8);
        profesional.setTrabajosRealizados(120);
        profesional.setDisponible(Boolean.TRUE);
        profesional.setZonaTrabajo("Ciudad Autónoma de Buenos Aires");
        profesional.setFechaRegistro(LocalDateTime.now());
        return profesional;
    }

    private Usuario crearUsuarioBasico(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Laura");
        usuario.setApellido("López");
        usuario.setDni("30123456");
        usuario.setTelefono("+54 9 11 2222-3333");
        usuario.setCalle("Calle Siempre Viva");
        usuario.setAltura(742);
        usuario.setUsername("usuario" + id + "@example.com");
        usuario.setPassword("contraseña_segura_para_usuario" + id);
        return usuario;
    }
}