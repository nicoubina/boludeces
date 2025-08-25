package ar.edu.huergo.lcarera.restaurante.service.security;

import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.huergo.lcarera.restaurante.entity.security.Rol;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;
import ar.edu.huergo.lcarera.restaurante.repository.security.RolRepository;
import ar.edu.huergo.lcarera.restaurante.repository.security.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario registrar(Usuario usuario, String password, String verificacionPassword) {
        if (!password.equals(verificacionPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        usuario.setPassword(passwordEncoder.encode(password));
        Rol rolCliente = rolRepository.findByNombre("CLIENTE").orElseThrow(() -> new IllegalArgumentException("Rol 'CLIENTE' no encontrado"));
        usuario.setRoles(Set.of(rolCliente));
        return usuarioRepository.save(usuario);
    }
}
