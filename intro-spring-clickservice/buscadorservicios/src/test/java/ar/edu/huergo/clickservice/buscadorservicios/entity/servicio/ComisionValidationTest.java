package ar.edu.huergo.clickservice.buscadorservicios.entity.servicio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ComisionValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería rechazar comisiones con datos inválidos")
    void deberiaRechazarComisionesConDatosInvalidos() {
        // Given
        Comision comision = new Comision();
        comision.setPagoId(-10L);
        comision.setTasa(-5.0);
        comision.setBase(-250.0);
        comision.setMonto(-15.0);
        comision.setFecha(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 4);

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("pagoId"));
        assertTrue(propiedadesConError.contains("tasa"));
        assertTrue(propiedadesConError.contains("base"));
        assertTrue(propiedadesConError.contains("monto"));
        assertTrue(propiedadesConError.contains("fecha"));
    }

    @Test
    @DisplayName("Debería validar una comisión completa y válida")
    void deberiaValidarUnaComisionCompletaYValida() {
        // Given
        Comision comision = new Comision(1L, 25L, 12.5, 200.0, 25.0, LocalDate.now());

        // When
        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería permitir valores en cero para base y monto")
    void deberiaPermitirValoresEnCeroParaBaseYMonto() {
        // Given
        Comision comision = new Comision();
        comision.setPagoId(99L);
        comision.setTasa(5.0);
        comision.setBase(0.0);
        comision.setMonto(0.0);
        comision.setFecha(LocalDate.now());

        // When
        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        // Then
        assertTrue(violaciones.isEmpty());
    }
}