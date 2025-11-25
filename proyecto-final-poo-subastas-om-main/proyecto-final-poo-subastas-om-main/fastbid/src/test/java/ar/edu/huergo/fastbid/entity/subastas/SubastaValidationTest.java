package ar.edu.huergo.fastbid.entity.subastas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Subasta")
class SubastaValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Categoria categoriaValida() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        categoria.setActiva(true);
        return categoria;
    }

    private Usuario usuarioValido(String email) {
        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Producto productoValido() {
        Producto producto = new Producto();
        producto.setNombre("Compresor");
        producto.setDescripcion("Compresor industrial");
        producto.setPrecioInicial(200_000.0);
        producto.setCategoria(categoriaValida());
        producto.setEstado("ACTIVO");
        producto.setImagenes(List.of("http://example.com/imagen1.jpg"));
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setFechaFin(LocalDateTime.now().plusDays(5));
        producto.setUsuario(usuarioValido("vendedor@example.com"));
        producto.setSubasta(null);
        producto.setPrecioCompraInmediata(250_000.0);
        producto.setCondicion("NUEVO");
        producto.setUbicacion("Buenos Aires");
        producto.setCantidad(2);
        return producto;
    }

    private Subasta subastaValida() {
        Subasta subasta = new Subasta();
        subasta.setProducto(productoValido());
        subasta.setFechaInicio(LocalDateTime.now());
        subasta.setFechaFin(LocalDateTime.now().plusDays(3));
        subasta.setPrecioInicial(150_000.0);
        subasta.setPrecioActual(150_000.0);
        subasta.setEstado(SubastaEstado.ACTIVA);
        subasta.setGanador(null);
        subasta.setIncrementoMinimo(5_000.0);
        subasta.setCompraInmediata(250_000.0);
        return subasta;
    }

    @Test
    @DisplayName("Debería validar subasta correcta sin errores")
    void deberiaValidarSubastaCorrecta() {
        Subasta subasta = subastaValida();

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para una subasta válida");
    }

    @Test
    @DisplayName("Debería fallar validación con productoId null")
    void deberiaFallarConProductoIdNull() {
        Subasta subasta = subastaValida();
        subasta.setProducto(null);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("producto")));
    }

    @Test
    @DisplayName("Debería fallar validación con fechaInicio null")
    void deberiaFallarConFechaInicioNull() {
        Subasta subasta = subastaValida();
        subasta.setFechaInicio(null);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fechaInicio")));
    }

    @Test
    @DisplayName("Debería fallar validación con fechaFin en el pasado")
    void deberiaFallarConFechaFinEnElPasado() {
        Subasta subasta = subastaValida();
        subasta.setFechaFin(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fechaFin")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -100.0})
    @DisplayName("Debería fallar validación con precioInicial no positivo")
    void deberiaFallarConPrecioInicialNoPositivo(double valor) {
        Subasta subasta = subastaValida();
        subasta.setPrecioInicial(valor);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("precioInicial")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -50.0})
    @DisplayName("Debería fallar validación con precioActual no positivo")
    void deberiaFallarConPrecioActualNoPositivo(double valor) {
        Subasta subasta = subastaValida();
        subasta.setPrecioActual(valor);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("precioActual")));
    }

    @Test
    @DisplayName("Debería aceptar compra inmediata null")
    void deberiaAceptarCompraInmediataNull() {
        Subasta subasta = subastaValida();
        subasta.setCompraInmediata(null);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertTrue(violaciones.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1_000.0})
    @DisplayName("Debería fallar validación con incrementoMinimo no positivo")
    void deberiaFallarConIncrementoMinimoNoPositivo(double valor) {
        Subasta subasta = subastaValida();
        subasta.setIncrementoMinimo(valor);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("incrementoMinimo")));
    }

    @Test
    @DisplayName("Debería fallar validación con estado null")
    void deberiaFallarConEstadoNull() {
        Subasta subasta = subastaValida();
        subasta.setEstado(null);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("estado")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10.0, -500.0})
    @DisplayName("Debería fallar validación con compraInmediata negativa")
    void deberiaFallarConCompraInmediataNegativa(double valor) {
        Subasta subasta = subastaValida();
        subasta.setCompraInmediata(valor);

        Set<ConstraintViolation<Subasta>> violaciones = validator.validate(subasta);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("compraInmediata")));
    }
}

