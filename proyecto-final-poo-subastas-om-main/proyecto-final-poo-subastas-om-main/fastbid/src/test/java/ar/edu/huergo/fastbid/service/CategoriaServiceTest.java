package ar.edu.huergo.fastbid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.repository.categoria.CategoriaRepository;
import ar.edu.huergo.fastbid.service.HistorialService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private HistorialService historialService;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos y tecnología");
        categoria.setActiva(true);
    }

    @Test
    @DisplayName("Debe obtener todas las categorías")
    void testObtenerCategorias() {
        // Given
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        // When
        List<Categoria> resultado = categoriaService.obtenerCategorias();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener solo las categorías activas")
    void testObtenerCategoriasActivas() {
        // Given
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findByActivaTrue()).thenReturn(categorias);

        // When
        List<Categoria> resultado = categoriaService.obtenerCategoriasActivas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isActiva());
        verify(categoriaRepository, times(1)).findByActivaTrue();
    }

    @Test
    @DisplayName("Debe obtener una categoría por ID")
    void testObtenerCategoriaPorId() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        Categoria resultado = categoriaService.obtenerCategoriaPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Electrónica", resultado.getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la categoría no existe")
    void testObtenerCategoriaPorIdNoExiste() {
        // Given
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            categoriaService.obtenerCategoriaPorId(999L);
        });
    }

    @Test
    @DisplayName("Debe crear una categoría exitosamente")
    void testCrearCategoria() {
        // Given
        when(categoriaRepository.existsByNombre(anyString())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // When
        Categoria resultado = categoriaService.crearCategoria(categoria);

        // Then
        assertNotNull(resultado);
        assertEquals("Electrónica", resultado.getNombre());
        verify(categoriaRepository, times(1)).existsByNombre(anyString());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear categoría con nombre duplicado")
    void testCrearCategoriaConNombreDuplicado() {
        // Given
        when(categoriaRepository.existsByNombre(anyString())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.crearCategoria(categoria);
        });
    }

    @Test
    @DisplayName("Debe actualizar una categoría exitosamente")
    void testActualizarCategoria() {
        // Given
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setNombre("Tecnología");
        categoriaActualizada.setDescripcion("Productos de tecnología");
        categoriaActualizada.setActiva(true);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombre("Tecnología")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // When
        Categoria resultado = categoriaService.actualizarCategoria(1L, categoriaActualizada);

        // Then
        assertNotNull(resultado);
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar con nombre duplicado")
    void testActualizarCategoriaConNombreDuplicado() {
        // Given
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setNombre("Hogar");
        categoriaActualizada.setDescripcion("Nueva descripción");
        categoriaActualizada.setActiva(true);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombre("Hogar")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.actualizarCategoria(1L, categoriaActualizada);
        });
    }

    @Test
    @DisplayName("Debe eliminar una categoría exitosamente")
    void testEliminarCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        categoriaService.eliminarCategoria(1L);

        // Then
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).delete(categoria);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar categoría inexistente")
    void testEliminarCategoriaInexistente() {
        // Given
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            categoriaService.eliminarCategoria(999L);
        });
    }

    @Test
    @DisplayName("Debe desactivar una categoría")
    void testDesactivarCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // When
        categoriaService.desactivarCategoria(1L);

        // Then
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Debe activar una categoría")
    void testActivarCategoria() {
        // Given
        categoria.setActiva(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // When
        categoriaService.activarCategoria(1L);

        // Then
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }
}
