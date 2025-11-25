package ar.edu.huergo.fastbid.entity.subastas;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Puja")
class PujaValidationTest {

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
        producto.setNombre("Taladro");
        producto.setDescripcion("Taladro percutor");
        producto.setPrecioInicial(100_000.0);
        producto.setCategoria(categoriaValida());
        producto.setEstado("ACTIVO");
        producto.setImagenes(List.of("http://example.com/imagen1.jpg"));
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setFechaFin(LocalDateTime.now().plusDays(7));
        producto.setUsuario(usuarioValido("vendedor@example.com"));
        producto.setSubasta(null);
        producto.setPrecioCompraInmediata(150_000.0);
        producto.setCondicion("NUEVO");
        producto.setUbicacion("Buenos Aires");
        producto.setCantidad(1);
        return producto;
    }

    private Subasta subastaValida() {
        Subasta subasta = new Subasta();
        subasta.setProducto(productoValido());
        subasta.setFechaInicio(LocalDateTime.now());
        subasta.setFechaFin(LocalDateTime.now().plusDays(3));
        subasta.setPrecioInicial(100_000.0);
        subasta.setPrecioActual(100_000.0);
        subasta.setEstado(SubastaEstado.ACTIVA);
        subasta.setGanador(null);
        subasta.setIncrementoMinimo(5_000.0);
        subasta.setCompraInmediata(180_000.0);
        return subasta;
    }

    private Puja pujaValida() {
        Puja puja = new Puja();
        puja.setSubasta(subastaValida());
        puja.setUsuario(usuarioValido("postor@example.com"));
        puja.setMonto(10_000.0);
        puja.setFechaHora(LocalDateTime.now());
        return puja;
    }

    @Test
    void deberiaValidarPujaCorrecta() {
        Puja puja = pujaValida();

        Set<ConstraintViolation<Puja>> violaciones = validator.validate(puja);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void deberiaFallarConSubastaNull() {
        Puja puja = pujaValida();
        puja.setSubasta(null);

        Set<ConstraintViolation<Puja>> violaciones = validator.validate(puja);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("subasta"));
    }

    @Test
    void deberiaFallarConUsuarioNull() {
        Puja puja = pujaValida();
        puja.setUsuario(null);

        Set<ConstraintViolation<Puja>> violaciones = validator.validate(puja);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("usuario"));
    }

    @Test
    void deberiaFallarConMontoNoPositivo() {
        Puja puja = pujaValida();
        puja.setMonto(0.0);

        Set<ConstraintViolation<Puja>> violaciones = validator.validate(puja);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("monto"));
    }

    @Test
    void deberiaFallarConFechaHoraNull() {
        Puja puja = pujaValida();
        puja.setFechaHora(null);

        Set<ConstraintViolation<Puja>> violaciones = validator.validate(puja);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("fechaHora"));
    }
}
