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

class ReseñaValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería identificar errores en reseña inválida")
    void deberiaIdentificarErroresEnReseñaInvalida() {
        // Given
        Reseña reseña = new Reseña();
        reseña.setOrdenId(null);
        reseña.setRating(6);
        reseña.setComentario("Muy corto");
        reseña.setFecha(null);
        reseña.setUsuario(null);
        reseña.setProfesional(null);

        // When
        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 5);

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("ordenId"));
        assertTrue(propiedadesConError.contains("rating"));
        assertTrue(propiedadesConError.contains("comentario"));
        assertTrue(propiedadesConError.contains("fecha"));
        assertTrue(propiedadesConError.contains("usuario"));
        assertTrue(propiedadesConError.contains("profesional"));
    }

    @Test
    @DisplayName("Debería validar una reseña completa")
    void deberiaValidarUnaReseñaCompleta() {
        // Given
        Reseña reseña = construirReseñaValida();

        // When
        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería permitir comentarios extensos dentro del límite")
    void deberiaPermitirComentariosExtensosDentroDelLimite() {
        // Given
        Reseña reseña = construirReseñaValida();
        reseña.setComentario("X".repeat(500));

        // When
        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    private Reseña construirReseñaValida() {
        Reseña reseña = new Reseña();
        reseña.setOrdenId(10L);
        reseña.setRating(5);
        reseña.setComentario("El profesional llegó a tiempo y resolvió el problema rápidamente");
        reseña.setFecha(LocalDateTime.now());
        reseña.setUsuario(crearUsuarioBasico(11L));
        Profesional profesional = new Profesional();
        profesional.setId(7L);
        reseña.setProfesional(profesional);
        return reseña;
    }

    private Usuario crearUsuarioBasico(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Carlos");
        usuario.setApellido("Ramírez");
        usuario.setDni("40123456");
        usuario.setTelefono("+54 9 11 9876-5432");
        usuario.setCalle("Av. Principal");
        usuario.setAltura(789);
        usuario.setUsername("usuario" + id + "@example.com");
        usuario.setPassword("contraseña_ultra_segura_" + id);
        return usuario;
    }
}