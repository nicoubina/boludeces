package ar.edu.huergo.fastbid.entity.historial;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Historial")
class HistorialValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Categoria categoriaValida() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        categoria.setActiva(true);
        return categoria;
    }

    private Usuario usuarioValido(String email) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Producto productoValido() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Taladro");
        producto.setDescripcion("Taladro profesional");
        producto.setPrecioInicial(100.0);
        producto.setCategoria(categoriaValida());
        producto.setEstado("ACTIVO");
        producto.setImagenes(List.of("http://example.com/imagen1.jpg"));
        producto.setFechaPublicacion(LocalDateTime.now().minusDays(1));
        producto.setFechaFin(LocalDateTime.now().plusDays(7));
        producto.setUsuario(usuarioValido("vendedor@example.com"));
        producto.setSubasta(null);
        producto.setPrecioCompraInmediata(200.0);
        producto.setCondicion("NUEVO");
        producto.setUbicacion("Buenos Aires");
        producto.setCantidad(1);
        return producto;
    }

    private Subasta subastaValida() {
        Subasta subasta = new Subasta();
        subasta.setIdSubasta(1L);
        subasta.setProducto(productoValido());
        subasta.setFechaInicio(LocalDateTime.now().minusHours(2));
        subasta.setFechaFin(LocalDateTime.now().plusDays(3));
        subasta.setPrecioInicial(100.0);
        subasta.setPrecioActual(150.0);
        subasta.setEstado(SubastaEstado.ACTIVA);
        subasta.setGanador(usuarioValido("ganador@example.com"));
        subasta.setIncrementoMinimo(10.0);
        subasta.setCompraInmediata(500.0);
        return subasta;
    }

    private Historial historialValido() {
        Historial historial = new Historial();
        historial.setIdHistorial(1L);
        historial.setSubasta(subastaValida());
        historial.setProducto(productoValido());
        historial.setUsuario(usuarioValido("postor@example.com"));
        historial.setTipoEvento(HistorialTipoEvento.PUJA_NUEVA);
        historial.setDescripcion("Nueva puja registrada");
        historial.setFechaHora(Instant.now());
        return historial;
    }

    @Test
    @DisplayName("Debería validar historial correcto sin errores")
    void deberiaValidarHistorialCorrectoSinErrores() {
        Historial historial = historialValido();

        Set<ConstraintViolation<Historial>> violaciones = validator.validate(historial);

        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con tipo de evento null")
    void deberiaFallarValidacionConTipoEventoNull() {
        Historial historial = historialValido();
        historial.setTipoEvento(null);

        Set<ConstraintViolation<Historial>> violaciones = validator.validate(historial);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("tipoEvento")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción vacía")
    void deberiaFallarValidacionConDescripcionVacia() {
        Historial historial = historialValido();
        historial.setDescripcion("   ");

        Set<ConstraintViolation<Historial>> violaciones = validator.validate(historial);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("descripcion")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción muy larga")
    void deberiaFallarValidacionConDescripcionMuyLarga() {
        Historial historial = historialValido();
        historial.setDescripcion("A".repeat(256));

        Set<ConstraintViolation<Historial>> violaciones = validator.validate(historial);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("descripcion")));
    }

    @Test
    @DisplayName("Debería fallar validación con fecha hora null")
    void deberiaFallarValidacionConFechaHoraNull() {
        Historial historial = historialValido();
        historial.setFechaHora(null);

        Set<ConstraintViolation<Historial>> violaciones = validator.validate(historial);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fechaHora")));
    }

    @Test
    @DisplayName("Debería permitir producto y usuario opcionales")
    void deberiaPermitirCamposOpcionales() {
        Historial historial = historialValido();
        historial.setProducto(null);
        historial.setUsuario(null);

        Set<ConstraintViolation<Historial>> violaciones = validator.validate(historial);

        assertTrue(violaciones.isEmpty());
    }
}
