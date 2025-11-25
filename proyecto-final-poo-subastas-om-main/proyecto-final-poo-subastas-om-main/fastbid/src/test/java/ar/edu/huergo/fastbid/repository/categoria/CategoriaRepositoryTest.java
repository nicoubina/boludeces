package ar.edu.huergo.fastbid.repository.categoria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import ar.edu.huergo.fastbid.entity.Categoria;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();

        categoria1 = new Categoria();
        categoria1.setNombre("Electrónica");
        categoria1.setDescripcion("Productos electrónicos");
        categoria1.setActiva(true);

        categoria2 = new Categoria();
        categoria2.setNombre("Hogar");
        categoria2.setDescripcion("Productos para el hogar");
        categoria2.setActiva(false);
    }

    @Test
    @DisplayName("Debe guardar una categoría")
    void testGuardarCategoria() {
        // When
        Categoria guardada = categoriaRepository.save(categoria1);

        // Then
        assertNotNull(guardada);
        assertNotNull(guardada.getIdCategoria());
        assertEquals("Electrónica", guardada.getNombre());
    }

    @Test
    @DisplayName("Debe encontrar una categoría por nombre")
    void testFindByNombre() {
        // Given
        categoriaRepository.save(categoria1);

        // When
        Optional<Categoria> resultado = categoriaRepository.findByNombre("Electrónica");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Electrónica", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Debe retornar vacío si no encuentra categoría por nombre")
    void testFindByNombreNoExiste() {
        // When
        Optional<Categoria> resultado = categoriaRepository.findByNombre("NoExiste");

        // Then
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar solo categorías activas")
    void testFindByActivaTrue() {
        // Given
        categoriaRepository.save(categoria1);
        categoriaRepository.save(categoria2);

        // When
        List<Categoria> activas = categoriaRepository.findByActivaTrue();

        // Then
        assertNotNull(activas);
        assertEquals(1, activas.size());
        assertTrue(activas.get(0).isActiva());
        assertEquals("Electrónica", activas.get(0).getNombre());
    }

    @Test
    @DisplayName("Debe verificar si existe una categoría por nombre")
    void testExistsByNombre() {
        // Given
        categoriaRepository.save(categoria1);

        // When
        boolean existe = categoriaRepository.existsByNombre("Electrónica");
        boolean noExiste = categoriaRepository.existsByNombre("NoExiste");

        // Then
        assertTrue(existe);
        assertFalse(noExiste);
    }

    @Test
    @DisplayName("Debe actualizar una categoría")
    void testActualizarCategoria() {
        // Given
        Categoria guardada = categoriaRepository.save(categoria1);
        Long id = guardada.getIdCategoria();

        // When
        guardada.setNombre("Tecnología");
        guardada.setDescripcion("Productos de tecnología avanzada");
        Categoria actualizada = categoriaRepository.save(guardada);

        // Then
        assertEquals(id, actualizada.getIdCategoria());
        assertEquals("Tecnología", actualizada.getNombre());
        assertEquals("Productos de tecnología avanzada", actualizada.getDescripcion());
    }

    @Test
    @DisplayName("Debe eliminar una categoría")
    void testEliminarCategoria() {
        // Given
        Categoria guardada = categoriaRepository.save(categoria1);
        Long id = guardada.getIdCategoria();

        // When
        categoriaRepository.deleteById(id);

        // Then
        assertFalse(categoriaRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("Debe encontrar todas las categorías")
    void testFindAll() {
        // Given
        categoriaRepository.save(categoria1);
        categoriaRepository.save(categoria2);

        // When
        List<Categoria> todas = categoriaRepository.findAll();

        // Then
        assertNotNull(todas);
        assertEquals(2, todas.size());
    }

    @Test
    @DisplayName("Debe verificar unicidad del nombre de categoría")
    void testUnicidadNombre() {
        // Given
        categoriaRepository.save(categoria1);

        // When
        Categoria duplicada = new Categoria();
        duplicada.setNombre("Electrónica");
        duplicada.setDescripcion("Otra descripción");
        duplicada.setActiva(true);

        // Then
        try {
            categoriaRepository.save(duplicada);
            categoriaRepository.flush();
            // Si llega aquí, el test debe fallar
            assertTrue(false, "Debería haber lanzado una excepción por nombre duplicado");
        } catch (Exception e) {
            // Se espera una excepción
            assertTrue(true);
        }
    }
}