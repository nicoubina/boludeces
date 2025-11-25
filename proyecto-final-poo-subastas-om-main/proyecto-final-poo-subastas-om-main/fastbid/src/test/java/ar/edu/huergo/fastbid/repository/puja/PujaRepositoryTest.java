package ar.edu.huergo.fastbid.repository.puja;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Puja;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;

@DataJpaTest
@DisplayName("Tests de Integración - PujaRepository")
class PujaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PujaRepository pujaRepository;

    private Producto producto;
    private Subasta subasta;
    private Usuario usuario1;
    private Usuario usuario2;
    private Categoria categoria;
    private Usuario vendedor;

    private Puja puja1;
    private Puja puja2;
    private Puja puja3;

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

    private Subasta buildSubastaValida(Producto producto, double precioInicial, double incrementoMinimo) {
        Subasta subasta = new Subasta();
        subasta.setProducto(producto);
        subasta.setFechaInicio(LocalDateTime.now());
        subasta.setFechaFin(LocalDateTime.now().plusDays(2));
        subasta.setPrecioInicial(precioInicial);
        subasta.setPrecioActual(precioInicial);
        subasta.setEstado(SubastaEstado.ACTIVA);
        subasta.setGanador(null);
        subasta.setIncrementoMinimo(incrementoMinimo);
        subasta.setCompraInmediata(precioInicial + 100_000.0);
        return subasta;
    }

    private Usuario buildUsuarioValido(String email) {
        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Puja buildPujaValida(Subasta subasta, Usuario usuario, double monto, LocalDateTime fechaHora) {
        Puja puja = new Puja();
        puja.setSubasta(subasta);
        puja.setUsuario(usuario);
        puja.setMonto(monto);
        puja.setFechaHora(fechaHora);
        return puja;
    }

    private Categoria buildCategoriaValida() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        categoria.setActiva(true);
        return categoria;
    }

    @BeforeEach
    void setUp() {
        categoria = entityManager.persistAndFlush(buildCategoriaValida());
        vendedor = entityManager.persistAndFlush(buildUsuarioValido("vendedor@example.com"));
        producto = entityManager.persistAndFlush(buildProductoValido("Taladro Industrial", 120_000.0));
        subasta = entityManager.persistAndFlush(buildSubastaValida(producto, 120_000.0, 5_000.0));
        usuario1 = entityManager.persistAndFlush(buildUsuarioValido("postor1@example.com"));
        usuario2 = entityManager.persistAndFlush(buildUsuarioValido("postor2@example.com"));

        puja1 = entityManager.persistAndFlush(
                buildPujaValida(subasta, usuario1, 125_000.0, LocalDateTime.now()));
        puja2 = entityManager.persistAndFlush(
                buildPujaValida(subasta, usuario2, 130_000.0, LocalDateTime.now().plusMinutes(5)));
        puja3 = entityManager.persistAndFlush(
                buildPujaValida(subasta, usuario1, 135_000.0, LocalDateTime.now().plusMinutes(10)));

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería encontrar todas las pujas")
    void deberiaEncontrarTodasLasPujas() {
        List<Puja> pujas = pujaRepository.findAll();

        assertNotNull(pujas);
        assertEquals(3, pujas.size());

        List<Double> montos = pujas.stream().map(Puja::getMonto).toList();
        assertTrue(montos.contains(125_000.0));
        assertTrue(montos.contains(130_000.0));
        assertTrue(montos.contains(135_000.0));
    }

    @Test
    @DisplayName("Debería contar las pujas")
    void deberiaContarPujas() {
        assertEquals(3, pujaRepository.count());

        Puja nueva = buildPujaValida(subasta, usuario2, 140_000.0,
                LocalDateTime.now().plusMinutes(15));
        pujaRepository.save(nueva);
        entityManager.flush();

        assertEquals(4, pujaRepository.count());
    }

    @Test
    @DisplayName("Debería guardar y recuperar una puja")
    void deberiaGuardarYRecuperarPuja() {
        Puja puja = buildPujaValida(subasta, usuario1, 145_000.0,
                LocalDateTime.now().plusMinutes(20));

        Puja guardada = pujaRepository.save(puja);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(guardada.getIdPuja());

        Optional<Puja> recuperada = pujaRepository.findById(guardada.getIdPuja());
        assertTrue(recuperada.isPresent());
        assertEquals(145_000.0, recuperada.get().getMonto());
        assertEquals(usuario1.getId(), recuperada.get().getUsuario().getId());
    }

    @Test
    @DisplayName("Debería actualizar una puja existente")
    void deberiaActualizarPujaExistente() {
        Long id = puja1.getIdPuja();

        Puja puja = pujaRepository.findById(id).orElseThrow();
        puja.setMonto(140_000.0);
        puja.setFechaHora(puja.getFechaHora().plusMinutes(2));

        Puja actualizada = pujaRepository.save(puja);
        entityManager.flush();
        entityManager.clear();

        Puja verificada = pujaRepository.findById(id).orElseThrow();
        assertEquals(140_000.0, actualizada.getMonto());
        assertEquals(verificada.getFechaHora(), actualizada.getFechaHora());
    }

    @Test
    @DisplayName("Debería eliminar una puja")
    void deberiaEliminarPuja() {
        Long id = puja2.getIdPuja();
        assertTrue(pujaRepository.existsById(id));

        pujaRepository.deleteById(id);
        entityManager.flush();

        assertFalse(pujaRepository.existsById(id));
        assertTrue(pujaRepository.findById(id).isEmpty());
    }

    @Test
    @DisplayName("Debería validar restricciones al persistir")
    void deberiaValidarRestriccionesAlPersistir() {
        Puja invalida = new Puja();
        invalida.setSubasta(null);
        invalida.setUsuario(null);
        invalida.setMonto(0.0);
        invalida.setFechaHora(null);

        assertThrows(Exception.class, () -> entityManager.persistAndFlush(invalida));
    }
}
