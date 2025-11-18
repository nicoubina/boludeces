package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.AgendaSlot;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;

@DataJpaTest
class AgendaSlotRepositoryTest {

    @Autowired
    private AgendaSlotRepository agendaSlotRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Profesional profesional;
    private AgendaSlot slotDisponible;
    private AgendaSlot slotNoDisponible;

    @BeforeEach
    void setUp() {
        profesional = persistirProfesional("disponible@example.com", "20123456");
        Profesional otroProfesional = persistirProfesional("otro@example.com", "30123456");

        slotDisponible = persistirAgendaSlot(
            profesional,
            LocalDateTime.of(2024, 10, 1, 9, 0),
            LocalDateTime.of(2024, 10, 1, 11, 0),
            true
        );

        slotNoDisponible = persistirAgendaSlot(
            profesional,
            LocalDateTime.of(2024, 10, 2, 9, 0),
            LocalDateTime.of(2024, 10, 2, 11, 0),
            false
        );

        persistirAgendaSlot(
            otroProfesional,
            LocalDateTime.of(2024, 10, 3, 14, 0),
            LocalDateTime.of(2024, 10, 3, 16, 0),
            true
        );

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Debería recuperar todos los slots por profesional")
    void deberiaBuscarSlotsPorProfesional() {
        List<AgendaSlot> slots = agendaSlotRepository.findByProfesionalId(profesional.getId());

        assertThat(slots)
            .as("Debe devolver todos los slots asociados al profesional")
            .hasSize(2)
            .allMatch(slot -> slot.getProfesional().getId().equals(profesional.getId()))
            .extracting(AgendaSlot::getId)
            .containsExactlyInAnyOrder(slotDisponible.getId(), slotNoDisponible.getId());
    }

    @Test
    @DisplayName("Debería listar sólo los slots disponibles por profesional")
    void deberiaBuscarSlotsDisponiblesPorProfesional() {
        List<AgendaSlot> disponibles = agendaSlotRepository.findByProfesionalIdAndDisponibleTrue(profesional.getId());

        assertThat(disponibles)
            .as("Debe devolver únicamente los slots marcados como disponibles")
            .hasSize(1)
            .allMatch(AgendaSlot::getDisponible)
            .extracting(AgendaSlot::getId)
            .containsExactly(slotDisponible.getId());
    }

    @Test
    @DisplayName("Debería detectar solapamiento de horarios para un profesional")
    void deberiaDetectarSolapamientoDeSlots() {
        boolean existeSolapamiento = agendaSlotRepository
            .existsByProfesionalIdAndFechaInicioLessThanAndFechaFinGreaterThan(
                profesional.getId(),
                LocalDateTime.of(2024, 10, 1, 12, 0), // fechaFin consulta (param 2)
                LocalDateTime.of(2024, 10, 1, 10, 0)  // fechaInicio consulta (param 3)
            );

        assertThat(existeSolapamiento)
            .as("Debe detectar cuando un nuevo rango horario se solapa con uno existente")
            .isTrue();
    }

    // ===== Helpers =====

    private AgendaSlot persistirAgendaSlot(
        Profesional profesional,
        LocalDateTime inicio,
        LocalDateTime fin,
        boolean disponible
    ) {
        AgendaSlot slot = new AgendaSlot();
        slot.setProfesional(profesional);
        slot.setFechaInicio(inicio);
        slot.setFechaFin(fin);
        slot.setDisponible(disponible);

        entityManager.persist(slot);
        return slot;
    }

    private Profesional persistirProfesional(String username, String dni) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre " + username);
        usuario.setApellido("Apellido " + username);
        usuario.setDni(dni);
        usuario.setTelefono("+54 9 11 4000-" + dni.substring(0, 4));
        usuario.setCalle("Calle Falsa");
        usuario.setAltura(123);
        usuario.setUsername(username);
        usuario.setPassword("contraseña_segura_de_mas_de_16");

        entityManager.persist(usuario);

        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto("Profesional " + username);
        profesional.setTelefono("+54 9 11 5000-" + dni.substring(0, 4));
        profesional.setDescripcion("Profesional con amplia experiencia");
        profesional.setDisponible(true);
        profesional.setZonaTrabajo("Zona Centro");
        profesional.setServicios(new HashSet<>());

        entityManager.persist(profesional);
        return profesional;
    }
}
