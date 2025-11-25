package ar.edu.huergo.fastbid.repository.historial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
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
import ar.edu.huergo.fastbid.entity.historial.Historial;
import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;

@DataJpaTest
@DisplayName("Tests de Integración - HistorialRepository")
class HistorialRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HistorialRepository historialRepository;

    private Categoria categoria;
    private Usuario vendedor;
    private Usuario postor;
    private Subasta subasta;
    private Producto producto;

    private Historial historial1;
    private Historial historial2;
    private Historial historial3;

    @BeforeEach
    void setUp() {
        categoria = entityManager.persistAndFlush(buildCategoriaValida());
        vendedor = entityManager.persistAndFlush(buildUsuarioValido("vendedor@example.com"));
        postor = entityManager.persistAndFlush(buildUsuarioValido("postor@example.com"));
        producto = entityManager.persistAndFlush(buildProductoValido("Taladro", 120_000.0));
        subasta = entityManager.persistAndFlush(buildSubastaValida(producto, vendedor));

        historial1 = entityManager.persistAndFlush(buildHistorial(subasta, producto, vendedor,
                HistorialTipoEvento.CREACION_SUBASTA, "Subasta creada",
                Instant.now().minusSeconds(3600)));
        historial2 = entityManager.persistAndFlush(buildHistorial(subasta, producto, postor,
                HistorialTipoEvento.PUJA_NUEVA, "Nueva puja",
                Instant.now().minusSeconds(1800)));
        historial3 = entityManager.persistAndFlush(buildHistorial(subasta, producto, null,
                HistorialTipoEvento.ESTADO_CAMBIADO, "Estado actualizado",
                Instant.now().minusSeconds(900)));

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería encontrar todos los historiales")
    void deberiaEncontrarTodosLosHistoriales() {
        List<Historial> historiales = historialRepository.findAll();

        assertNotNull(historiales);
        assertEquals(3, historiales.size());
    }

    @Test
    @DisplayName("Debería contar los historiales")
    void deberiaContarHistoriales() {
        assertEquals(3, historialRepository.count());

        Historial nuevo = buildHistorial(subasta, producto, postor,
                HistorialTipoEvento.PUJA_NUEVA, "Nueva puja", Instant.now());
        historialRepository.save(nuevo);
        entityManager.flush();

        assertEquals(4, historialRepository.count());
    }

    @Test
    @DisplayName("Debería guardar y recuperar un historial")
    void deberiaGuardarYRecuperarHistorial() {
        Historial historial = buildHistorial(subasta, producto, postor,
                HistorialTipoEvento.PAGO_APROBADO, "Pago aprobado",
                Instant.now());

        Historial guardado = historialRepository.save(historial);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(guardado.getIdHistorial());

        Optional<Historial> recuperado = historialRepository.findById(guardado.getIdHistorial());
        assertTrue(recuperado.isPresent());
        assertEquals(HistorialTipoEvento.PAGO_APROBADO, recuperado.get().getTipoEvento());
        assertEquals("Pago aprobado", recuperado.get().getDescripcion());
    }

    @Test
    @DisplayName("Debería actualizar un historial existente")
    void deberiaActualizarHistorialExistente() {
        Long id = historial1.getIdHistorial();

        Historial historial = historialRepository.findById(id).orElseThrow();
        historial.setDescripcion("Subasta publicada");
        historial.setTipoEvento(HistorialTipoEvento.ESTADO_CAMBIADO);

        Historial actualizado = historialRepository.save(historial);
        entityManager.flush();
        entityManager.clear();

        Historial verificado = historialRepository.findById(id).orElseThrow();
        assertEquals(HistorialTipoEvento.ESTADO_CAMBIADO, actualizado.getTipoEvento());
        assertEquals(actualizado.getDescripcion(), verificado.getDescripcion());
    }

    @Test
    @DisplayName("Debería eliminar un historial")
    void deberiaEliminarHistorial() {
        Long id = historial2.getIdHistorial();
        assertTrue(historialRepository.existsById(id));

        historialRepository.deleteById(id);
        entityManager.flush();

        assertFalse(historialRepository.existsById(id));
        assertTrue(historialRepository.findById(id).isEmpty());
    }

    @Test
    @DisplayName("Debería obtener historiales ordenados por subasta")
    void deberiaObtenerHistorialesOrdenadosPorSubasta() {
        List<Historial> historiales = historialRepository.findBySubastaOrderByFechaHoraDesc(subasta);

        assertEquals(3, historiales.size());
        assertTrue(historiales.get(0).getFechaHora().isAfter(historiales.get(1).getFechaHora()));
        assertTrue(historiales.get(1).getFechaHora().isAfter(historiales.get(2).getFechaHora()));
    }

    @Test
    @DisplayName("Debería obtener historiales ordenados por id de subasta")
    void deberiaObtenerHistorialesOrdenadosPorIdSubasta() {
        List<Historial> historiales = historialRepository.findBySubastaIdSubastaOrderByFechaHoraDesc(
                subasta.getIdSubasta());

        assertEquals(3, historiales.size());
        assertEquals(historial3.getIdHistorial(), historiales.get(0).getIdHistorial());
    }

    @Test
    @DisplayName("Debería obtener historiales por usuario actor ordenados descendentemente")
    void deberiaObtenerHistorialesPorUsuarioOrdenadosDesc() {
        List<Historial> historiales = historialRepository.findByUsuarioOrderByFechaHoraDesc(postor);

        assertEquals(1, historiales.size());
        assertEquals(historial2.getIdHistorial(), historiales.get(0).getIdHistorial());
    }

    @Test
    @DisplayName("Debería obtener historiales por propietario de subasta ordenados descendentemente")
    void deberiaObtenerHistorialesPorPropietarioOrdenadosDesc() {
        List<Historial> historiales = historialRepository
                .findBySubastaProductoUsuarioOrderByFechaHoraDesc(vendedor);

        assertEquals(3, historiales.size());
        assertEquals(historial3.getIdHistorial(), historiales.get(0).getIdHistorial());
    }

    @Test
    @DisplayName("Debería validar restricciones al persistir")
    void deberiaValidarRestriccionesAlPersistir() {
        Historial invalido = new Historial();
        invalido.setSubasta(null);
        invalido.setProducto(producto);
        invalido.setUsuario(postor);
        invalido.setTipoEvento(null);
        invalido.setDescripcion(" ");
        invalido.setFechaHora(null);

        assertThrows(Exception.class, () -> entityManager.persistAndFlush(invalido));
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

    private Subasta buildSubastaValida(Producto producto, Usuario vendedor) {
        Subasta subasta = new Subasta();
        subasta.setProducto(producto);
        subasta.setFechaInicio(LocalDateTime.now().minusHours(1));
        subasta.setFechaFin(LocalDateTime.now().plusDays(5));
        subasta.setPrecioInicial(producto.getPrecioInicial());
        subasta.setPrecioActual(producto.getPrecioInicial());
        subasta.setEstado(SubastaEstado.ACTIVA);
        subasta.setGanador(null);
        subasta.setIncrementoMinimo(5_000.0);
        subasta.setCompraInmediata(producto.getPrecioInicial() + 100_000.0);
        return subasta;
    }

    private Historial buildHistorial(Subasta subasta, Producto producto, Usuario usuario,
            HistorialTipoEvento tipoEvento, String descripcion, Instant fechaHora) {
        Historial historial = new Historial();
        historial.setSubasta(subasta);
        historial.setProducto(producto);
        historial.setUsuario(usuario);
        historial.setTipoEvento(tipoEvento);
        historial.setDescripcion(descripcion);
        historial.setFechaHora(fechaHora);
        return historial;
    }
}
