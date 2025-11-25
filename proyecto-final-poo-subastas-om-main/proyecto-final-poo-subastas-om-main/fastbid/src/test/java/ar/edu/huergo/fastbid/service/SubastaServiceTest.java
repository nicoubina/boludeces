package ar.edu.huergo.fastbid.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import ar.edu.huergo.fastbid.repository.producto.ProductoRepository;
import ar.edu.huergo.fastbid.repository.subasta.SubastaRepository;
import ar.edu.huergo.fastbid.service.HistorialService;

@ExtendWith(MockitoExtension.class)
class SubastaServiceTest {

    @Mock
    private SubastaRepository subastaRepository;

    @Mock
    private HistorialService historialService;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private SubastaService subastaService;

    private Subasta subasta;

    @BeforeEach
    void setUp() {
        subasta = crearSubasta();
    }

    @Test
    void obtenerSubastas_devuelveTodasLasSubastas() {
        when(subastaRepository.findAll()).thenReturn(List.of(subasta));

        List<Subasta> resultado = subastaService.obtenerSubastas();

        assertThat(resultado).containsExactly(subasta);
        verify(subastaRepository).findAll();
    }

    @Test
    void obtenerSubastaPorId_devuelveSubastaSiExiste() {
        when(subastaRepository.findById(1L)).thenReturn(Optional.of(subasta));

        Subasta resultado = subastaService.obtenerSubastaPorId(1L);

        assertThat(resultado).isEqualTo(subasta);
        verify(subastaRepository).findById(1L);
    }

    @Test
    void obtenerSubastaPorId_devuelveNullSiNoExiste() {
        when(subastaRepository.findById(2L)).thenReturn(Optional.empty());

        Subasta resultado = subastaService.obtenerSubastaPorId(2L);

        assertThat(resultado).isNull();
        verify(subastaRepository).findById(2L);
    }

    @Test
    void crearSubasta_persisteLaSubasta() {
        when(productoRepository.findById(subasta.getProducto().getIdProducto()))
                .thenReturn(Optional.of(subasta.getProducto()));
        when(subastaRepository.save(any(Subasta.class))).thenReturn(subasta);
        when(historialService.registrarEvento(any(Subasta.class), any(Producto.class), any(Usuario.class),
                eq(HistorialTipoEvento.CREACION_SUBASTA), anyString())).thenReturn(new Historial());

        Subasta resultado = subastaService.crearSubasta(subasta);

        assertThat(resultado).isEqualTo(subasta);
        verify(subastaRepository).save(subasta);
        verify(historialService).registrarEvento(any(Subasta.class), any(Producto.class), any(Usuario.class),
                eq(HistorialTipoEvento.CREACION_SUBASTA), anyString());
    }

    @Test
    void actualizarSubasta_actualizaCamposCuandoExiste() {
        Subasta cambios = crearSubasta();
        cambios.setPrecioActual(500.0);
        cambios.setEstado(SubastaEstado.FINALIZADA);
        when(subastaRepository.findById(eq(1L))).thenReturn(Optional.of(subasta));
        when(subastaRepository.save(any(Subasta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subasta resultado = subastaService.actualizarSubasta(1L, cambios);

        assertThat(resultado.getPrecioActual()).isEqualTo(500.0);
        assertThat(resultado.getEstado()).isEqualTo(SubastaEstado.FINALIZADA);
        verify(subastaRepository).save(subasta);
    }

    @Test
    void actualizarSubasta_devuelveNullCuandoNoExiste() {
        when(subastaRepository.findById(3L)).thenReturn(Optional.empty());

        Subasta resultado = subastaService.actualizarSubasta(3L, subasta);

        assertThat(resultado).isNull();
    }

    @Test
    void eliminarSubasta_eliminaPorId() {
        when(subastaRepository.findById(1L)).thenReturn(Optional.of(subasta));

        subastaService.eliminarSubasta(1L);

        verify(subastaRepository).findById(1L);
        verify(subastaRepository).delete(subasta);
    }

    @Test
    void eliminarSubastasDeUsuario_registraHistoriales() {
        Usuario propietario = subasta.getProducto().getUsuario();
        when(subastaRepository.findAllById(List.of(1L))).thenReturn(List.of(subasta));
        when(historialService.registrarEvento(any(Subasta.class), any(Producto.class), any(Usuario.class),
                eq(HistorialTipoEvento.SUBASTA_CANCELADA), anyString())).thenReturn(new Historial());

        int eliminadas = subastaService.eliminarSubastasDeUsuario(List.of(1L), propietario);

        assertThat(eliminadas).isEqualTo(1);
        verify(historialService).registrarEvento(any(Subasta.class), any(Producto.class), eq(propietario),
                eq(HistorialTipoEvento.SUBASTA_CANCELADA), anyString());
        verify(subastaRepository).deleteAll(List.of(subasta));
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
        usuario.setId(5L);
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Producto producto() {
        Producto producto = new Producto();
        producto.setIdProducto(10L);
        producto.setNombre("Taladro");
        producto.setDescripcion("Taladro percutor");
        producto.setPrecioInicial(100.0);
        producto.setCategoria(categoria());
        producto.setEstado("ACTIVO");
        producto.setImagenes(List.of("http://example.com/1.jpg"));
        producto.setFechaPublicacion(LocalDateTime.now().minusDays(2));
        producto.setFechaFin(LocalDateTime.now().plusDays(2));
        producto.setUsuario(usuario("vendedor@example.com"));
        producto.setSubasta(null);
        producto.setPrecioCompraInmediata(200.0);
        producto.setCondicion("NUEVO");
        producto.setUbicacion("Buenos Aires");
        producto.setCantidad(1);
        return producto;
    }

    private Subasta crearSubasta() {
        Subasta nuevaSubasta = new Subasta();
        nuevaSubasta.setIdSubasta(1L);
        nuevaSubasta.setProducto(producto());
        nuevaSubasta.setFechaInicio(LocalDateTime.now().minusDays(1));
        nuevaSubasta.setFechaFin(LocalDateTime.now().plusDays(1));
        nuevaSubasta.setPrecioInicial(100.0);
        nuevaSubasta.setPrecioActual(150.0);
        nuevaSubasta.setEstado(SubastaEstado.ACTIVA);
        nuevaSubasta.setGanador(usuario("ganador@example.com"));
        nuevaSubasta.setIncrementoMinimo(10.0);
        nuevaSubasta.setCompraInmediata(1000.0);
        return nuevaSubasta;
    }
}
