package ar.edu.huergo.fastbid.controller.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.huergo.fastbid.dto.SubastaForm;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;
import ar.edu.huergo.fastbid.service.ProductoService;
import ar.edu.huergo.fastbid.service.SubastaService;
import ar.edu.huergo.fastbid.service.security.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/subastas")
@RequiredArgsConstructor
public class SubastaWebController {

    private final SubastaService subastaService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String redirigirListado() {
        return "redirect:/subastas/mis";
    }

    @GetMapping("/mis")
    public String verMisSubastas(Model model, Authentication authentication) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado(authentication);
            model.addAttribute("subastas", subastaService.obtenerSubastasPorUsuario(usuario));
            return "subastas/ver-subastas";
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }
    }

    @GetMapping("/ver")
    public String verSubastas(Model model, Authentication authentication) {
        return verMisSubastas(model, authentication);
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model, Authentication authentication) {
        Usuario usuario;
        try {
            usuario = obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("subastaForm")) {
            SubastaForm formulario = new SubastaForm();
            LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
            formulario.setFechaInicio(ahora);
            formulario.setFechaFin(ahora.plusDays(7));
            formulario.setIncrementoMinimo(100.0);
            formulario.setPrecioInicial(100.0);
            model.addAttribute("subastaForm", formulario);
        }

        SubastaForm formulario = (SubastaForm) model.asMap().get("subastaForm");
        prepararProductosParaFormulario(model, usuario, formulario);
        return "subastas/crear-subasta";
    }

    @PostMapping("/crear")
    public String crearSubasta(@Valid @ModelAttribute("subastaForm") SubastaForm subastaForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario;
        try {
            usuario = obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        Producto producto = null;
        if (subastaForm.getProductoId() != null) {
            producto = productoService.obteneProductoPorId(subastaForm.getProductoId());
            if (producto == null || producto.getUsuario() == null
                    || !producto.getUsuario().getId().equals(usuario.getId())) {
                bindingResult.rejectValue("productoId", "producto.invalido",
                        "Seleccioná uno de tus productos disponibles");
            } else if (producto.getSubasta() != null || subastaService.existeSubastaParaProducto(producto)) {
                bindingResult.rejectValue("productoId", "producto.conSubasta",
                        "El producto ya tiene una subasta activa");
            }
        }

        if (subastaForm.getFechaInicio() != null && subastaForm.getFechaFin() != null
                && !subastaForm.getFechaFin().isAfter(subastaForm.getFechaInicio())) {
            bindingResult.rejectValue("fechaFin", "fechaFin.invalida",
                    "La fecha de fin debe ser posterior a la fecha de inicio");
        }

        if (subastaForm.getCompraInmediata() != null && subastaForm.getPrecioInicial() != null
                && subastaForm.getCompraInmediata() <= subastaForm.getPrecioInicial()) {
            bindingResult.rejectValue("compraInmediata", "compraInmediata.menor",
                    "La compra inmediata debe ser mayor al precio inicial");
        }

        if (bindingResult.hasErrors()) {
            prepararProductosParaFormulario(model, usuario, subastaForm);
            return "subastas/crear-subasta";
        }

        Subasta subasta = new Subasta();
        subasta.setProducto(producto);
        subasta.setFechaInicio(subastaForm.getFechaInicio());
        subasta.setFechaFin(subastaForm.getFechaFin());
        subasta.setPrecioInicial(subastaForm.getPrecioInicial());
        subasta.setPrecioActual(subastaForm.getPrecioInicial());
        subasta.setIncrementoMinimo(subastaForm.getIncrementoMinimo());
        subasta.setCompraInmediata(subastaForm.getCompraInmediata());
        subasta.setEstado(subastaForm.getFechaInicio().isAfter(LocalDateTime.now())
                ? SubastaEstado.PROGRAMADA
                : SubastaEstado.ACTIVA);
        producto.setSubasta(subasta);

        subastaService.crearSubasta(subasta);

        redirectAttributes.addFlashAttribute("success", "Subasta creada correctamente.");
        return "redirect:/subastas/mis";
    }

    @GetMapping("/eliminar")
    public String mostrarFormularioEliminar(Model model, Authentication authentication) {
        Usuario usuario;
        try {
            usuario = obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        model.addAttribute("subastas", subastaService.obtenerSubastasPorUsuario(usuario));
        return "subastas/eliminar-subasta";
    }

    @PostMapping("/eliminar")
    public String eliminarSubastas(@RequestParam(name = "ids", required = false) List<Long> ids,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario;
        try {
            usuario = obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selecciona al menos una subasta para eliminar.");
            return "redirect:/subastas/eliminar";
        }

        int eliminadas = subastaService.eliminarSubastasDeUsuario(ids, usuario);
        if (eliminadas == 0) {
            redirectAttributes.addFlashAttribute("error", "No se pudieron eliminar las subastas seleccionadas.");
            return "redirect:/subastas/eliminar";
        }

        String mensaje = eliminadas == 1 ? "Se eliminó 1 subasta." : "Se eliminaron " + eliminadas + " subastas.";
        redirectAttributes.addFlashAttribute("success", mensaje);
        return "redirect:/subastas/mis";
    }

    private void prepararProductosParaFormulario(Model model, Usuario usuario, SubastaForm formulario) {
        List<Producto> productos = productoService.obtenerProductosSinSubastaPorUsuario(usuario);
        model.addAttribute("productosDisponibles", productos);
        model.addAttribute("cantidadProductos", productos.size());
        model.addAttribute("productoSeleccionadoNombre", null);

        if (formulario != null && formulario.getProductoId() != null) {
            Optional<Producto> seleccionado = productos.stream()
                    .filter(prod -> formulario.getProductoId().equals(prod.getIdProducto()))
                    .findFirst();
            seleccionado.ifPresent(producto -> model.addAttribute("productoSeleccionadoNombre", producto.getNombre()));
        }
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        if (authentication.getPrincipal() instanceof String principal && "anonymousUser".equals(principal)) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        return usuarioService.buscarPorUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
