package ar.edu.huergo.clickservice.buscadorservicios.repository.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Comision;

@DataJpaTest
class ComisionRepositoryTest {

    @Autowired
    private ComisionRepository comisionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Comision comision1;
    private Comision comision2;
    private Comision comision3;

    @BeforeEach
    void setUp() {
        comision1 = new Comision(null, 10L, 12.5, 300.0, 37.5, LocalDate.now());
        comision2 = new Comision(null, 11L, 10.0, 200.0, 20.0, LocalDate.now().minusDays(1));
        comision3 = new Comision(null, 12L, 8.5, 150.0, 12.75, LocalDate.now().minusDays(2));

        entityManager.persist(comision1);
        entityManager.persist(comision2);
        entityManager.persist(comision3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debería verificar existencia de comisiones por id")
    void deberiaVerificarExistenciaDeComisionesPorId() {
        // When & Then
        assertTrue(comisionRepository.existsById(comision1.getId()));
        assertTrue(comisionRepository.existsById(comision2.getId()));
        assertTrue(comisionRepository.existsById(comision3.getId()));
        assertFalse(comisionRepository.existsById(999L));
    }

    @Test
    @DisplayName("Debería eliminar una comisión persistida")
    void deberiaEliminarUnaComisionPersistida() {
        // Given
        Long id = comision2.getId();
        assertTrue(comisionRepository.existsById(id));

        // When
        comisionRepository.delete(comision2);
        entityManager.flush();

        // Then
        assertFalse(comisionRepository.existsById(id));
    }

    @Test
    @DisplayName("Debería almacenar montos y bases con decimales")
    void deberiaAlmacenarMontosYBasesConDecimales() {
        // Given
        Comision comision = new Comision(null, 99L, 15.75, 123.45, 19.44, LocalDate.now());

        // When
        Comision guardada = comisionRepository.save(comision);
        entityManager.flush();
        entityManager.clear();

        Comision recuperada = comisionRepository.findById(guardada.getId()).orElseThrow();

        // Then
        assertEquals(123.45, recuperada.getBase());
        assertEquals(19.44, recuperada.getMonto());
    }
}
