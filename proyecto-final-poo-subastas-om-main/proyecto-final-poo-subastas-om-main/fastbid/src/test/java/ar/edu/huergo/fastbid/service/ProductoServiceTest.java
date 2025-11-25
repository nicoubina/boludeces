package ar.edu.huergo.fastbid.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.repository.producto.ProductoRepository;
import ar.edu.huergo.fastbid.service.HistorialService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests de Unidad - productoService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - productoService")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private HistorialService historialService;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto1;

    private Categoria buildCategoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Categoria de herramientas");
        return categoria;
    }

    private Usuario buildUsuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername(email);
        usuario.setPassword("passwordSeguro123");
        return usuario;
    }

    private Producto buildProducto(
            String nombre, String descripcion, double precio, String estado, int cantidad) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecioInicial(precio);
        p.setCategoria(buildCategoria());
        p.setEstado(estado);
        p.setImagenes(Arrays.asList("http://example.com/1.jpg", "http://example.com/2.jpg"));
        p.setFechaPublicacion(LocalDateTime.now());
        p.setFechaFin(LocalDateTime.now().plusDays(7));
        p.setUsuario(buildUsuario("usuario@example.com"));
        p.setSubasta(null);
        p.setPrecioCompraInmediata(precio * 1.2);
        p.setCondicion("NUEVO");
        p.setUbicacion("Buenos Aires, Argentina");
        p.setCantidad(cantidad);
        return p;
    }

    @BeforeEach
    void setUp() {
        producto1 = buildProducto("Taladro", "Percutor 13mm", 120_000.0, "ACTIVO", 3);
        producto1.setIdProducto(10L);
    }

    @Test
    @DisplayName("Debería obtener todos los productos")
    void deberiaObtenerTodosLosProductos() {
        // Given
        Producto p2 = buildProducto("Llave Francesa", "Acero forjado", 15_000.0, "PAUSADO", 5);
        p2.setIdProducto(11L);
        when(productoRepository.findAll()).thenReturn(List.of(producto1, p2));

        // When
        List<Producto> resultado = productoService.obtenerProductos();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener producto por ID cuando existe")
    void deberiaObtenerProductoPorId() {
        // Given
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto1));

        // When (ojo: el método en tu service se llama 'obteneProductoPorId')
        Producto resultado = productoService.obteneProductoPorId(10L);

        // Then
        assertNotNull(resultado);
        assertEquals(10L, resultado.getIdProducto());
        assertEquals("Taladro", resultado.getNombre());
        verify(productoRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Debería devolver null cuando el producto no existe")
    void deberiaDevolverNullCuandoNoExiste() {
        // Given
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Producto resultado = productoService.obteneProductoPorId(999L);

        // Then
        assertNull(resultado);
        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debería crear producto correctamente")
    void deberiaCrearProducto() {
        // Given
        Producto nuevo = buildProducto("Amoladora", "850W 115mm", 65_000.0, "ACTIVO", 2);
        when(productoRepository.save(nuevo)).thenAnswer(inv -> {
            Producto saved = inv.getArgument(0);
            saved.setIdProducto(20L);
            return saved;
        });

        // When
        Producto resultado = productoService.crearProducto(nuevo);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getIdProducto());
        assertEquals("Amoladora", resultado.getNombre());
        verify(productoRepository, times(1)).save(nuevo);
    }

    @Test
    @DisplayName("Debería actualizar producto existente")
    void deberiaActualizarProducto() {
        // Given
        Producto detalles = buildProducto("Taladro Makita 13mm", "Percutor + maletín",
                125_000.0, "ACTIVO", 7);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto1));
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Producto actualizado = productoService.actualizarProducto(10L, detalles);

        // Then
        assertNotNull(actualizado);
        assertEquals("Taladro Makita 13mm", actualizado.getNombre());
        assertEquals("Percutor + maletín", actualizado.getDescripcion());
        assertEquals(125_000.0, actualizado.getPrecioInicial());
        assertEquals(7, actualizado.getCantidad());
        assertEquals("ACTIVO", actualizado.getEstado());
        assertEquals(producto1.getIdProducto(), actualizado.getIdProducto()); // preserva el id
        verify(productoRepository, times(1)).findById(10L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería devolver null al actualizar un producto inexistente")
    void deberiaDevolverNullAlActualizarInexistente() {
        // Given
        when(productoRepository.findById(404L)).thenReturn(Optional.empty());

        // When
        Producto res = productoService.actualizarProducto(404L, buildProducto("X", "Y", 1, "ACTIVO", 1));

        // Then
        assertNull(res);
        verify(productoRepository, times(1)).findById(404L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería eliminar producto por ID")
    void deberiaEliminarProducto() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto1));

        // When / Then
        assertDoesNotThrow(() -> productoService.eliminarProducto(10L));
        verify(productoRepository, times(1)).findById(10L);
        verify(productoRepository, times(1)).delete(producto1);
    }
}
