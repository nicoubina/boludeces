package ar.edu.huergo.fastbid.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.service.security.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("") // raíz web
public class FastbidWebController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Página de login (form Thymeleaf).
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña inválidos.");
        }
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada correctamente.");
        }
        return "auth/login";
    }

    /**
     * Página de registro
     */
    @GetMapping("/registrar")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registrar";
    }
    /**
     * Procesar registro de usuario
     */
    @PostMapping("/registrar")
    public String procesarRegistro(@Valid @ModelAttribute Usuario usuario,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/registrar";
        }

        try {
            usuarioService.registrarUsuario(usuario.getUsername(), usuario.getUsername(), usuario.getPassword());
            redirectAttributes.addFlashAttribute("success", "Usuario registrado exitosamente. Puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "redirect:/registrar";
        }
    }

    /**
     * Inicio: requiere autenticación (configurado en SecurityConfig).
     * Inyecta el nombre del usuario en el modelo.
     * Templates esperados:
     *   templates/index.html
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
        if (authentication != null) {
            model.addAttribute("isAuth", true);
        } else {
            model.addAttribute("isAuth", false);
        }
        return "index";
    }

    /**
     * Logout por GET (opcional).
     * Útil si querés un enlace <a href="/logout">Salir</a>.
     * Si preferís el POST nativo de Spring (/logout), podés quitar este método.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();                                             
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?logout";
    }
}
