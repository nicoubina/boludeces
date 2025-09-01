package ar.edu.huergo.lcarera.restaurante.service.plato;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;
import ar.edu.huergo.lcarera.restaurante.repository.plato.PlatoRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Tests de unidad para PlatoService
 * 
 * CONCEPTOS DEMOSTRADOS:
 * 1. @ExtendWith(MockitoExtension.class) - Habilita el uso de Mockito en JUnit
 * 5
 * 2. @Mock - Crea objetos mock (simulados) de las dependencias
 * 3. @InjectMocks - Inyecta los mocks en la clase bajo prueba
 * 4. @BeforeEach - Método que se ejecuta antes de cada test
 * 5. @DisplayName - Nombres descriptivos para los tests
 * 6. when().thenReturn() - Define el comportamiento de los mocks
 * 7. verify() - Verifica que se llamaron métodos específicos
 * 8. Assertions - Verificaciones del resultado esperado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - PlatoService")
class PlatoServiceTest {

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private IngredienteService ingredienteService;

    @InjectMocks
    private PlatoService platoService;

    private Plato platoEjemplo;
    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;
    private List<Long> ingredientesIds;

    @BeforeEach // Se ejecuta antes de cada test
    void setUp() {
        // Preparación de datos de prueba
        ingrediente1 = new Ingrediente(1L, "Tomate", null);
        ingrediente2 = new Ingrediente(2L, "Queso", null);

        platoEjemplo = new Plato();
        platoEjemplo.setId(1L);
        platoEjemplo.setNombre("Pizza Margherita");
        platoEjemplo.setDescripcion("Pizza clásica con tomate y queso");
        platoEjemplo.setPrecio(15.50);
        platoEjemplo.setIngredientes(Arrays.asList(ingrediente1, ingrediente2));

        ingredientesIds = Arrays.asList(1L, 2L);
    }

    @Test
    @DisplayName("Debería obtener todos los platos correctamente")
    void deberiaObtenerTodosLosPlatos() {
        // Given - Preparación
        List<Plato> platosEsperados = Arrays.asList(platoEjemplo);
        when(platoRepository.findAll()).thenReturn(platosEsperados);

        // When - Ejecución
        List<Plato> resultado = platoService.obtenerTodosLosPlatos();

        // Then - Verificación
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(platoEjemplo.getNombre(), resultado.get(0).getNombre());

        // Verificar que se llamó al método del repositorio
        verify(platoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un plato por ID cuando existe")
    void deberiaObtenerPlatoPorIdCuandoExiste() {
        // Given
        Long platoId = 1L;
        when(platoRepository.findById(platoId)).thenReturn(Optional.of(platoEjemplo));

        // When
        Plato resultado = platoService.obtenerPlatoPorId(platoId);

        // Then
        assertNotNull(resultado);
        assertEquals(platoEjemplo.getId(), resultado.getId());
        assertEquals(platoEjemplo.getNombre(), resultado.getNombre());
        verify(platoRepository, times(1)).findById(platoId);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException cuando el plato no existe")
    void deberiaLanzarExcepcionCuandoPlatoNoExiste() {
        // Given
        Long platoIdInexistente = 999L;
        when(platoRepository.findById(platoIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> platoService.obtenerPlatoPorId(platoIdInexistente));

        assertEquals("Plato no encontrado", excepcion.getMessage());
        verify(platoRepository, times(1)).findById(platoIdInexistente);
    }

    @Test
    @DisplayName("Debería crear un plato correctamente")
    void deberiaCrearPlatoCorrectamente() {
        // Given
        Plato nuevoPlato = new Plato();
        nuevoPlato.setNombre("Pasta Carbonara");
        nuevoPlato.setDescripcion("Pasta con huevo y panceta");
        nuevoPlato.setPrecio(12.00);

        List<Ingrediente> ingredientes = Arrays.asList(ingrediente1, ingrediente2);

        when(ingredienteService.resolverIngredientes(ingredientesIds)).thenReturn(ingredientes);
        when(platoRepository.save(any(Plato.class))).thenReturn(nuevoPlato);

        // When
        Plato resultado = platoService.crearPlato(nuevoPlato, ingredientesIds);

        // Then
        assertNotNull(resultado);
        assertEquals(nuevoPlato.getNombre(), resultado.getNombre());
        assertEquals(ingredientes, nuevoPlato.getIngredientes());

        verify(ingredienteService, times(1)).resolverIngredientes(ingredientesIds);
        verify(platoRepository, times(1)).save(nuevoPlato);
    }

    @Test
    @DisplayName("Debería actualizar un plato existente correctamente")
    void deberiaActualizarPlatoExistente() {
        // Given
        Long platoId = 1L;
        Plato platoActualizado = new Plato();
        platoActualizado.setNombre("Pizza Napolitana");
        platoActualizado.setDescripcion("Pizza con anchoas");
        platoActualizado.setPrecio(18.00);

        List<Ingrediente> nuevosIngredientes = Arrays.asList(ingrediente1);
        List<Long> nuevosIngredientesIds = Arrays.asList(1L);

        when(platoRepository.findById(platoId)).thenReturn(Optional.of(platoEjemplo));
        when(ingredienteService.resolverIngredientes(nuevosIngredientesIds)).thenReturn(nuevosIngredientes);
        when(platoRepository.save(any(Plato.class))).thenReturn(platoEjemplo);

        // When
        Plato resultado = platoService.actualizarPlato(platoId, platoActualizado, nuevosIngredientesIds);

        // Then
        assertNotNull(resultado);
        verify(platoRepository, times(1)).findById(platoId);
        verify(ingredienteService, times(1)).resolverIngredientes(nuevosIngredientesIds);
        verify(platoRepository, times(1)).save(platoEjemplo);

        // Verificar que los campos se actualizaron
        assertEquals(platoActualizado.getNombre(), platoEjemplo.getNombre());
        assertEquals(platoActualizado.getDescripcion(), platoEjemplo.getDescripcion());
        assertEquals(platoActualizado.getPrecio(), platoEjemplo.getPrecio());
    }

    @Test
    @DisplayName("Debería actualizar plato sin cambiar ingredientes cuando la lista es null")
    void deberiaActualizarPlatoSinCambiarIngredientes() {
        // Given
        Long platoId = 1L;
        Plato platoActualizado = new Plato();
        platoActualizado.setNombre("Pizza Actualizada");
        platoActualizado.setDescripcion("Descripción actualizada");
        platoActualizado.setPrecio(20.00);

        when(platoRepository.findById(platoId)).thenReturn(Optional.of(platoEjemplo));
        when(platoRepository.save(any(Plato.class))).thenReturn(platoEjemplo);

        // When
        Plato resultado = platoService.actualizarPlato(platoId, platoActualizado, null);

        // Then
        assertNotNull(resultado);
        verify(platoRepository, times(1)).findById(platoId);
        verify(ingredienteService, never()).resolverIngredientes(any());
        verify(platoRepository, times(1)).save(platoEjemplo);
    }
}
