package ar.edu.huergo.clickservice.buscadorservicios.service.profesional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ProfesionalRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProfesionalServiceTest {

    @Mock
    private ProfesionalRepository profesionalRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ProfesionalService profesionalService;

    private Profesional profesional;

    @BeforeEach
    void setUp() {
        profesional = new Profesional();
        profesional.setId(1L);
        profesional.setNombreCompleto("Profesional Test");
        profesional.setTelefono("+54 9 11 1111-1111");
        profesional.setDescripcion("Especialista en múltiples servicios");
        profesional.setDisponible(Boolean.TRUE);
    }

    @Test
    @DisplayName("Debería obtener la lista de profesionales disponibles")
    void deberiaObtenerLaListaDeProfesionalesDisponibles() {
        // Given
        when(profesionalRepository.findByDisponibleTrue()).thenReturn(List.of(profesional));

        // When
        List<Profesional> resultado = profesionalService.obtenerProfesionalesDisponibles();

        // Then
        assertEquals(1, resultado.size());
        assertEquals(profesional.getId(), resultado.get(0).getId());
        verify(profesionalRepository, times(1)).findByDisponibleTrue();
    }

    @Test
    @DisplayName("Debería asignar servicios a un profesional existente")
    void deberiaAsignarServiciosAUnProfesionalExistente() {
        // Given
        Servicio servicio = new Servicio(2L, "Electricidad", 40.0);
        when(profesionalRepository.findById(1L)).thenReturn(Optional.of(profesional));
        when(servicioRepository.findAllById(Set.of(2L))).thenReturn(List.of(servicio));
        when(profesionalRepository.save(profesional)).thenReturn(profesional);

        // When
        Profesional resultado = profesionalService.asignarServicios(1L, Set.of(2L));

        // Then
        assertNotNull(resultado.getServicios());
        assertEquals(1, resultado.getServicios().size());
        verify(servicioRepository, times(1)).findAllById(Set.of(2L));
        verify(profesionalRepository, times(1)).save(profesional);
    }

    @Test
    @DisplayName("Debería lanzar excepción al buscar profesional por usuario inexistente")
    void deberiaLanzarExcepcionAlBuscarProfesionalPorUsuarioInexistente() {
        // Given
        when(profesionalRepository.findByUsuarioId(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> profesionalService.obtenerProfesionalPorUsuarioId(99L));
    }
}