package ar.edu.huergo.fastbid.repository.producto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayName("Tests de Integración - ProductoRepository")
class ProductoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductoRepository productoRepository;

    private Producto producto1;
    private Producto producto2;
    private Producto producto3;
    private Categoria categoria;
    private Usuario usuario;

    // --------- Helper: armar un Producto válido ----------
    private Producto buildProductoValido(
            String nombre, String descripcion, double precioInicial, String estado) {

        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecioInicial(precioInicial);
        p.setCategoria(categoria);
        p.setEstado(estado);
        p.setImagenes(Arrays.asList(
                "http://example.com/imagen1.jpg",
                "http://example.com/imagen2.jpg"));
        p.setFechaPublicacion(LocalDateTime.now());
        p.setFechaFin(LocalDateTime.now().plusDays(7));
        p.setUsuario(usuario);
        p.setSubasta(null);
        p.setPrecioCompraInmediata(999.99);
        p.setCondicion("NUEVO");
        p.setUbicacion("Buenos Aires, Argentina");
        p.setCantidad(5);
        return p;
    }

    private Categoria buildCategoriaValida() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        categoria.setActiva(true);
        return categoria;
    }

    private Usuario buildUsuarioValido(String email) {
        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    @BeforeEach
    void setUp() {
        categoria = entityManager.persistAndFlush(buildCategoriaValida());
        usuario = entityManager.persistAndFlush(buildUsuarioValido("usuario@example.com"));
        producto1 = entityManager.persistAndFlush(
                buildProductoValido("Taladro Makita", "Taladro percutor 13mm", 120_000.0, "ACTIVO"));
        producto2 = entityManager.persistAndFlush(
                buildProductoValido("Taladro Black & Decker", "Uso hogareño", 80_000.0, "ACTIVO"));
        producto3 = entityManager.persistAndFlush(
                buildProductoValido("Llave Francesa", "Acero forjado", 15_000.0, "PAUSADO"));
        entityManager.clear();
    }

    @Test
    @DisplayName("Debería encontrar todos los productos")
    void deberiaEncontrarTodosLosProductos() {
        List<Producto> todos = productoRepository.findAll();

        assertNotNull(todos);
        assertEquals(3, todos.size());

        List<String> nombres = todos.stream().map(Producto::getNombre).toList();
        assertTrue(nombres.contains("Taladro Makita"));
        assertTrue(nombres.contains("Taladro Black & Decker"));
        assertTrue(nombres.contains("Llave Francesa"));
    }

    @Test
    @DisplayName("Debería contar productos correctamente")
    void deberiaContarProductos() {
        assertEquals(3, productoRepository.count());

        Producto nuevo = buildProductoValido("Martillo", "Mango de fibra", 9_500.0, "ACTIVO");
        entityManager.persistAndFlush(nuevo);

        assertEquals(4, productoRepository.count());
    }

    @Test
    @DisplayName("Debería guardar y recuperar un producto")
    void deberiaGuardarYRecuperarProducto() {
        Producto nuevo = buildProductoValido("Amoladora", "850W 115mm", 65_000.0, "ACTIVO");

        Producto guardado = productoRepository.save(nuevo);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(guardado.getIdProducto());

        Optional<Producto> recuperado = productoRepository.findById(guardado.getIdProducto());
        assertTrue(recuperado.isPresent());
        assertEquals("Amoladora", recuperado.get().getNombre());
        assertEquals(65_000.0, recuperado.get().getPrecioInicial());
    }

    @Test
    @DisplayName("Debería actualizar un producto existente")
    void deberiaActualizarProductoExistente() {
        Long id = producto1.getIdProducto();

        Producto p = productoRepository.findById(id).orElseThrow();
        p.setNombre("Taladro Makita 13mm");
        p.setPrecioInicial(125_000.0);

        Producto actualizado = productoRepository.save(p);
        entityManager.flush();
        entityManager.clear();

        Producto verificado = productoRepository.findById(id).orElseThrow();
        assertEquals("Taladro Makita 13mm", actualizado.getNombre());
        assertEquals(125_000.0, verificado.getPrecioInicial());
    }

    @Test
    @DisplayName("Debería eliminar un producto")
    void deberiaEliminarProducto() {
        Long id = producto2.getIdProducto();
        assertTrue(productoRepository.existsById(id));

        productoRepository.deleteById(id);
        entityManager.flush();

        assertFalse(productoRepository.existsById(id));
        assertTrue(productoRepository.findById(id).isEmpty());
    }

    @Test
    @DisplayName("Debería fallar al persistir producto inválido (Bean Validation)")
    void deberiaValidarRestriccionesAlPersistir() {
        // nombre vacío, precio <= 0, cantidad < 1, usuario/categoría nulos, sin imágenes
        Producto invalido = new Producto();
        invalido.setNombre("");
        invalido.setDescripcion("x".repeat(300));     // si tenés @Size(max=255)
        invalido.setPrecioInicial(0.0);
        invalido.setCategoria(null);
        invalido.setEstado("ACTIVO");
        invalido.setImagenes(null);
        invalido.setFechaPublicacion(LocalDateTime.now());
        invalido.setFechaFin(LocalDateTime.now().plusDays(7));
        invalido.setUsuario(null);
        invalido.setSubasta(null);
        invalido.setPrecioCompraInmediata(-10.0);
        invalido.setCondicion("USADO");
        invalido.setUbicacion("CABA");
        invalido.setCantidad(0);

        // En JPA con Bean Validation, la violación ocurre al flush
        assertThrows(Exception.class, () -> entityManager.persistAndFlush(invalido));
    }
}
