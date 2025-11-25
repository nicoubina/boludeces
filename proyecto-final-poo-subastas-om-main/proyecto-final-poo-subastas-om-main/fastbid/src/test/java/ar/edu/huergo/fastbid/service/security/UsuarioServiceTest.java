package ar.edu.huergo.fastbid.service.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ar.edu.huergo.fastbid.entity.security.Rol;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.repository.security.RolRepository;
import ar.edu.huergo.fastbid.repository.security.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;
    private Rol rolCliente;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setUsername("usuario@test.com");
        usuarioEjemplo.setPassword("password123");

        rolCliente = new Rol();
        // si tu entidad no tiene setId, no pasa nada: no lo usamos en las aserciones
        rolCliente.setNombre("CLIENTE");
    }

    @Test
    @DisplayName("Debería obtener todos los usuarios")
    void deberiaObtenerTodosLosUsuarios() {
        // Given
        List<Usuario> usuariosEsperados = Arrays.asList(usuarioEjemplo);
        when(usuarioRepository.findAll()).thenReturn(usuariosEsperados);

        // When
        List<Usuario> resultado = usuarioService.getAllUsuarios();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuarioEjemplo.getUsername(), resultado.get(0).getUsername());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería registrar usuario correctamente")
    void deberiaRegistrarUsuarioCorrectamente() {
        // Given
        String password = "password123";
        String verificacionPassword = "password123";
        String passwordEncriptado = "encrypted_password";

        when(usuarioRepository.existsByUsername(usuarioEjemplo.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(passwordEncriptado);
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        // When
        Usuario resultado = usuarioService.registrar(usuarioEjemplo, password, verificacionPassword);

        // Then
        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).existsByUsername(usuarioEjemplo.getUsername());
        verify(passwordEncoder, times(1)).encode(password);
        verify(rolRepository, times(1)).findByNombre("CLIENTE");
        verify(usuarioRepository, times(1)).save(usuarioEjemplo);

        // Verificar que la contraseña fue encriptada y el rol asignado
        assertEquals(passwordEncriptado, usuarioEjemplo.getPassword());
        assertTrue(usuarioEjemplo.getRoles().contains(rolCliente));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando las contraseñas no coinciden")
    void deberiaLanzarExcepcionCuandoContraseniasNoCoinciden() {
        // Given
        String password = "password123";
        String verificacionPassword = "password456";

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(usuarioEjemplo, password, verificacionPassword));

        assertEquals("Las contraseñas no coinciden", ex.getMessage());
        verify(usuarioRepository, never()).existsByUsername(any());
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el username ya existe")
    void deberiaLanzarExcepcionCuandoUsernameYaExiste() {
        // Given
        String password = "password123";
        String verificacionPassword = "password123";
        when(usuarioRepository.existsByUsername(usuarioEjemplo.getUsername())).thenReturn(true);

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(usuarioEjemplo, password, verificacionPassword));

        assertEquals("El nombre de usuario ya está en uso", ex.getMessage());
        verify(usuarioRepository, times(1)).existsByUsername(usuarioEjemplo.getUsername());
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no encuentra el rol CLIENTE")
    void deberiaLanzarExcepcionCuandoNoEncuentraRolCliente() {
        // Given
        String password = "password123";
        String verificacionPassword = "password123";

        when(usuarioRepository.existsByUsername(usuarioEjemplo.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encrypted_password");
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(usuarioEjemplo, password, verificacionPassword));

        assertEquals("Rol 'CLIENTE' no encontrado", ex.getMessage());
        verify(usuarioRepository, times(1)).existsByUsername(usuarioEjemplo.getUsername());
        verify(passwordEncoder, times(1)).encode(password);
        verify(rolRepository, times(1)).findByNombre("CLIENTE");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería manejar contraseñas vacías correctamente (no coinciden)")
    void deberiaManejarContraseniasVacias() {
        // Given
        String passwordVacio = "";
        String verificacionPasswordDiferente = "diferente";

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(usuarioEjemplo, passwordVacio, verificacionPasswordDiferente));

        assertEquals("Las contraseñas no coinciden", ex.getMessage());
    }

    @Test
    @DisplayName("Debería manejar contraseñas null correctamente")
    void deberiaManejarContraseniasNull() {
        // Given
        String passwordNull = null;
        String verificacionPasswordNull = null;

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(usuarioEjemplo, passwordNull, verificacionPasswordNull));

        assertEquals("Las contraseñas no pueden ser null", ex.getMessage());
    }
}
