package ar.edu.huergo.lcarera.restaurante.service.plato;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import ar.edu.huergo.lcarera.restaurante.entity.plato.Ingrediente;
import ar.edu.huergo.lcarera.restaurante.repository.plato.IngredienteRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - IngredienteService")
class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @InjectMocks
    private IngredienteService ingredienteService;

    private Ingrediente ingredienteEjemplo;

    @BeforeEach
    void setUp() {
        ingredienteEjemplo = new Ingrediente();
        ingredienteEjemplo.setId(1L);
        ingredienteEjemplo.setNombre("Tomate");
    }

    @Test
    @DisplayName("Debería obtener todos los ingredientes")
    void deberiaObtenerTodosLosIngredientes() {
        // Given
        List<Ingrediente> ingredientesEsperados = Arrays.asList(ingredienteEjemplo);
        /*
         * Seteamos que cuando se llame a findAll, se devuelva la lista de ingredientes
         * esperados
         */
        when(ingredienteRepository.findAll()).thenReturn(ingredientesEsperados);

        // When
        List<Ingrediente> resultado = ingredienteService.obtenerTodosLosIngredientes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(ingredienteEjemplo.getNombre(), resultado.get(0).getNombre());
        verify(ingredienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener ingrediente por ID cuando existe")
    void deberiaObtenerIngredientePorId() {
        // Given
        Long ingredienteId = 1L;
        when(ingredienteRepository.findById(ingredienteId))
                .thenReturn(Optional.of(ingredienteEjemplo));

        // When
        Ingrediente resultado = ingredienteService.obtenerIngredientePorId(ingredienteId);

        // Then
        assertNotNull(resultado);
        assertEquals(ingredienteEjemplo.getId(), resultado.getId());
        assertEquals(ingredienteEjemplo.getNombre(), resultado.getNombre());
        verify(ingredienteRepository, times(1)).findById(ingredienteId);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando ingrediente no existe")
    void deberiaLanzarExcepcionCuandoIngredienteNoExiste() {
        // Given
        Long ingredienteIdInexistente = 999L;
        when(ingredienteRepository.findById(ingredienteIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(EntityNotFoundException.class,
                () -> ingredienteService.obtenerIngredientePorId(ingredienteIdInexistente));

        assertEquals("Ingrediente no encontrado", excepcion.getMessage());
        verify(ingredienteRepository, times(1)).findById(ingredienteIdInexistente);
    }

    @Test
    @DisplayName("Debería crear ingrediente correctamente")
    void deberiaCrearIngrediente() {
        // Given
        Ingrediente nuevoIngrediente = new Ingrediente();
        nuevoIngrediente.setNombre("Cebolla");

        when(ingredienteRepository.save(nuevoIngrediente)).thenReturn(nuevoIngrediente);

        // When
        Ingrediente resultado = ingredienteService.crearIngrediente(nuevoIngrediente);

        // Then
        assertNotNull(resultado);
        assertEquals(nuevoIngrediente.getNombre(), resultado.getNombre());
        verify(ingredienteRepository, times(1)).save(nuevoIngrediente);
    }

    @Test
    @DisplayName("Debería actualizar ingrediente existente")
    void deberiaActualizarIngrediente() {
        // Given
        Long ingredienteId = 1L;
        Ingrediente ingredienteActualizado = new Ingrediente();
        ingredienteActualizado.setNombre("Tomate Cherry");

        when(ingredienteRepository.findById(ingredienteId))
                .thenReturn(Optional.of(ingredienteEjemplo));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteEjemplo);

        // When
        Ingrediente resultado =
                ingredienteService.actualizarIngrediente(ingredienteId, ingredienteActualizado);

        // Then
        assertNotNull(resultado);
        verify(ingredienteRepository, times(1)).findById(ingredienteId);
        verify(ingredienteRepository, times(1)).save(ingredienteEjemplo);
        assertEquals(ingredienteActualizado.getNombre(), ingredienteEjemplo.getNombre());
    }

    @Test
    @DisplayName("Debería eliminar ingrediente existente")
    void deberiaEliminarIngrediente() {
        // Given
        Long ingredienteId = 1L;
        when(ingredienteRepository.findById(ingredienteId))
                .thenReturn(Optional.of(ingredienteEjemplo));
        doNothing().when(ingredienteRepository).delete(ingredienteEjemplo);

        // When
        assertDoesNotThrow(() -> ingredienteService.eliminarIngrediente(ingredienteId));

        // Then
        verify(ingredienteRepository, times(1)).findById(ingredienteId);
        verify(ingredienteRepository, times(1)).delete(ingredienteEjemplo);
    }

    @Test
    @DisplayName("Debería buscar ingredientes por nombre (case insensitive)")
    void deberiaBuscarIngredientesPorNombre() {
        // Given
        String nombreBusqueda = "tom";
        List<Ingrediente> ingredientesEncontrados = Arrays.asList(ingredienteEjemplo);
        when(ingredienteRepository.findByNombreContainingIgnoreCase(nombreBusqueda))
                .thenReturn(ingredientesEncontrados);

        // When
        List<Ingrediente> resultado =
                ingredienteService.obtenerIngredientesPorNombre(nombreBusqueda);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(ingredienteEjemplo.getNombre(), resultado.get(0).getNombre());
        verify(ingredienteRepository, times(1)).findByNombreContainingIgnoreCase(nombreBusqueda);
    }

    @Test
    @DisplayName("Debería retornar lista vacía cuando no encuentra ingredientes por nombre")
    void deberiaRetornarListaVaciaCuandoNoEncuentraIngredientes() {
        // Given
        String nombreBusqueda = "inexistente";
        when(ingredienteRepository.findByNombreContainingIgnoreCase(nombreBusqueda))
                .thenReturn(Arrays.asList());

        // When
        List<Ingrediente> resultado =
                ingredienteService.obtenerIngredientesPorNombre(nombreBusqueda);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(ingredienteRepository, times(1)).findByNombreContainingIgnoreCase(nombreBusqueda);
    }
}
