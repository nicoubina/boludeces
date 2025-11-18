package ar.edu.huergo.clickservice.buscadorservicios.service.profesional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Reseña;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ProfesionalRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ReseñaRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.security.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ReseñaServiceTest {

    @Mock
    private ReseñaRepository reseñaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProfesionalRepository profesionalRepository;

    @InjectMocks
    private ReseñaService reseñaService;

    private Profesional profesional;
    private Usuario usuario;
    private Reseña reseña;

    @BeforeEach
    void setUp() {
        profesional = new Profesional();
        profesional.setId(5L);
        usuario = new Usuario();
        usuario.setId(3L);
        usuario.setUsername("cliente@example.com");
        usuario.setPassword("contraseña_segura_para_cliente");

        reseña = new Reseña();
        reseña.setId(1L);
        reseña.setOrdenId(100L);
        reseña.setRating(5);
        reseña.setComentario("Servicio excelente y rápido");
        reseña.setFecha(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debería obtener reseñas por profesional")
    void deberiaObtenerReseñasPorProfesional() {
        // Given
        when(reseñaRepository.findByProfesionalId(5L)).thenReturn(List.of(reseña));

        // When
        List<Reseña> resultado = reseñaService.obtenerReseñasPorProfesional(5L);

        // Then
        assertEquals(1, resultado.size());
        verify(reseñaRepository, times(1)).findByProfesionalId(5L);
    }

    @Test
    @DisplayName("Debería crear una reseña con datos válidos")
    void deberiaCrearUnaReseñaConDatosValidos() {
        // Given
        when(reseñaRepository.existsByOrdenId(100L)).thenReturn(false);
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));
        when(profesionalRepository.findById(5L)).thenReturn(Optional.of(profesional));
        when(reseñaRepository.save(reseña)).thenReturn(reseña);

        // When
        Reseña resultado = reseñaService.crearReseña(reseña, 3L, 5L);

        // Then
        assertNotNull(resultado.getUsuario());
        assertNotNull(resultado.getProfesional());
        verify(reseñaRepository, times(1)).save(reseña);
    }

    @Test
    @DisplayName("Debería lanzar error al crear reseña duplicada")
    void deberiaLanzarErrorAlCrearReseñaDuplicada() {
        // Given
        when(reseñaRepository.existsByOrdenId(100L)).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> reseñaService.crearReseña(reseña, 3L, 5L));
    }

    @Test
    @DisplayName("Debería obtener reseña por orden existente")
    void deberiaObtenerReseñaPorOrdenExistente() {
        // Given
        when(reseñaRepository.findByOrdenId(100L)).thenReturn(Optional.of(reseña));

        // When
        Reseña resultado = reseñaService.obtenerReseñaPorOrden(100L);

        // Then
        assertEquals(reseña.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Debería lanzar excepción al buscar reseña inexistente")
    void deberiaLanzarExcepcionAlBuscarReseñaInexistente() {
        // Given
        when(reseñaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> reseñaService.obtenerReseñaPorId(99L));
    }
}