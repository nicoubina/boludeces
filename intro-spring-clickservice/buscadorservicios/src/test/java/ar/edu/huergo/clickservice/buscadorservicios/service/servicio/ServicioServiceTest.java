package ar.edu.huergo.clickservice.buscadorservicios.service.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ServicioRepository;
import ar.edu.huergo.clickservice.buscadorservicios.service.servicio.ServicioService;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioService servicioService;

    private Servicio servicioEjemplo;

    @BeforeEach
    void setUp() {
        servicioEjemplo = new Servicio();
        servicioEjemplo.setId(1L);
        servicioEjemplo.setNombre("Plomería");
        servicioEjemplo.setPrecioHora(25.50);
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener todos los servicios")
    void deberiaManejarListaVaciaAlObtenerTodosLosServicios() {
        // Given
        when(servicioRepository.findAll()).thenReturn(Arrays.<Servicio>asList());

        // When
        List<Servicio> resultado = servicioService.obtenerTodosLosServicios();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(servicioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar múltiples servicios correctamente")
    void deberiaManejarMultiplesServiciosCorrectamente() {
        // Given
        Servicio servicio2 = new Servicio();
        servicio2.setId(2L);
        servicio2.setNombre("Electricidad");
        servicio2.setPrecioHora(30.00);

        Servicio servicio3 = new Servicio();
        servicio3.setId(3L);
        servicio3.setNombre("Carpintería");
        servicio3.setPrecioHora(20.75);

        List<Servicio> serviciosEsperados = Arrays.asList(servicioEjemplo, servicio2, servicio3);
        when(servicioRepository.findAll()).thenReturn(serviciosEsperados);

        // When
        List<Servicio> resultado = servicioService.obtenerTodosLosServicios();

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Plomería", resultado.get(0).getNombre());
        assertEquals("Electricidad", resultado.get(1).getNombre());
        assertEquals("Carpintería", resultado.get(2).getNombre());
        verify(servicioRepository, times(1)).findAll();
    }
}