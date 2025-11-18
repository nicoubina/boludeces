package ar.edu.huergo.clickservice.buscadorservicios.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.huergo.clickservice.buscadorservicios.repository.security.UsuarioRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configuración de seguridad para rutas WEB (/web/**)
     * Usa autenticación basada en sesiones (form login)
     */
    @Bean
    @Order(1)
    SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/web/**")
            .csrf(csrf -> csrf.disable()) // Puedes habilitarlo si lo necesitas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas web
                .requestMatchers("/web/", "/web/login", "/web/registro", 
                                "/web/registro-profesional", "/web/acerca", 
                                "/web/servicios").permitAll()
                
                // Dashboard requiere autenticación
                .requestMatchers("/web/dashboard").authenticated()
                
                // Cualquier otra ruta web requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/web/login")
                .loginProcessingUrl("/web/login")
                .defaultSuccessUrl("/web/dashboard", true)
                .failureUrl("/web/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/web/logout")
                .logoutSuccessUrl("/web/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
            
        return http.build();
    }

    /**
     * Configuración de seguridad para API REST (/api/**)
     * Usa autenticación JWT (stateless)
     */
    @Bean
    @Order(2)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas API - no requieren autenticación
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar-profesional").permitAll()
                
                // Rutas de servicios - configuración por método HTTP y rol
                .requestMatchers(HttpMethod.GET, "/api/servicios").hasAnyRole("ADMIN", "CLIENTE", "PROFESIONAL")
                .requestMatchers(HttpMethod.GET, "/api/servicios/**").hasAnyRole("ADMIN", "CLIENTE", "PROFESIONAL")
                .requestMatchers(HttpMethod.POST, "/api/servicios").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/servicios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/servicios/**").hasRole("ADMIN")
                
                // Rutas de profesionales
                .requestMatchers(HttpMethod.GET, "/api/profesionales").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/profesionales/**").hasAnyRole("ADMIN", "CLIENTE", "PROFESIONAL")
                .requestMatchers(HttpMethod.POST, "/api/profesionales").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/profesionales/**").hasAnyRole("ADMIN", "PROFESIONAL")
                .requestMatchers(HttpMethod.DELETE, "/api/profesionales/**").hasRole("ADMIN")
                
                // Rutas de solicitudes
                .requestMatchers(HttpMethod.GET, "/api/solicitudes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/**").hasAnyRole("ADMIN", "CLIENTE", "PROFESIONAL")
                .requestMatchers(HttpMethod.POST, "/api/solicitudes").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes/**").hasAnyRole("ADMIN", "CLIENTE", "PROFESIONAL")
                .requestMatchers(HttpMethod.DELETE, "/api/solicitudes/**").hasRole("ADMIN")
                
                // Rutas de usuarios - solo ADMIN puede ver todos los usuarios
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                
                // Cualquier otra ruta API requiere autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
            )
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);
                        
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(java.util.Map.of(
                    "type", "https://http.dev/problems/access-denied", 
                    "title", "Acceso denegado", 
                    "status", 403, 
                    "detail", "No tienes permisos para acceder a este recurso"));

            response.getWriter().write(jsonResponse);
        };
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(java.util.Map.of(
                    "type", "https://http.dev/problems/unauthorized", 
                    "title", "No autorizado", 
                    "status", 401, 
                    "detail", "Credenciales inválidas o faltantes"));

            response.getWriter().write(jsonResponse);
        };
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByUsername(username)
                .map(usuario -> org.springframework.security.core.userdetails.User
                        .withUsername(usuario.getUsername())
                        .password(usuario.getPassword())
                        .roles(usuario.getRoles().stream()
                                .map(r -> r.getNombre())
                                .toArray(String[]::new))
                        .build())
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}