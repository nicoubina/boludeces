package ar.edu.huergo.clickservice.buscadorservicios.repository.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ServicioRepository;

@DataJpaTest
class ServicioRepositoryTest {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Servicio servicio1;
    private Servicio servicio2;
    private Servicio servicio3;

    @BeforeEach
    void setUp() {
        servicio1 = new Servicio(null, "Plomería", 25.0);
        servicio2 = new Servicio(null, "Electricidad", 30.5);
        servicio3 = new Servicio(null, "Carpintería", 40.75);

        entityManager.persist(servicio1);
        entityManager.persist(servicio2);
        entityManager.persist(servicio3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debería verificar existencia servicio por id")
    void deberiaVerificarExistenciaServicioPorId() {
        // When & Then
        assertTrue(servicioRepository.existsById(servicio1.getId()));
        assertTrue(servicioRepository.existsById(servicio2.getId()));
        assertTrue(servicioRepository.existsById(servicio3.getId()));
        assertFalse(servicioRepository.existsById(999L));
    }

    @Test
    @DisplayName("Debería eliminar servicio por entidad")
    void deberiaEliminarServicioPorEntidad() {
        // Given
        Long servicioId = servicio3.getId();
        assertTrue(servicioRepository.existsById(servicioId));

        // When
        servicioRepository.delete(servicio3);
        entityManager.flush();

        // Then
        assertFalse(servicioRepository.existsById(servicioId));
    }

    @Test
    @DisplayName("Debería manejar servicios con precios decimales")
    void deberiaManejarServiciosConPreciosDecimales() {
        // Given
        Servicio servicioConDecimales = new Servicio();
        servicioConDecimales.setNombre("Albañilería");
        servicioConDecimales.setPrecioHora(22.75);

        // When
        Servicio servicioGuardado = servicioRepository.save(servicioConDecimales);
        entityManager.flush();
        entityManager.clear();

        Optional<Servicio> servicioRecuperado =
                servicioRepository.findById(servicioGuardado.getId());

        // Then
        assertTrue(servicioRecuperado.isPresent());
        assertEquals(22.75, servicioRecuperado.get().getPrecioHora());
    }
}