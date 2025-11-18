package ar.edu.huergo.clickservice.buscadorservicios.controller.web;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.service.servicio.ServicioService;
import ar.edu.huergo.clickservice.buscadorservicios.service.security.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador web para manejar las vistas de ClickService usando Thymeleaf
 * Este controlador sirve páginas HTML en lugar de respuestas JSON
 */
@Controller
@RequestMapping("/web")
public class BuscadorServiciosWebController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ServicioService servicioService;

    /**
     * Página principal de ClickService - muestra todos los servicios
     */
    @GetMapping({"", "/"})
    public String home(Model model) {
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        model.addAttribute("servicios", servicios);
        model.addAttribute("titulo", "Servicios Disponibles");
        return "auth/index";
    }

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión correctamente");
        }
        model.addAttribute("titulo", "Iniciar Sesión");
        return "auth/login";
    }

    /**
     * Página de registro para clientes
     */
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("titulo", "Registro de Cliente");
        return "auth/registro"; 
    }

    /**
     * Procesar registro de cliente
     */
    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam("nombre") String nombre,
                                  @RequestParam("apellido") String apellido,
                                  @RequestParam("dni") String dni,
                                  @RequestParam("telefono") String telefono,
                                  @RequestParam("calle") String calle,
                                  @RequestParam("altura") String altura,
                                  @RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("verificacionPassword") String verificacionPassword,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Validar que las contraseñas coincidan
            if (!password.equals(verificacionPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/web/registro";
            }

            Integer alturaNumero;
            try {
                alturaNumero = Integer.parseInt(altura);
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("error", "La altura debe ser un número válido");
                return "redirect:/web/registro";
            }

            if (alturaNumero <= 0) {
                redirectAttributes.addFlashAttribute("error", "La altura debe ser un número positivo");
                return "redirect:/web/registro";
            }

            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDni(dni);
            usuario.setTelefono(telefono);
            usuario.setCalle(calle);
            usuario.setAltura(alturaNumero);
            usuario.setUsername(username);

            // Registrar usuario con rol CLIENTE
            usuarioService.registrar(usuario, password, verificacionPassword);
            
            redirectAttributes.addFlashAttribute("success", 
                "Usuario registrado exitosamente. Puedes iniciar sesión.");
            return "redirect:/web/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/registro";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al registrar usuario: " + e.getMessage());
            return "redirect:/web/registro";
        }
    }

    /**
     * Página de registro para profesionales
     */
    @GetMapping("/registro-profesional")
    public String registroProfesional(Model model) {
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        model.addAttribute("servicios", servicios);
        model.addAttribute("titulo", "Registro de Profesional");
        return "auth/registro-profesional";
    }

    /**
     * Procesar registro de profesional
     */
    @PostMapping("/registro-profesional")
    public String procesarRegistroProfesional(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("dni") String dni,
            @RequestParam("calle") String calle,
            @RequestParam("altura") String altura,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("telefono") String telefono,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "zonaTrabajo", required = false) String zonaTrabajo,
            @RequestParam("serviciosIds") Set<Long> serviciosIds,
            RedirectAttributes redirectAttributes) {

        try {
            Integer alturaNumero;
            try {
                alturaNumero = Integer.parseInt(altura);
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("error", "La altura debe ser un número válido");
                return "redirect:/web/registro-profesional";
            }

            if (alturaNumero <= 0) {
                redirectAttributes.addFlashAttribute("error", "La altura debe ser un número positivo");
                return "redirect:/web/registro-profesional";
            }

            // Crear DTO para registro de profesional
            ar.edu.huergo.clickservice.buscadorservicios.dto.security.RegistrarProfesionalDTO dto =
                new ar.edu.huergo.clickservice.buscadorservicios.dto.security.RegistrarProfesionalDTO();
            dto.setNombre(nombre);
            dto.setApellido(apellido);
            dto.setDni(dni);
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setCalle(calle);
            dto.setAltura(alturaNumero);
            dto.setNombreCompleto(nombreCompleto);
            dto.setTelefono(telefono);
            dto.setDescripcion(descripcion);
            dto.setZonaTrabajo(zonaTrabajo);
            dto.setServiciosIds(serviciosIds);

            // Registrar profesional
            usuarioService.registrarProfesional(dto);
            
            redirectAttributes.addFlashAttribute("success", 
                "Profesional registrado exitosamente. Puedes iniciar sesión.");
            return "redirect:/web/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/registro-profesional";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al registrar profesional: " + e.getMessage());
            return "redirect:/web/registro-profesional";
        }
    }

    /**
     * Logout
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/web/login?logout";
    }

    /**
     * Página de información de ClickService
     */
    @GetMapping("/acerca")
    public String acercaDe(Model model) {
        model.addAttribute("titulo", "Acerca de ClickService");
        model.addAttribute("nombre", "ClickService");
        model.addAttribute("descripcion", 
            "Una plataforma digital que conecta a usuarios que necesitan resolver tareas cotidianas con proveedores de servicios confiables.");
        return "acerca";
    }

    /**
     * Página para mostrar la lista de servicios
     */
    @GetMapping("/servicios")
    public String listarServicios(Model model) {
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        model.addAttribute("servicios", servicios);
        model.addAttribute("titulo", "Servicios Disponibles");
        return "servicios/lista";
    }

    /**
     * Dashboard del usuario (después de login)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            model.addAttribute("username", username);
            model.addAttribute("titulo", "Mi Panel");
            
            // Obtener roles del usuario
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isProfesional = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESIONAL"));
            boolean isCliente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
            
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isProfesional", isProfesional);
            model.addAttribute("isCliente", isCliente);
            
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar dashboard: " + e.getMessage());
            return "dashboard";
        }
    }
}