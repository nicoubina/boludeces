package ar.edu.huergo.fastbid.repository.subasta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;

@DataJpaTest
@DisplayName("Tests de Integración - SubastaRepository")
class SubastaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubastaRepository subastaRepository;

    private Producto producto1;
    private Producto producto2;
    private Producto producto3;
    private Categoria categoria;
    private Usuario vendedor;

    private Subasta subasta1;
    private Subasta subasta2;
    private Subasta subasta3;

    private Producto buildProductoValido(String nombre, double precioInicial) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion("Herramienta profesional");
        producto.setPrecioInicial(precioInicial);
        producto.setCategoria(categoria);
        producto.setEstado("ACTIVO");
        producto.setImagenes(List.of("http://example.com/imagen1.jpg"));
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setFechaFin(LocalDateTime.now().plusDays(7));
        producto.setUsuario(vendedor);
        producto.setSubasta(null);
        producto.setPrecioCompraInmediata(250_000.0);
        producto.setCondicion("NUEVO");
        producto.setUbicacion("Buenos Aires");
        producto.setCantidad(3);
        return producto;
    }

    private Subasta buildSubastaValida(Producto producto, SubastaEstado estado, double precioInicial,
            double incrementoMinimo) {
        Subasta subasta = new Subasta();
        subasta.setProducto(producto);
        subasta.setFechaInicio(LocalDateTime.now().plusHours(1));
        subasta.setFechaFin(LocalDateTime.now().plusHours(1).plusDays(5));
        subasta.setPrecioInicial(precioInicial);
        subasta.setPrecioActual(precioInicial);
        subasta.setEstado(estado);
        subasta.setGanador(null);
        subasta.setIncrementoMinimo(incrementoMinimo);
        subasta.setCompraInmediata(precioInicial + 100_000.0);
        return subasta;
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
        vendedor = entityManager.persistAndFlush(buildUsuarioValido("vendedor@example.com"));
        producto1 = entityManager.persistAndFlush(buildProductoValido("Taladro Industrial", 120_000.0));
        producto2 = entityManager.persistAndFlush(buildProductoValido("Amoladora Pro", 90_000.0));
        producto3 = entityManager.persistAndFlush(buildProductoValido("Soldadora MIG", 200_000.0));

        subasta1 = entityManager.persistAndFlush(
                buildSubastaValida(producto1, SubastaEstado.ACTIVA, 120_000.0, 5_000.0));
        subasta2 = entityManager.persistAndFlush(
                buildSubastaValida(producto2, SubastaEstado.PROGRAMADA, 90_000.0, 3_000.0));
        subasta3 = entityManager.persistAndFlush(
                buildSubastaValida(producto3, SubastaEstado.PROGRAMADA, 200_000.0, 10_000.0));

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería encontrar todas las subastas")
    void deberiaEncontrarTodasLasSubastas() {
        List<Subasta> subastas = subastaRepository.findAll();

        assertNotNull(subastas);
        assertEquals(3, subastas.size());

        List<Long> productos = subastas.stream()
                .map(subasta -> subasta.getProducto().getIdProducto()).toList();
        assertTrue(productos.contains(producto1.getIdProducto()));
        assertTrue(productos.contains(producto2.getIdProducto()));
        assertTrue(productos.contains(producto3.getIdProducto()));
    }

    @Test
    @DisplayName("Debería contar las subastas")
    void deberiaContarSubastas() {
        assertEquals(3, subastaRepository.count());

        Producto productoExtra = entityManager.persistAndFlush(buildProductoValido("Compresor", 150_000.0));
        Subasta nueva = buildSubastaValida(productoExtra, SubastaEstado.ACTIVA, 150_000.0, 4_000.0);
        subastaRepository.save(nueva);
        entityManager.flush();

        assertEquals(4, subastaRepository.count());
    }

    @Test
    @DisplayName("Debería guardar y recuperar una subasta")
    void deberiaGuardarYRecuperarSubasta() {
        Producto productoExtra = entityManager.persistAndFlush(buildProductoValido("Generador", 300_000.0));
        Subasta subasta = buildSubastaValida(productoExtra, SubastaEstado.ACTIVA, 300_000.0, 12_000.0);

        Subasta guardada = subastaRepository.save(subasta);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(guardada.getIdSubasta());

        Optional<Subasta> recuperada = subastaRepository.findById(guardada.getIdSubasta());
        assertTrue(recuperada.isPresent());
        assertEquals(SubastaEstado.ACTIVA, recuperada.get().getEstado());
        assertEquals(300_000.0, recuperada.get().getPrecioInicial());
    }

    @Test
    @DisplayName("Debería actualizar una subasta existente")
    void deberiaActualizarSubastaExistente() {
        Long id = subasta1.getIdSubasta();

        Subasta subasta = subastaRepository.findById(id).orElseThrow();
        subasta.setPrecioActual(subasta.getPrecioActual() + subasta.getIncrementoMinimo());
        subasta.setEstado(SubastaEstado.FINALIZADA);

        Subasta actualizada = subastaRepository.save(subasta);
        entityManager.flush();
        entityManager.clear();

        Subasta verificada = subastaRepository.findById(id).orElseThrow();
        assertEquals(SubastaEstado.FINALIZADA, actualizada.getEstado());
        assertEquals(actualizada.getPrecioActual(), verificada.getPrecioActual());
    }

    @Test
    @DisplayName("Debería eliminar una subasta")
    void deberiaEliminarSubasta() {
        Long id = subasta2.getIdSubasta();
        assertTrue(subastaRepository.existsById(id));

        subastaRepository.deleteById(id);
        entityManager.flush();

        assertFalse(subastaRepository.existsById(id));
        assertTrue(subastaRepository.findById(id).isEmpty());
    }

    @Test
    @DisplayName("Debería validar restricciones al persistir")
    void deberiaValidarRestriccionesAlPersistir() {
        Subasta invalida = new Subasta();
        invalida.setProducto(null);
        invalida.setFechaInicio(null);
        invalida.setFechaFin(LocalDateTime.now().minusDays(1));
        invalida.setPrecioInicial(0.0);
        invalida.setPrecioActual(-10.0);
        invalida.setEstado(null);
        invalida.setGanador(null);
        invalida.setIncrementoMinimo(0.0);
        invalida.setCompraInmediata(-1_000.0);

        assertThrows(Exception.class, () -> entityManager.persistAndFlush(invalida));
    }
}

