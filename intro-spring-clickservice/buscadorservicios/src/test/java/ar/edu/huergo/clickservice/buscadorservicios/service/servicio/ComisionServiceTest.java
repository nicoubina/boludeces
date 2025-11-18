package ar.edu.huergo.clickservice.buscadorservicios.service.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Comision;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ComisionRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ComisionServiceTest {

    @Mock
    private ComisionRepository comisionRepository;

    @InjectMocks
    private ComisionService comisionService;

    private Comision comision;

    @BeforeEach
    void setUp() {
        comision = new Comision(1L, 10L, 12.5, 200.0, 25.0, LocalDate.now());
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener todas las comisiones")
    void deberiaManejarListaVaciaAlObtenerTodasLasComisiones() {
        // Given
        when(comisionRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Comision> resultado = comisionService.obtenerTodasLasComisiones();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(comisionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería actualizar una comisión existente")
    void deberiaActualizarUnaComisionExistente() {
        // Given
        Comision comisionActualizada = new Comision(1L, 20L, 15.0, 300.0, 45.0, LocalDate.now());
        when(comisionRepository.findById(1L)).thenReturn(Optional.of(comision));
        when(comisionRepository.save(comision)).thenReturn(comisionActualizada);

        // When
        Comision resultado = comisionService.actualizarComision(1L, comisionActualizada);

        // Then
        assertEquals(20L, resultado.getPagoId());
        assertEquals(15.0, resultado.getTasa());
        verify(comisionRepository, times(1)).findById(1L);
        verify(comisionRepository, times(1)).save(comision);
    }

    @Test
    @DisplayName("Debería lanzar excepción al buscar comisión inexistente")
    void deberiaLanzarExcepcionAlBuscarComisionInexistente() {
        // Given
        when(comisionRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> comisionService.obtenerComisionPorId(99L));
    }
}
