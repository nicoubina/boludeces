package ar.edu.huergo.lcarera.restaurante.entity.plato;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Ingrediente")
class IngredienteValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar ingrediente correcto sin errores")
    void deberiaValidarIngredienteCorrectoSinErrores() {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre("Tomate");

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para un ingrediente válido");
    }

    @Test
    @DisplayName("Debería fallar validación con nombre null")
    void deberiaFallarValidacionConNombreNull() {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(null);

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre vacío")
    void deberiaFallarValidacionConNombreVacio() {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre("");

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre solo espacios")
    void deberiaFallarValidacionConNombreSoloEspacios() {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre("   ");

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B"})
    @DisplayName("Debería fallar validación con nombres muy cortos")
    void deberiaFallarValidacionConNombresMuyCortos(String nombreCorto) {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(nombreCorto);

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 2 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre muy largo")
    void deberiaFallarValidacionConNombreMuyLargo() {
        // Given
        String nombreLargo = "A".repeat(101); // 101 caracteres
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(nombreLargo);

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 2 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar nombres en el límite válido")
    void deberiaAceptarNombresEnLimiteValido() {
        // Given - Nombres de exactamente 2 y 100 caracteres
        String nombreMinimo = "AB"; // 2 caracteres
        String nombreMaximo = "A".repeat(100); // 100 caracteres

        Ingrediente ingrediente1 = new Ingrediente();
        ingrediente1.setNombre(nombreMinimo);

        Ingrediente ingrediente2 = new Ingrediente();
        ingrediente2.setNombre(nombreMaximo);

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones1 = validator.validate(ingrediente1);
        Set<ConstraintViolation<Ingrediente>> violaciones2 = validator.validate(ingrediente2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Tomate", "Queso Mozzarella", "Aceite de Oliva Extra Virgen",
            "Sal Marina", "Pimienta Negra Molida"})
    @DisplayName("Debería aceptar nombres de ingredientes comunes")
    void deberiaAceptarNombresDeIngredientesComunes(String nombreValido) {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(nombreValido);

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertTrue(violaciones.isEmpty(), "El nombre '" + nombreValido + "' debería ser válido");
    }

    @Test
    @DisplayName("Debería aceptar nombres con caracteres especiales válidos")
    void deberiaAceptarNombresConCaracteresEspeciales() {
        // Given - Nombres con acentos, espacios, guiones
        String[] nombresEspeciales = {"Aceite de Oliva", "Queso Parmesano", "Pimienta Negra",
                "Sal de Mar", "Tomate Cherry"};

        for (String nombre : nombresEspeciales) {
            Ingrediente ingrediente = new Ingrediente();
            ingrediente.setNombre(nombre);

            // When
            Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

            // Then
            assertTrue(violaciones.isEmpty(), "El nombre '" + nombre + "' debería ser válido");
        }
    }

    @Test
    @DisplayName("Debería manejar nombres con números")
    void deberiaManejarNombresConNumeros() {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre("Queso Tipo 1");

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertTrue(violaciones.isEmpty(), "Los nombres con números deberían ser válidos");
    }

    @Test
    @DisplayName("Debería validar ingrediente con constructor completo")
    void deberiaValidarIngredienteConConstructorCompleto() {
        // Given
        Ingrediente ingrediente = new Ingrediente(1L, "Tomate Cherry", null);

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería validar ingrediente creado con constructor de nombre")
    void deberiaValidarIngredienteConConstructorDeNombre() {
        // Given
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setId(1L);
        ingrediente.setNombre("Albahaca Fresca");

        // When
        Set<ConstraintViolation<Ingrediente>> violaciones = validator.validate(ingrediente);

        // Then
        assertTrue(violaciones.isEmpty());
    }
}
