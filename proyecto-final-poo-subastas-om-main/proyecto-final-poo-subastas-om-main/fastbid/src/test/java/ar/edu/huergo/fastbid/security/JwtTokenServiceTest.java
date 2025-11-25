package ar.edu.huergo.fastbid.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ar.edu.huergo.fastbid.service.security.JwtTokenService;

/**
 * Tests de seguridad para JwtTokenService (FastBid)
 *
 * Demuestra:
 * 1) Testing de servicios de seguridad
 * 2) Mocking de UserDetails
 * 3) Generación/validación de JWT
 * 4) Expiración y manejo de tokens inválidos
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Tests de Seguridad - JwtTokenService")
class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private UserDetails userDetails;

    private static final String SECRET_KEY =
            "mi-clave-secreta-para-jwt-que-debe-ser-lo-suficientemente-larga-para-ser-segura";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hora

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @BeforeEach
    void setUp() {
        // Instanciamos el servicio con el mismo constructor que usás en producción
        jwtTokenService = new JwtTokenService(SECRET_KEY, EXPIRATION_MS);

        // Mock de usuario
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("usuario@test.com");
        when(userDetails.getAuthorities()).thenReturn(
                (Collection) Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_CLIENTE"),
                        new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("Debería generar token JWT válido")
    void deberiaGenerarTokenJwtValido() {
        List<String> roles = Arrays.asList("ROLE_CLIENTE", "ROLE_USER");

        String token = jwtTokenService.generarToken(userDetails, roles);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."), "El token JWT debería tener formato con puntos");
        assertEquals(3, token.split("\\.").length, "El token JWT debería tener 3 partes");
    }

    @Test
    @DisplayName("Debería extraer username del token correctamente")
    void deberiaExtraerUsernameDelToken() {
        List<String> roles = Arrays.asList("ROLE_CLIENTE");
        String token = jwtTokenService.generarToken(userDetails, roles);

        String usernameExtraido = jwtTokenService.extraerUsername(token);

        assertEquals("usuario@test.com", usernameExtraido);
    }

    @Test
    @DisplayName("Debería validar token correctamente para usuario válido")
    void deberiaValidarTokenCorrectamente() {
        List<String> roles = Arrays.asList("ROLE_CLIENTE");
        String token = jwtTokenService.generarToken(userDetails, roles);

        boolean esValido = jwtTokenService.esTokenValido(token, userDetails);

        assertTrue(esValido);
    }

    @Test
    @DisplayName("Debería rechazar token para usuario diferente")
    void deberiaRechazarTokenParaUsuarioDiferente() {
        List<String> roles = Arrays.asList("ROLE_CLIENTE");
        String token = jwtTokenService.generarToken(userDetails, roles);

        UserDetails otroUsuario = mock(UserDetails.class);
        when(otroUsuario.getUsername()).thenReturn("otro@test.com");

        boolean esValido = jwtTokenService.esTokenValido(token, otroUsuario);

        assertFalse(esValido);
    }

    @Test
    @DisplayName("Debería rechazar token malformado")
    void deberiaRechazarTokenMalformado() {
        String tokenMalformado = "token.malformado.invalido";

        assertThrows(Exception.class, () -> jwtTokenService.extraerUsername(tokenMalformado));
    }

    @Test
    @DisplayName("Debería rechazar token vacío")
    void deberiaRechazarTokenVacio() {
        String tokenVacio = "";

        assertThrows(Exception.class, () -> jwtTokenService.extraerUsername(tokenVacio));
    }

    @Test
    @DisplayName("Debería rechazar token null")
    void deberiaRechazarTokenNull() {
        String tokenNull = null;

        assertThrows(Exception.class, () -> jwtTokenService.extraerUsername(tokenNull));
    }

    @Test
    @DisplayName("Debería generar tokens diferentes para llamadas consecutivas")
    void deberiaGenerarTokensDiferentesParaLlamadasConsecutivas() throws InterruptedException {
        List<String> roles = Arrays.asList("ROLE_CLIENTE");

        String token1 = jwtTokenService.generarToken(userDetails, roles);
        Thread.sleep(1000); // asegurar distinto iat/exp
        String token2 = jwtTokenService.generarToken(userDetails, roles);

        assertNotEquals(token1, token2, "Los tokens deberían ser diferentes");
    }

    @Test
    @DisplayName("Debería manejar roles vacíos")
    void deberiaManejarRolesVacios() {
        List<String> rolesVacios = Arrays.asList();

        String token = jwtTokenService.generarToken(userDetails, rolesVacios);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String usernameExtraido = jwtTokenService.extraerUsername(token);
        assertEquals("usuario@test.com", usernameExtraido);
    }

    @Test
    @DisplayName("Debería manejar múltiples roles")
    void deberiaManejarMultiplesRoles() {
        List<String> multiplesRoles = Arrays.asList("ROLE_CLIENTE", "ROLE_ADMIN", "ROLE_MODERADOR");

        String token = jwtTokenService.generarToken(userDetails, multiplesRoles);
        String usernameExtraido = jwtTokenService.extraerUsername(token);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("usuario@test.com", usernameExtraido);
        assertTrue(jwtTokenService.esTokenValido(token, userDetails));
    }

    @Test
    @DisplayName("Debería rechazar token expirado")
    void deberiaRechazarTokenExpirado() throws InterruptedException {
        JwtTokenService servicioConExpiracionCorta = new JwtTokenService(SECRET_KEY, 1L);
        List<String> roles = Arrays.asList("ROLE_CLIENTE");

        String token = servicioConExpiracionCorta.generarToken(userDetails, roles);

        Thread.sleep(10); // esperar a que expire

        assertThrows(Exception.class, () -> servicioConExpiracionCorta.extraerUsername(token));
        assertFalse(servicioConExpiracionCorta.esTokenValido(token, userDetails));
    }

    @Test
    @DisplayName("Debería validar token dentro del tiempo de expiración")
    void deberiaValidarTokenDentroDelTiempoDeExpiracion() {
        List<String> roles = Arrays.asList("ROLE_CLIENTE");
        String token = jwtTokenService.generarToken(userDetails, roles);

        boolean esValido = jwtTokenService.esTokenValido(token, userDetails);

        assertTrue(esValido);
    }

    @Test
    @DisplayName("Debería manejar usernames con caracteres especiales")
    void deberiaManejarUsernamesConCaracteresEspeciales() {
        UserDetails usuarioEspecial = mock(UserDetails.class);
        when(usuarioEspecial.getUsername()).thenReturn("usuario.especial+test@dominio-test.com");

        List<String> roles = Arrays.asList("ROLE_CLIENTE");

        String token = jwtTokenService.generarToken(usuarioEspecial, roles);
        String usernameExtraido = jwtTokenService.extraerUsername(token);

        assertEquals("usuario.especial+test@dominio-test.com", usernameExtraido);
        assertTrue(jwtTokenService.esTokenValido(token, usuarioEspecial));
    }
}
