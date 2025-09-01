package ar.edu.huergo.lcarera.restaurante.repository.plato;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Ingrediente;

@DataJpaTest
@DisplayName("Tests de Integración - IngredienteRepository")
class IngredienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;
    private Ingrediente ingrediente3;

    @BeforeEach
    void setUp() {
        // Crear ingredientes de prueba
        ingrediente1 = new Ingrediente();
        ingrediente1.setNombre("Tomate");
        ingrediente1 = entityManager.persistAndFlush(ingrediente1);

        ingrediente2 = new Ingrediente();
        ingrediente2.setNombre("Tomate Cherry");
        ingrediente2 = entityManager.persistAndFlush(ingrediente2);

        ingrediente3 = new Ingrediente();
        ingrediente3.setNombre("Queso Mozzarella");
        ingrediente3 = entityManager.persistAndFlush(ingrediente3);

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería encontrar ingredientes por nombre conteniendo texto (case insensitive)")
    void deberiaEncontrarIngredientesPorNombreContaining() {
        // When - Buscar ingredientes que contengan "tomate"
        List<Ingrediente> ingredientesEncontrados =
                ingredienteRepository.findByNombreContainingIgnoreCase("tomate");

        // Then
        assertNotNull(ingredientesEncontrados);
        assertEquals(2, ingredientesEncontrados.size());

        List<String> nombres =
                ingredientesEncontrados.stream().map(Ingrediente::getNombre).toList();
        assertTrue(nombres.contains("Tomate"));
        assertTrue(nombres.contains("Tomate Cherry"));
    }

    @Test
    @DisplayName("Debería encontrar ingredientes con búsqueda case insensitive")
    void deberiaEncontrarIngredientesCaseInsensitive() {
        // When - Buscar con diferentes casos
        List<Ingrediente> resultadoMinuscula =
                ingredienteRepository.findByNombreContainingIgnoreCase("TOMATE");
        List<Ingrediente> resultadoMayuscula =
                ingredienteRepository.findByNombreContainingIgnoreCase("tomate");
        List<Ingrediente> resultadoMixto =
                ingredienteRepository.findByNombreContainingIgnoreCase("ToMaTe");

        // Then - Todos deberían dar el mismo resultado
        assertEquals(2, resultadoMinuscula.size());
        assertEquals(2, resultadoMayuscula.size());
        assertEquals(2, resultadoMixto.size());
    }

    @Test
    @DisplayName("Debería retornar lista vacía cuando no encuentra coincidencias")
    void deberiaRetornarListaVaciaSinCoincidencias() {
        // When
        List<Ingrediente> ingredientesEncontrados =
                ingredienteRepository.findByNombreContainingIgnoreCase("inexistente");

        // Then
        assertNotNull(ingredientesEncontrados);
        assertTrue(ingredientesEncontrados.isEmpty());
    }

    @Test
    @DisplayName("Debería encontrar ingredientes con búsqueda parcial")
    void deberiaEncontrarIngredientesConBusquedaParcial() {
        // When - Buscar solo parte del nombre
        List<Ingrediente> resultadoQue =
                ingredienteRepository.findByNombreContainingIgnoreCase("que");
        List<Ingrediente> resultadoMozza =
                ingredienteRepository.findByNombreContainingIgnoreCase("mozza");

        // Then
        assertEquals(1, resultadoQue.size());
        assertEquals("Queso Mozzarella", resultadoQue.get(0).getNombre());

        assertEquals(1, resultadoMozza.size());
        assertEquals("Queso Mozzarella", resultadoMozza.get(0).getNombre());
    }

    @Test
    @DisplayName("Debería guardar y recuperar ingrediente correctamente")
    void deberiaGuardarYRecuperarIngrediente() {
        // Given
        Ingrediente nuevoIngrediente = new Ingrediente();
        nuevoIngrediente.setNombre("Albahaca");

        // When
        Ingrediente ingredienteGuardado = ingredienteRepository.save(nuevoIngrediente);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(ingredienteGuardado.getId());

        Optional<Ingrediente> ingredienteRecuperado =
                ingredienteRepository.findById(ingredienteGuardado.getId());

        assertTrue(ingredienteRecuperado.isPresent());
        assertEquals("Albahaca", ingredienteRecuperado.get().getNombre());
    }

    @Test
    @DisplayName("Debería actualizar ingrediente existente")
    void deberiaActualizarIngredienteExistente() {
        // Given
        String nuevoNombre = "Tomate Roma";

        // When
        Optional<Ingrediente> ingredienteOptional =
                ingredienteRepository.findById(ingrediente1.getId());
        assertTrue(ingredienteOptional.isPresent());

        Ingrediente ingrediente = ingredienteOptional.get();
        ingrediente.setNombre(nuevoNombre);

        Ingrediente ingredienteActualizado = ingredienteRepository.save(ingrediente);
        entityManager.flush();

        // Then
        assertEquals(nuevoNombre, ingredienteActualizado.getNombre());

        // Verificar persistencia
        entityManager.clear();
        Optional<Ingrediente> ingredienteVerificacion =
                ingredienteRepository.findById(ingrediente1.getId());
        assertTrue(ingredienteVerificacion.isPresent());
        assertEquals(nuevoNombre, ingredienteVerificacion.get().getNombre());
    }

    @Test
    @DisplayName("Debería eliminar ingrediente correctamente")
    void deberiaEliminarIngrediente() {
        // Given
        Long ingredienteId = ingrediente1.getId();
        assertTrue(ingredienteRepository.existsById(ingredienteId));

        // When
        ingredienteRepository.deleteById(ingredienteId);
        entityManager.flush();

        // Then
        assertFalse(ingredienteRepository.existsById(ingredienteId));
        Optional<Ingrediente> ingredienteEliminado = ingredienteRepository.findById(ingredienteId);
        assertFalse(ingredienteEliminado.isPresent());
    }

    @Test
    @DisplayName("Debería encontrar todos los ingredientes")
    void deberiaEncontrarTodosLosIngredientes() {
        // When
        List<Ingrediente> todosLosIngredientes = ingredienteRepository.findAll();

        // Then
        assertNotNull(todosLosIngredientes);
        assertEquals(3, todosLosIngredientes.size());

        List<String> nombres = todosLosIngredientes.stream().map(Ingrediente::getNombre).toList();
        assertTrue(nombres.contains("Tomate"));
        assertTrue(nombres.contains("Tomate Cherry"));
        assertTrue(nombres.contains("Queso Mozzarella"));
    }

    @Test
    @DisplayName("Debería contar ingredientes correctamente")
    void deberiaContarIngredientes() {
        // When
        long cantidadIngredientes = ingredienteRepository.count();

        // Then
        assertEquals(3, cantidadIngredientes);

        // Agregar un ingrediente más y verificar
        Ingrediente nuevoIngrediente = new Ingrediente();
        nuevoIngrediente.setNombre("Orégano");
        entityManager.persistAndFlush(nuevoIngrediente);

        assertEquals(4, ingredienteRepository.count());
    }

    @Test
    @DisplayName("Debería validar restricciones de la entidad")
    void deberiaValidarRestricciones() {
        // Given - Crear ingrediente con nombre vacío
        Ingrediente ingredienteInvalido = new Ingrediente();
        ingredienteInvalido.setNombre(""); // Viola @NotBlank

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(ingredienteInvalido);
        });
    }

    @Test
    @DisplayName("Debería manejar nombres con espacios en la búsqueda")
    void deberiaManejarNombresConEspacios() {
        // When - Buscar parte del nombre que incluye espacios
        List<Ingrediente> resultado =
                ingredienteRepository.findByNombreContainingIgnoreCase("cherry");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Tomate Cherry", resultado.get(0).getNombre());
    }
}
