package ar.edu.huergo.fastbid.entity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ar.edu.huergo.fastbid.entity.security.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Producto")
class ProductoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // =========================
    // Helper para armar Producto válido y mutar campos en cada test
    // =========================
    private Categoria categoriaValida() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        return categoria;
    }

    private Usuario usuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario@example.com");
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Producto productoValido() {
        Producto p = new Producto();
        p.setNombre("Caja de Herramientas");
        p.setDescripcion("Caja de herramientas completa con 100 piezas.");
        p.setPrecioInicial(49.99);
        p.setCategoria(categoriaValida());
        p.setEstado("ACTIVO");
        p.setImagenes(Arrays.asList("http://example.com/imagen1.jpg", "http://example.com/imagen2.jpg"));
        p.setFechaPublicacion(LocalDateTime.now());
        p.setFechaFin(LocalDateTime.now().plusDays(7));
        p.setUsuario(usuarioValido());
        p.setSubasta(null);
        p.setPrecioCompraInmediata(99.99);
        p.setCondicion("NUEVO");
        p.setUbicacion("Buenos Aires, Argentina");
        p.setCantidad(10);
        return p;
    }

    @Test
    @DisplayName("Debería validar producto correcto sin errores")
    void deberiaValidarProductoCorrectoSinErrores() {
        // Given
        Producto producto = productoValido();

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para un producto válido");
    }

    @Test
    @DisplayName("Debería fallar validación con nombre null")
    void deberiaFallarValidacionConNombreNull() {
        // Given
        Producto producto = productoValido();
        producto.setNombre(null);

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre vacío")
    void deberiaFallarValidacionConNombreVacio() {
        // Given
        Producto producto = productoValido();
        producto.setNombre("");

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre solo espacios")
    void deberiaFallarValidacionConNombreSoloEspacios() {
        // Given
        Producto producto = productoValido();
        producto.setNombre("   ");

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A"})
    @DisplayName("Debería fallar validación con nombres muy cortos")
    void deberiaFallarValidacionConNombresMuyCortos(String nombreCorto) {
        // Given
        Producto producto = productoValido();
        producto.setNombre(nombreCorto);

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("entre 2 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre muy largo")
    void deberiaFallarValidacionConNombreMuyLargo() {
        // Given
        String nombreLargo = "A".repeat(101); // 101 caracteres
        Producto producto = productoValido();
        producto.setNombre(nombreLargo);

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("entre 2 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar nombres en el límite válido")
    void deberiaAceptarNombresEnLimiteValido() {
        // Given - Nombres de exactamente 2 y 100 caracteres
        Producto producto1 = productoValido();
        producto1.setNombre("AB"); // 2 caracteres

        Producto producto2 = productoValido();
        producto2.setNombre("A".repeat(100)); // 100 caracteres

        // When
        Set<ConstraintViolation<Producto>> violaciones1 = validator.validate(producto1);
        Set<ConstraintViolation<Producto>> violaciones2 = validator.validate(producto2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @Test
    @DisplayName("Debería aceptar descripción null o vacía")
    void deberiaAceptarDescripcionNullOVacia() {
        // Given
        Producto producto1 = productoValido();
        producto1.setDescripcion(null);

        Producto producto2 = productoValido();
        producto2.setDescripcion("");

        // When
        Set<ConstraintViolation<Producto>> violaciones1 = validator.validate(producto1);
        Set<ConstraintViolation<Producto>> violaciones2 = validator.validate(producto2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -0.01, 0.0})
    @DisplayName("Debería fallar validación con precios no positivos (precioInicial)")
    void deberiaFallarValidacionConPreciosNoPositivos(double precioInvalido) {
        // Given
        Producto producto = productoValido();
        producto.setPrecioInicial(precioInvalido);

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("precioInicial")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("mayor a 0")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 1.0, 100.0, 999.99})
    @DisplayName("Debería aceptar precios positivos (precioInicial)")
    void deberiaAceptarPreciosPositivos(double precioValido) {
        // Given
        Producto producto = productoValido();
        producto.setPrecioInicial(precioValido);

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con imágenes null")
    void deberiaFallarValidacionConImagenesNull() {
        // Given
        Producto producto = productoValido();
        producto.setImagenes(null);

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("imagenes")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().toLowerCase().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con lista de imágenes vacía")
    void deberiaFallarValidacionConListaImagenesVacia() {
        // Given
        Producto producto = productoValido();
        producto.setImagenes(Collections.emptyList());

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("imagenes")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().toLowerCase().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería validar múltiples errores simultáneamente")
    void deberiaValidarMultiplesErroresSimultaneamente() {
        // Given - Producto con múltiples errores
        Producto invalido = productoValido();
        invalido.setNombre("");                      // Nombre vacío
        invalido.setDescripcion("A".repeat(300));    // Descripción muy larga
        invalido.setPrecioInicial(-5.0);             // Precio negativo
        invalido.setImagenes(Collections.emptyList());// Lista vacía

        // When
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(invalido);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 3); // Al menos 3 errores

        List<String> propiedadesConError = violaciones.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();

        assertTrue(propiedadesConError.contains("nombre"));
        assertTrue(propiedadesConError.contains("descripcion"));
        assertTrue(propiedadesConError.contains("precioInicial"));
        assertTrue(propiedadesConError.contains("imagenes"));
    }
}
