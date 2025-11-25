package ar.edu.huergo.fastbid.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.historial.Historial;
import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;
import ar.edu.huergo.fastbid.repository.historial.HistorialRepository;

@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    @Mock
    private HistorialRepository historialRepository;

    @InjectMocks
    private HistorialService historialService;

    private Historial historial;

    @BeforeEach
    void setUp() {
        historial = historial();
    }

    @Test
    void obtenerHistoriales_devuelveTodosLosRegistros() {
        when(historialRepository.findAll()).thenReturn(List.of(historial));

        List<Historial> resultado = historialService.obtenerHistoriales();

        assertThat(resultado).containsExactly(historial);
        verify(historialRepository).findAll();
    }

    @Test
    void obtenerHistorialPorId_devuelveHistorialSiExiste() {
        when(historialRepository.findById(1L)).thenReturn(Optional.of(historial));

        Historial resultado = historialService.obtenerHistorialPorId(1L);

        assertThat(resultado).isEqualTo(historial);
        verify(historialRepository).findById(1L);
    }

    @Test
    void obtenerHistorialPorId_devuelveNullSiNoExiste() {
        when(historialRepository.findById(2L)).thenReturn(Optional.empty());

        Historial resultado = historialService.obtenerHistorialPorId(2L);

        assertThat(resultado).isNull();
        verify(historialRepository).findById(2L);
    }

    @Test
    void crearHistorial_persisteElHistorial() {
        when(historialRepository.save(any(Historial.class))).thenReturn(historial);

        Historial resultado = historialService.crearHistorial(historial);

        assertThat(resultado).isEqualTo(historial);
        verify(historialRepository).save(historial);
    }

    @Test
    void registrarEvento_creaHistorialConDatos() {
        when(historialRepository.save(any(Historial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Historial resultado = historialService.registrarEvento(historial.getSubasta(), historial.getProducto(),
                historial.getUsuario(), HistorialTipoEvento.PUJA_NUEVA, "Evento de prueba");

        assertThat(resultado.getDescripcion()).isEqualTo("Evento de prueba");
        assertThat(resultado.getSubasta()).isEqualTo(historial.getSubasta());
        assertThat(resultado.getFechaHora()).isNotNull();
        verify(historialRepository).save(any(Historial.class));
    }

    @Test
    void actualizarHistorial_actualizaCamposCuandoExiste() {
        Historial cambios = historial();
        cambios.setDescripcion("Descripción actualizada");
        cambios.setTipoEvento(HistorialTipoEvento.ESTADO_CAMBIADO);
        cambios.setFechaHora(Instant.now());

        when(historialRepository.findById(eq(1L))).thenReturn(Optional.of(historial));
        when(historialRepository.save(any(Historial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Historial resultado = historialService.actualizarHistorial(1L, cambios);

        assertThat(resultado.getDescripcion()).isEqualTo("Descripción actualizada");
        assertThat(resultado.getTipoEvento()).isEqualTo(HistorialTipoEvento.ESTADO_CAMBIADO);
        verify(historialRepository).save(historial);
    }

    @Test
    void actualizarHistorial_devuelveNullCuandoNoExiste() {
        when(historialRepository.findById(3L)).thenReturn(Optional.empty());

        Historial resultado = historialService.actualizarHistorial(3L, historial);

        assertThat(resultado).isNull();
    }

    @Test
    void eliminarHistorial_eliminaPorId() {
        doNothing().when(historialRepository).deleteById(1L);

        historialService.eliminarHistorial(1L);

        verify(historialRepository).deleteById(1L);
    }

    @Test
    void obtenerHistorialPorSubasta_devuelveListaOrdenada() {
        Subasta subasta = historial.getSubasta();
        when(historialRepository.findBySubastaOrderByFechaHoraDesc(subasta)).thenReturn(List.of(historial));

        List<Historial> resultado = historialService.obtenerHistorialPorSubasta(subasta);

        assertThat(resultado).containsExactly(historial);
        verify(historialRepository).findBySubastaOrderByFechaHoraDesc(subasta);
    }

    @Test
    void obtenerHistorialPorIdSubasta_devuelveListaOrdenada() {
        when(historialRepository.findBySubastaIdSubastaOrderByFechaHoraDesc(1L)).thenReturn(List.of(historial));

        List<Historial> resultado = historialService.obtenerHistorialPorIdSubasta(1L);

        assertThat(resultado).containsExactly(historial);
        verify(historialRepository).findBySubastaIdSubastaOrderByFechaHoraDesc(1L);
    }

    @Test
    void obtenerHistorialPorUsuario_combineListasSinDuplicados() {
        Usuario propietario = usuario("propietario@example.com");
        propietario.setId(10L);

        Historial historialPropietario = historial();
        historialPropietario.setIdHistorial(1L);
        historialPropietario.setUsuario(propietario);
        historialPropietario.setFechaHora(Instant.parse("2024-01-01T10:00:00Z"));
        historialPropietario.getProducto().setUsuario(propietario);
        historialPropietario.getSubasta().setProducto(historialPropietario.getProducto());

        Historial historialActor = historial();
        historialActor.setIdHistorial(2L);
        historialActor.setUsuario(propietario);
        historialActor.setFechaHora(Instant.parse("2024-02-01T10:00:00Z"));
        historialActor.setProducto(historialPropietario.getProducto());
        historialActor.setSubasta(historialPropietario.getSubasta());

        when(historialRepository.findBySubastaProductoUsuarioOrderByFechaHoraDesc(propietario))
                .thenReturn(List.of(historialPropietario));
        when(historialRepository.findByUsuarioOrderByFechaHoraDesc(propietario))
                .thenReturn(List.of(historialActor));

        List<Historial> resultado = historialService.obtenerHistorialPorUsuario(propietario);

        assertThat(resultado).extracting(Historial::getIdHistorial).containsExactly(2L, 1L);
        verify(historialRepository).findBySubastaProductoUsuarioOrderByFechaHoraDesc(propietario);
        verify(historialRepository).findByUsuarioOrderByFechaHoraDesc(propietario);
    }

    private Historial historial() {
        Historial historial = new Historial();
        historial.setIdHistorial(1L);
        historial.setSubasta(subasta());
        historial.setProducto(producto());
        historial.setUsuario(usuario("postor@example.com"));
        historial.setTipoEvento(HistorialTipoEvento.PUJA_NUEVA);
        historial.setDescripcion("Nueva puja registrada");
        historial.setFechaHora(Instant.now());
        return historial;
    }

    private Categoria categoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        categoria.setActiva(true);
        return categoria;
    }

    private Usuario usuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Producto producto() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Taladro");
        producto.setDescripcion("Taladro profesional");
        producto.setPrecioInicial(100.0);
        producto.setCategoria(categoria());
        producto.setEstado("ACTIVO");
        producto.setImagenes(List.of("http://example.com/imagen1.jpg"));
        producto.setFechaPublicacion(LocalDateTime.now().minusDays(1));
        producto.setFechaFin(LocalDateTime.now().plusDays(7));
        producto.setUsuario(usuario("vendedor@example.com"));
        producto.setSubasta(null);
        producto.setPrecioCompraInmediata(200.0);
        producto.setCondicion("NUEVO");
        producto.setUbicacion("Buenos Aires");
        producto.setCantidad(1);
        return producto;
    }

    private Subasta subasta() {
        Subasta subasta = new Subasta();
        subasta.setIdSubasta(1L);
        subasta.setProducto(producto());
        subasta.setFechaInicio(LocalDateTime.now().minusHours(2));
        subasta.setFechaFin(LocalDateTime.now().plusDays(3));
        subasta.setPrecioInicial(100.0);
        subasta.setPrecioActual(150.0);
        subasta.setEstado(SubastaEstado.ACTIVA);
        subasta.setGanador(usuario("ganador@example.com"));
        subasta.setIncrementoMinimo(10.0);
        subasta.setCompraInmediata(500.0);
        return subasta;
    }
}
