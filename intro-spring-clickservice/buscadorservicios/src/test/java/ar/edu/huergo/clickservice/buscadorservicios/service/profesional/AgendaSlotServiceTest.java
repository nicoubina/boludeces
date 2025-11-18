package ar.edu.huergo.clickservice.buscadorservicios.service.profesional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.AgendaSlot;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.AgendaSlotRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ProfesionalRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AgendaSlotServiceTest {

    @Mock
    private AgendaSlotRepository agendaSlotRepository;

    @Mock
    private ProfesionalRepository profesionalRepository;

    @InjectMocks
    private AgendaSlotService agendaSlotService;

    private Profesional profesional;
    private AgendaSlot agendaSlot;

    @BeforeEach
    void setUp() {
        profesional = new Profesional();
        profesional.setId(1L);
        agendaSlot = new AgendaSlot();
        agendaSlot.setId(10L);
        agendaSlot.setProfesional(profesional);
        agendaSlot.setFechaInicio(LocalDateTime.now().plusDays(1));
        agendaSlot.setFechaFin(agendaSlot.getFechaInicio().plusHours(2));
        agendaSlot.setDisponible(Boolean.TRUE);
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener agenda slots")
    void deberiaManejarListaVaciaAlObtenerAgendaSlots() {
        // Given
        when(agendaSlotRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<AgendaSlot> resultado = agendaSlotService.obtenerTodosLosAgendaSlots();

        // Then
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(agendaSlotRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería crear un agenda slot con profesional válido")
    void deberiaCrearUnAgendaSlotConProfesionalValido() {
        // Given
        when(profesionalRepository.findById(1L)).thenReturn(Optional.of(profesional));
        when(agendaSlotRepository.save(agendaSlot)).thenReturn(agendaSlot);

        // When
        AgendaSlot resultado = agendaSlotService.crearAgendaSlot(agendaSlot);

        // Then
        assertNotNull(resultado);
        assertEquals(profesional.getId(), resultado.getProfesional().getId());
        verify(profesionalRepository, times(1)).findById(1L);
        verify(agendaSlotRepository, times(1)).save(agendaSlot);
    }

    @Test
    @DisplayName("Debería actualizar un agenda slot con nuevo profesional")
    void deberiaActualizarUnAgendaSlotConNuevoProfesional() {
        // Given
        Profesional nuevoProfesional = new Profesional();
        nuevoProfesional.setId(2L);

        AgendaSlot cambios = new AgendaSlot();
        cambios.setProfesional(nuevoProfesional);
        cambios.setFechaInicio(LocalDateTime.now().plusDays(2));
        cambios.setFechaFin(cambios.getFechaInicio().plusHours(3));
        cambios.setDisponible(Boolean.FALSE);

        when(agendaSlotRepository.findById(10L)).thenReturn(Optional.of(agendaSlot));
        when(profesionalRepository.findById(2L)).thenReturn(Optional.of(nuevoProfesional));
        when(agendaSlotRepository.save(agendaSlot)).thenReturn(agendaSlot);

        // When
        AgendaSlot resultado = agendaSlotService.actualizarAgendaSlot(10L, cambios);

        // Then
        assertEquals(2L, resultado.getProfesional().getId());
        assertEquals(Boolean.FALSE, resultado.getDisponible());
        verify(agendaSlotRepository, times(1)).save(agendaSlot);
    }

    @Test
    @DisplayName("Debería lanzar excepción al crear agenda slot con profesional inexistente")
    void deberiaLanzarExcepcionAlCrearAgendaSlotConProfesionalInexistente() {
        // Given
        when(profesionalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> agendaSlotService.crearAgendaSlot(agendaSlot));
    }
}