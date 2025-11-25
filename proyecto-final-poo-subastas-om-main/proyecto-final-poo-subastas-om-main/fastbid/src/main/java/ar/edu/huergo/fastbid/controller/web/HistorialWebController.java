package ar.edu.huergo.fastbid.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.service.HistorialService;
import ar.edu.huergo.fastbid.service.security.UsuarioService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialWebController {

    private final HistorialService historialService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String verHistorial(Model model, Authentication authentication) {
        Usuario usuario;
        try {
            usuario = obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("historiales", historialService.obtenerHistorialPorUsuario(usuario));
        return "historial/historial";
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        if (authentication.getPrincipal() instanceof String principal
                && "anonymousUser".equals(principal)) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        return usuarioService.buscarPorUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
