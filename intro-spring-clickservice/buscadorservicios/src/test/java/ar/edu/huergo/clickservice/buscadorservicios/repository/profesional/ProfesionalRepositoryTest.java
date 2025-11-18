package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;

@DataJpaTest
class ProfesionalRepositoryTest {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe encontrarse el profesional por id de usuario y no para un usuario inexistente")
    void deberiaEncontrarProfesionalPorUsuario() {
        // Arrange
        Servicio servicio = persistirServicio(1L, "Electricidad", 1200.0);
        Profesional profesional = persistirProfesional(1L, true, servicio);
        entityManager.clear();

        // Act + Assert (usuario existente)
        var resultado = profesionalRepository.findByUsuarioId(profesional.getUsuario().getId());

        assertThat(resultado)
            .as("Debe encontrarse el profesional asociado al usuario")
            .isPresent()
            .get()
            .extracting(Profesional::getId)
            .isEqualTo(profesional.getId());

        // Act + Assert (usuario inexistente)
        assertThat(profesionalRepository.findByUsuarioId(999L))
            .as("No debe encontrarse un profesional para un usuario inexistente")
            .isEmpty();
    }

    @Test
    @DisplayName("Debe listar solo profesionales disponibles")
    void deberiaListarSoloProfesionalesDisponibles() {
        // Arrange
        Servicio servicio = persistirServicio(2L, "Plomeria", 1500.0);
        Profesional disponible = persistirProfesional(2L, true, servicio);
        persistirProfesional(3L, false, servicio);
        entityManager.clear();

        // Act
        List<Profesional> profesionales = profesionalRepository.findByDisponibleTrue();

        // Assert (con tus cambios)
        assertThat(profesionales)
            .as("Solo debe devolver profesionales con disponibilidad activa")
            .hasSize(1)
            .allMatch(Profesional::getDisponible)
            .extracting(Profesional::getId)
            .containsExactly(disponible.getId());
    }

    @Test
    @DisplayName("Debe encontrar profesionales disponibles por servicio")
    void deberiaEncontrarDisponiblesPorServicio() {
        // Arrange
        Servicio electricidad = persistirServicio(4L, "Electricista", 1800.0);
        Servicio gasista = persistirServicio(5L, "Gasista", 2000.0);

        Profesional profesionalDisponible = persistirProfesional(4L, true, electricidad);
        persistirProfesional(5L, true, gasista);
        persistirProfesional(6L, false, electricidad);
        entityManager.clear();

        // Act
        List<Profesional> encontrados =
            profesionalRepository.findByServiciosIdAndDisponibleTrue(electricidad.getId());

        // Assert (con tus cambios)
        assertThat(encontrados)
            .as("Debe listar únicamente profesionales disponibles asociados al servicio indicado")
            .hasSize(1)
            .allMatch(Profesional::getDisponible)
            .extracting(Profesional::getId)
            .containsExactly(profesionalDisponible.getId());
    }

    // ===== Helpers =====

    private Servicio persistirServicio(Long indice, String nombre, Double precio) {
        Servicio s = new Servicio(null, nombre, precio);
        entityManager.persist(s);
        entityManager.flush();
        return s;
    }

    private Profesional persistirProfesional(Long indice, boolean disponible, Servicio servicio) {
        Usuario usuario = persistirUsuario(indice);

        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto("Profesional " + indice);
        profesional.setTelefono("+541100000" + indice);
        profesional.setDescripcion("Descripcion " + indice);
        profesional.setDisponible(disponible);
        profesional.setZonaTrabajo("Zona " + indice);
        profesional.setServicios(new HashSet<>(Collections.singleton(servicio)));

        entityManager.persist(profesional);
        entityManager.flush();
        return profesional;
    }

    private Usuario persistirUsuario(Long indice) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre" + indice);
        usuario.setApellido("Apellido" + indice);
        usuario.setDni(String.format("%08d", 10000000 + indice));
        usuario.setTelefono("+54119999" + String.format("%02d", indice));
        usuario.setCalle("Calle " + indice);
        usuario.setAltura(100 + indice.intValue());
        usuario.setUsername("user" + indice + "@example.com");
        usuario.setPassword("contraseña_segura_de_mas_de_16");
        entityManager.persist(usuario);
        entityManager.flush();
        return usuario;
    }
}
