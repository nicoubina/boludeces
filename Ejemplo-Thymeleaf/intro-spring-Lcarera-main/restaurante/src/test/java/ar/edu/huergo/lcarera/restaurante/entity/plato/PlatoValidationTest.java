package ar.edu.huergo.lcarera.restaurante.entity.plato;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;
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

@DisplayName("Tests de Validación - Entidad Plato")
class PlatoValidationTest {

    private Validator validator;
    private Ingrediente ingredienteEjemplo;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        ingredienteEjemplo = new Ingrediente();
        ingredienteEjemplo.setId(1L);
        ingredienteEjemplo.setNombre("Tomate");
    }

    @Test
    @DisplayName("Debería validar plato correcto sin errores")
    void deberiaValidarPlatoCorrectoSinErrores() {
        // Given
        Plato plato = new Plato();
        plato.setNombre("Pizza Margherita");
        plato.setDescripcion("Pizza clásica italiana");
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para un plato válido");
    }

    @Test
    @DisplayName("Debería fallar validación con nombre null")
    void deberiaFallarValidacionConNombreNull() {
        // Given
        Plato plato = new Plato();
        plato.setNombre(null);
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

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
        Plato plato = new Plato();
        plato.setNombre("");
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre solo espacios")
    void deberiaFallarValidacionConNombreSoloEspacios() {
        // Given
        Plato plato = new Plato();
        plato.setNombre("   ");
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A"})
    @DisplayName("Debería fallar validación con nombres muy cortos")
    void deberiaFallarValidacionConNombresMuyCortos(String nombreCorto) {
        // Given
        Plato plato = new Plato();
        plato.setNombre(nombreCorto);
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

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
        Plato plato = new Plato();
        plato.setNombre(nombreLargo);
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

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

        Plato plato1 = new Plato();
        plato1.setNombre(nombreMinimo);
        plato1.setDescripcion("Descripción válida");
        plato1.setPrecio(15.50);
        plato1.setIngredientes(Arrays.asList(ingredienteEjemplo));

        Plato plato2 = new Plato();
        plato2.setNombre(nombreMaximo);
        plato2.setDescripcion("Descripción válida");
        plato2.setPrecio(15.50);
        plato2.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones1 = validator.validate(plato1);
        Set<ConstraintViolation<Plato>> violaciones2 = validator.validate(plato2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con descripción muy larga")
    void deberiaFallarValidacionConDescripcionMuyLarga() {
        // Given
        String descripcionLarga = "A".repeat(256); // 256 caracteres
        Plato plato = new Plato();
        plato.setNombre("Pizza Válida");
        plato.setDescripcion(descripcionLarga);
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("descripcion")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("no puede superar 255 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar descripción null o vacía")
    void deberiaAceptarDescripcionNullOVacia() {
        // Given
        Plato plato1 = new Plato();
        plato1.setNombre("Pizza Válida");
        plato1.setDescripcion(null);
        plato1.setPrecio(15.50);
        plato1.setIngredientes(Arrays.asList(ingredienteEjemplo));

        Plato plato2 = new Plato();
        plato2.setNombre("Pizza Válida");
        plato2.setDescripcion("");
        plato2.setPrecio(15.50);
        plato2.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones1 = validator.validate(plato1);
        Set<ConstraintViolation<Plato>> violaciones2 = validator.validate(plato2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -0.01, 0.0})
    @DisplayName("Debería fallar validación con precios no positivos")
    void deberiaFallarValidacionConPreciosNoPositivos(double precioInvalido) {
        // Given
        Plato plato = new Plato();
        plato.setNombre("Pizza Válida");
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(precioInvalido);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("precio")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("mayor a 0")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 1.0, 100.0, 999.99})
    @DisplayName("Debería aceptar precios positivos")
    void deberiaAceptarPreciosPositivos(double precioValido) {
        // Given
        Plato plato = new Plato();
        plato.setNombre("Pizza Válida");
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(precioValido);
        plato.setIngredientes(Arrays.asList(ingredienteEjemplo));

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con ingredientes null")
    void deberiaFallarValidacionConIngredientesNull() {
        // Given
        Plato plato = new Plato();
        plato.setNombre("Pizza Válida");
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(15.50);
        plato.setIngredientes(null);

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("ingredientes")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorios")));
    }

    @Test
    @DisplayName("Debería fallar validación con lista de ingredientes vacía")
    void deberiaFallarValidacionConListaIngredientesVacia() {
        // Given
        Plato plato = new Plato();
        plato.setNombre("Pizza Válida");
        plato.setDescripcion("Descripción válida");
        plato.setPrecio(15.50);
        plato.setIngredientes(Arrays.asList()); // Lista vacía

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(plato);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("ingredientes")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorios")));
    }

    @Test
    @DisplayName("Debería validar múltiples errores simultáneamente")
    void deberiaValidarMultiplesErroresSimultaneamente() {
        // Given - Plato con múltiples errores
        Plato platoInvalido = new Plato();
        platoInvalido.setNombre(""); // Nombre vacío
        platoInvalido.setDescripcion("A".repeat(300)); // Descripción muy larga
        platoInvalido.setPrecio(-5.0); // Precio negativo
        platoInvalido.setIngredientes(Arrays.asList()); // Lista vacía

        // When
        Set<ConstraintViolation<Plato>> violaciones = validator.validate(platoInvalido);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 3); // Al menos 3 errores

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("nombre"));
        assertTrue(propiedadesConError.contains("descripcion"));
        assertTrue(propiedadesConError.contains("precio"));
        assertTrue(propiedadesConError.contains("ingredientes"));
    }
}
