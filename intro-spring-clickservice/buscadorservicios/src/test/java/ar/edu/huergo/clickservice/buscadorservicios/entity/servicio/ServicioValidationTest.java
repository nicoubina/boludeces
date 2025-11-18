package ar.edu.huergo.clickservice.buscadorservicios.entity.servicio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ServicioValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar las restricciones de servicio inválido")
    void deberiaValidarRestriccionesDeServicioInvalido() {
        // Given
        Servicio servicioInvalido = new Servicio();
        servicioInvalido.setPrecioHora(-5.0); // Precio negativo

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicioInvalido);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 2); // Al menos 2 errores

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("nombre"));
        assertTrue(propiedadesConError.contains("precioHora"));
    }

    @Test
    @DisplayName("Debería validar servicio con constructor completo")
    void deberiaValidarServicioConConstructorCompleto() {
        // Given
        Servicio servicio = new Servicio(1L, "Plomería", 35.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería manejar nombres con caracteres especiales")
    void deberiaManejarNombresConCaracteresEspeciales() {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("Aire Acondicionado");
        servicio.setPrecioHora(45.00);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertTrue(violaciones.isEmpty(), "Los nombres con espacios deberían ser válidos");
    }
}