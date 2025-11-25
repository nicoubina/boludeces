package ar.edu.huergo.fastbid.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CategoriaValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe validar una categoría correcta")
    void testCategoriaValida() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos y tecnología");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertTrue(violations.isEmpty(), "No debería haber violaciones de validación");
    }

    @Test
    @DisplayName("Debe fallar cuando el nombre es nulo")
    void testNombreNulo() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre(null);
        categoria.setDescripcion("Descripción válida");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("El nombre de la categoría es obligatorio")));
    }

    @Test
    @DisplayName("Debe fallar cuando el nombre está vacío")
    void testNombreVacio() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("");
        categoria.setDescripcion("Descripción válida");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("El nombre de la categoría es obligatorio")));
    }

    @Test
    @DisplayName("Debe fallar cuando el nombre es solo espacios en blanco")
    void testNombreSoloEspacios() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("   ");
        categoria.setDescripcion("Descripción válida");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("El nombre de la categoría es obligatorio")));
    }

    @Test
    @DisplayName("Debe fallar cuando el nombre es muy corto")
    void testNombreMuyCorto() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("A");
        categoria.setDescripcion("Descripción válida");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("El nombre debe tener entre 2 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debe fallar cuando el nombre es muy largo")
    void testNombreMuyLargo() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("A".repeat(101)); // 101 caracteres
        categoria.setDescripcion("Descripción válida");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("El nombre debe tener entre 2 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debe aceptar un nombre con la longitud mínima permitida")
    void testNombreLongitudMinima() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("AB"); // 2 caracteres (mínimo)
        categoria.setDescripcion("Descripción válida");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Debe aceptar un nombre con la longitud máxima permitida")
    void testNombreLongitudMaxima() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("A".repeat(100)); // 100 caracteres (máximo)
        categoria.setDescripcion("Descripción válida");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Debe fallar cuando la descripción es muy larga")
    void testDescripcionMuyLarga() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("A".repeat(501)); // 501 caracteres
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("La descripción no puede superar 500 caracteres")));
    }

    @Test
    @DisplayName("Debe aceptar una categoría sin descripción")
    void testSinDescripcion() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion(null);
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertTrue(violations.isEmpty(), "La descripción es opcional");
    }

    @Test
    @DisplayName("Debe aceptar una descripción vacía")
    void testDescripcionVacia() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("");
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertTrue(violations.isEmpty(), "La descripción puede estar vacía");
    }

    @Test
    @DisplayName("Debe aceptar una descripción con longitud máxima permitida")
    void testDescripcionLongitudMaxima() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("A".repeat(500)); // 500 caracteres (máximo)
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Debe validar múltiples errores simultáneamente")
    void testMultiplesErrores() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("A"); // Muy corto
        categoria.setDescripcion("A".repeat(501)); // Muy largo
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("Debe validar categoría con todos los campos opcionales nulos")
    void testCamposOpcionalesNulos() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Electrónica");
        categoria.setDescripcion(null);
        categoria.setActiva(true);

        // When
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);

        // Then
        assertTrue(violations.isEmpty());
    }
}