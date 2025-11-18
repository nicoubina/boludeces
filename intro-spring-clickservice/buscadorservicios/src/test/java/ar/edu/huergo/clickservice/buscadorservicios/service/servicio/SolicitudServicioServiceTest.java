package ar.edu.huergo.clickservice.buscadorservicios.service.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio.EstadoSolicitud;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.SolicitudServicioRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class SolicitudServicioServiceTest {

    @Mock
    private SolicitudServicioRepository solicitudServicioRepository;

    @InjectMocks
    private SolicitudServicioService solicitudServicioService;

    private SolicitudServicio solicitud;

    @BeforeEach
    void setUp() {
        solicitud = new SolicitudServicio();
        solicitud.setId(1L);
        solicitud.setServicio(new Servicio(1L, "Plomería", 25.0));
        solicitud.setCliente(crearUsuarioBasico());
        solicitud.setDescripcionProblema("Fuga de agua en la cocina");
        solicitud.setDireccionServicio("Calle Falsa 123");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(2));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(15000.0);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener todas las solicitudes")
    void deberiaManejarListaVaciaAlObtenerTodasLasSolicitudes() {
        // Given
        when(solicitudServicioRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<SolicitudServicio> resultado = solicitudServicioService.obtenerTodasLasSolicitudes();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(solicitudServicioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería cambiar el estado de una solicitud existente")
    void deberiaCambiarElEstadoDeUnaSolicitudExistente() {
        // Given
        when(solicitudServicioRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudServicioRepository.save(solicitud)).thenReturn(solicitud);

        // When
        SolicitudServicio resultado =
                solicitudServicioService.cambiarEstadoSolicitud(1L, EstadoSolicitud.ASIGNADA);

        // Then
        assertEquals(EstadoSolicitud.ASIGNADA, resultado.getEstado());
        verify(solicitudServicioRepository, times(1)).save(solicitud);
    }

    @Test
    @DisplayName("Debería obtener solicitudes por cliente")
    void deberiaObtenerSolicitudesPorCliente() {
        // Given
        when(solicitudServicioRepository.findByClienteId(5L)).thenReturn(List.of(solicitud));

        // When
        List<SolicitudServicio> resultado = solicitudServicioService.obtenerSolicitudesPorCliente(5L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(EstadoSolicitud.PENDIENTE, resultado.get(0).getEstado());
        verify(solicitudServicioRepository, times(1)).findByClienteId(5L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al buscar solicitud inexistente")
    void deberiaLanzarExcepcionAlBuscarSolicitudInexistente() {
        // Given
        when(solicitudServicioRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> solicitudServicioService.obtenerSolicitudPorId(99L));
    }

    private Usuario crearUsuarioBasico() {
        Usuario usuario = new Usuario();
        usuario.setId(5L);
        usuario.setNombre("Ana");
        usuario.setApellido("Pérez");
        usuario.setDni("32123456");
        usuario.setTelefono("+54 9 11 1111-2222");
        usuario.setCalle("Calle Principal");
        usuario.setAltura(456);
        usuario.setUsername("ana@example.com");
        usuario.setPassword("contraseña_segura_para_ana");
        return usuario;
    }
}