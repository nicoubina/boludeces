package ar.edu.huergo.lcarera.restaurante.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.huergo.lcarera.restaurante.entity.security.Rol;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;
import ar.edu.huergo.lcarera.restaurante.repository.security.RolRepository;
import ar.edu.huergo.lcarera.restaurante.repository.security.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        return args -> {
            Rol admin = rolRepository.findByNombre("ADMIN").orElseGet(() -> rolRepository.save(new Rol("ADMIN")));
            Rol cliente = rolRepository.findByNombre("CLIENTE").orElseGet(() -> rolRepository.save(new Rol("CLIENTE")));

            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario u = new Usuario("admin", encoder.encode("admin123"));
                u.setRoles(Set.of(admin));
                usuarioRepository.save(u);
            }

            if (usuarioRepository.findByUsername("cliente").isEmpty()) {
                Usuario u = new Usuario("cliente", encoder.encode("cliente123"));
                u.setRoles(Set.of(cliente));
                usuarioRepository.save(u);
            }
        };
    }
}


