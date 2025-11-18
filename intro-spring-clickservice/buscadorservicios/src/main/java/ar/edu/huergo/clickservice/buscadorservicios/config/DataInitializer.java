package ar.edu.huergo.clickservice.buscadorservicios.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Rol;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ServicioRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.security.RolRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.security.UsuarioRepository;
import ar.edu.huergo.clickservice.buscadorservicios.util.PasswordValidator;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RolRepository rolRepository, UsuarioRepository usuarioRepository, 
                              PasswordEncoder encoder, ServicioRepository servicioRepository) {
        return args -> {
            // Crear roles
            Rol admin = rolRepository.findByNombre("ADMIN").orElseGet(() -> rolRepository.save(new Rol("ADMIN")));
            Rol cliente = rolRepository.findByNombre("CLIENTE").orElseGet(() -> rolRepository.save(new Rol("CLIENTE")));
            Rol profesional = rolRepository.findByNombre("PROFESIONAL").orElseGet(() -> rolRepository.save(new Rol("PROFESIONAL")));

            // Crear servicios de ejemplo
            if (servicioRepository.count() == 0) {
                servicioRepository.save(new Servicio(null, "Plomería", 2500.0));
                servicioRepository.save(new Servicio(null, "Electricidad", 3000.0));
                servicioRepository.save(new Servicio(null, "Jardinería", 1800.0));
                servicioRepository.save(new Servicio(null, "Carpintería", 2800.0));
                servicioRepository.save(new Servicio(null, "Pintura", 2200.0));
                servicioRepository.save(new Servicio(null, "Albañilería", 3200.0));
            }

            // Crear usuario administrador
            if (usuarioRepository.findByUsername("admin@clickservice.edu.ar").isEmpty()) {
                String adminPassword = "AdminSuperSegura@123";
                PasswordValidator.validate(adminPassword);
                Usuario u = new Usuario("admin@clickservice.edu.ar", encoder.encode(adminPassword));
                u.setNombre("Admin");
                u.setApellido("ClickService");
                u.setDni("10000000");
                u.setTelefono("+54 11 4000-0000");
                u.setCalle("Av. Central");
                u.setAltura(100);
                u.setRoles(Set.of(admin));
                usuarioRepository.save(u);
            }

            // Crear usuario cliente
            if (usuarioRepository.findByUsername("cliente@clickservice.edu.ar").isEmpty()) {
                String clientePassword = "ClienteSuperSegura@123";
                PasswordValidator.validate(clientePassword);
                Usuario u = new Usuario("cliente@clickservice.edu.ar", encoder.encode(clientePassword));
                u.setNombre("Carla");
                u.setApellido("Cliente");
                u.setDni("20000000");
                u.setTelefono("+54 11 4111-1111");
                u.setCalle("Calle Cliente");
                u.setAltura(200);
                u.setRoles(Set.of(cliente));
                usuarioRepository.save(u);
            }

            // Crear usuario profesional
            if (usuarioRepository.findByUsername("profesional@clickservice.edu.ar").isEmpty()) {
                String profesionalPassword = "ProfesionalSegura@123";
                PasswordValidator.validate(profesionalPassword);
                Usuario u = new Usuario("profesional@clickservice.edu.ar", encoder.encode(profesionalPassword));
                u.setNombre("Pablo");
                u.setApellido("Profesional");
                u.setDni("30000000");
                u.setTelefono("+54 11 4222-2222");
                u.setCalle("Calle Profesional");
                u.setAltura(300);
                u.setRoles(Set.of(profesional));
                usuarioRepository.save(u);
            }
        };
    }
}