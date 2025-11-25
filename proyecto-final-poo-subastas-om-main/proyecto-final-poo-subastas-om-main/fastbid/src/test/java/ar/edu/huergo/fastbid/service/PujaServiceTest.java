package ar.edu.huergo.fastbid.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import ar.edu.huergo.fastbid.entity.subastas.Puja;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;
import ar.edu.huergo.fastbid.repository.puja.PujaRepository;
import ar.edu.huergo.fastbid.service.HistorialService;

@ExtendWith(MockitoExtension.class)
class PujaServiceTest {

    @Mock
    private PujaRepository pujaRepository;

    @Mock
    private HistorialService historialService;

    @InjectMocks
    private PujaService pujaService;

    private Puja puja;

    @BeforeEach
    void setUp() {
        puja = crearPuja();
    }

    @Test
    void obtenerPujas_devuelveTodasLasPujas() {
        when(pujaRepository.findAll()).thenReturn(List.of(puja));

        List<Puja> resultado = pujaService.obtenerPujas();

        assertThat(resultado).containsExactly(puja);
        verify(pujaRepository).findAll();
    }

    @Test
    void obtenerPujaPorId_devuelvePujaSiExiste() {
        when(pujaRepository.findById(1L)).thenReturn(Optional.of(puja));

        Puja resultado = pujaService.obtenerPujaPorId(1L);

        assertThat(resultado).isEqualTo(puja);
        verify(pujaRepository).findById(1L);
    }

    @Test
    void obtenerPujaPorId_devuelveNullSiNoExiste() {
        when(pujaRepository.findById(2L)).thenReturn(Optional.empty());

        Puja resultado = pujaService.obtenerPujaPorId(2L);

        assertThat(resultado).isNull();
        verify(pujaRepository).findById(2L);
    }

    @Test
    void crearPuja_persisteLaPuja() {
        when(pujaRepository.save(any(Puja.class))).thenReturn(puja);
        when(historialService.registrarEvento(any(Subasta.class), any(Producto.class), any(Usuario.class),
                eq(HistorialTipoEvento.PUJA_NUEVA), anyString())).thenReturn(new Historial());

        Puja resultado = pujaService.crearPuja(puja);

        assertThat(resultado).isEqualTo(puja);
        verify(pujaRepository).save(puja);
        verify(historialService).registrarEvento(any(Subasta.class), any(Producto.class), any(Usuario.class),
                eq(HistorialTipoEvento.PUJA_NUEVA), anyString());
    }

    @Test
    void actualizarPuja_actualizaCamposCuandoExiste() {
        Puja cambios = crearPuja();
        cambios.setMonto(900.0);
        cambios.setFechaHora(LocalDateTime.now().plusMinutes(5));
        when(pujaRepository.findById(eq(1L))).thenReturn(Optional.of(puja));
        when(pujaRepository.save(any(Puja.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Puja resultado = pujaService.actualizarPuja(1L, cambios);

        assertThat(resultado.getMonto()).isEqualTo(900.0);
        assertThat(resultado.getFechaHora()).isEqualTo(cambios.getFechaHora());
        verify(pujaRepository).save(puja);
    }

    @Test
    void actualizarPuja_devuelveNullCuandoNoExiste() {
        when(pujaRepository.findById(3L)).thenReturn(Optional.empty());

        Puja resultado = pujaService.actualizarPuja(3L, puja);

        assertThat(resultado).isNull();
    }

    @Test
    void eliminarPuja_eliminaPorId() {
        doNothing().when(pujaRepository).deleteById(1L);

        pujaService.eliminarPuja(1L);

        verify(pujaRepository).deleteById(1L);
    }

    private Categoria categoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        categoria.setActiva(true);
        return categoria;
    }

    private Usuario usuario(String email, Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Producto producto() {
        Producto producto = new Producto();
        producto.setIdProducto(10L);
        producto.setNombre("Taladro");
        producto.setDescripcion("Taladro percutor");
        producto.setPrecioInicial(800.0);
        producto.setCategoria(categoria());
        producto.setEstado("ACTIVO");
        producto.setImagenes(List.of("http://example.com/1.jpg"));
        producto.setFechaPublicacion(LocalDateTime.now().minusDays(1));
        producto.setFechaFin(LocalDateTime.now().plusDays(2));
        producto.setUsuario(usuario("vendedor@example.com", 20L));
        producto.setSubasta(null);
        producto.setPrecioCompraInmediata(1_500.0);
        producto.setCondicion("NUEVO");
        producto.setUbicacion("Buenos Aires");
        producto.setCantidad(1);
        return producto;
    }

    private Subasta subasta() {
        Subasta subasta = new Subasta();
        subasta.setIdSubasta(10L);
        subasta.setProducto(producto());
        subasta.setFechaInicio(LocalDateTime.now().minusHours(1));
        subasta.setFechaFin(LocalDateTime.now().plusDays(1));
        subasta.setPrecioInicial(800.0);
        subasta.setPrecioActual(850.0);
        subasta.setEstado(SubastaEstado.ACTIVA);
        subasta.setGanador(null);
        subasta.setIncrementoMinimo(25.0);
        subasta.setCompraInmediata(1_500.0);
        return subasta;
    }

    private Puja crearPuja() {
        Puja nuevaPuja = new Puja();
        nuevaPuja.setIdPuja(1L);
        nuevaPuja.setSubasta(subasta());
        nuevaPuja.setUsuario(usuario("postor@example.com", 5L));
        nuevaPuja.setMonto(800.0);
        nuevaPuja.setFechaHora(LocalDateTime.now());
        return nuevaPuja;
    }
}
