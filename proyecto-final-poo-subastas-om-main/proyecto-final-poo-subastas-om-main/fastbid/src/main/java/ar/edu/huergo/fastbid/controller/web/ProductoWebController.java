package ar.edu.huergo.fastbid.controller.web;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

import ar.edu.huergo.fastbid.dto.ProductoForm;
import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.service.CategoriaService;
import ar.edu.huergo.fastbid.service.ProductoService;
import ar.edu.huergo.fastbid.service.security.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoWebController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String redirigirListado() {
        return "redirect:/productos/mis";
    }

    @GetMapping("/mis")
    public String verMisProductos(Model model, Authentication authentication) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado(authentication);
            model.addAttribute("productos", productoService.obtenerProductosPorUsuario(usuario));
            return "productos/ver-productos";
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model, Authentication authentication) {
        try {
            obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("productoForm")) {
            ProductoForm formulario = new ProductoForm();
            formulario.setCantidad(1);
            formulario.setCondicion("NUEVO");
            formulario.setFechaFin(LocalDateTime.now().plusDays(7).withSecond(0).withNano(0));
            model.addAttribute("productoForm", formulario);
        }

        ProductoForm formulario = (ProductoForm) model.asMap().get("productoForm");
        prepararCategoriasParaFormulario(model, formulario);
        return "productos/crear-producto";
    }

    @PostMapping("/crear")
    public String crearProducto(@Valid @ModelAttribute("productoForm") ProductoForm productoForm,
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

        List<String> imagenes = Arrays.stream(productoForm.getImagenesTexto().split("[\\r\\n,]+"))
                .map(String::trim)
                .filter(texto -> !texto.isEmpty())
                .collect(Collectors.toList());

        if (imagenes.isEmpty()) {
            bindingResult.rejectValue("imagenesTexto", "imagenes.vacias", "Debes ingresar al menos una URL de imagen");
        }

        if (productoForm.getFechaFin() != null
                && productoForm.getFechaFin().isBefore(LocalDateTime.now())) {
            bindingResult.rejectValue("fechaFin", "fechaFin.pasada",
                    "La fecha de finalización debe ser posterior a la fecha actual");
        }

        if (productoForm.getPrecioCompraInmediata() != null
                && productoForm.getPrecioCompraInmediata() <= productoForm.getPrecioInicial()) {
            bindingResult.rejectValue("precioCompraInmediata", "precioCompraInmediata.menor",
                    "El precio de compra inmediata debe ser mayor al precio inicial");
        }

        Categoria categoria = null;
        if (productoForm.getCategoriaId() != null) {
            try {
                categoria = categoriaService.obtenerCategoriaPorId(productoForm.getCategoriaId());
            } catch (EntityNotFoundException ex) {
                bindingResult.rejectValue("categoriaId", "categoria.invalida", ex.getMessage());
            }
        }

        if (bindingResult.hasErrors()) {
            prepararCategoriasParaFormulario(model, productoForm);
            return "productos/crear-producto";
        }

        Producto producto = new Producto();
        producto.setNombre(productoForm.getNombre());
        producto.setDescripcion(productoForm.getDescripcion());
        producto.setPrecioInicial(productoForm.getPrecioInicial());
        producto.setImagenes(imagenes);
        producto.setCategoria(categoria);
        producto.setEstado("ACTIVO");
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setFechaFin(productoForm.getFechaFin());
        producto.setUsuario(usuario);
        producto.setPrecioCompraInmediata(productoForm.getPrecioCompraInmediata());
        producto.setCondicion(productoForm.getCondicion());
        producto.setUbicacion(productoForm.getUbicacion());
        producto.setCantidad(productoForm.getCantidad());

        productoService.crearProducto(producto);

        redirectAttributes.addFlashAttribute("success", "Producto creado correctamente.");
        return "redirect:/productos/mis";
    }

    @GetMapping("/eliminar")
    public String mostrarFormularioEliminar(Model model, Authentication authentication) {
        Usuario usuario;
        try {
            usuario = obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        model.addAttribute("productos", productoService.obtenerProductosPorUsuario(usuario));
        return "productos/eliminar-producto";
    }

    @PostMapping("/eliminar")
    public String eliminarProductos(@RequestParam(name = "ids", required = false) List<Long> ids,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario;
        try {
            usuario = obtenerUsuarioAutenticado(authentication);
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }

        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selecciona al menos un producto para eliminar.");
            return "redirect:/productos/eliminar";
        }

        int eliminados = productoService.eliminarProductosDeUsuario(ids, usuario);
        if (eliminados == 0) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudieron eliminar los productos seleccionados.");
            return "redirect:/productos/eliminar";
        }

        String mensaje = eliminados == 1 ? "Se eliminó 1 producto." : "Se eliminaron " + eliminados + " productos.";
        redirectAttributes.addFlashAttribute("success", mensaje);
        return "redirect:/productos/mis";
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

    private void prepararCategoriasParaFormulario(Model model, ProductoForm formulario) {
        List<Categoria> categorias = categoriaService.obtenerCategoriasActivas();
        model.addAttribute("categorias", categorias);
        model.addAttribute("cantidadCategorias", categorias.size());

        String categoriaSeleccionada = null;
        if (formulario != null && formulario.getCategoriaId() != null) {
            categoriaSeleccionada = categorias.stream()
                    .filter(cat -> formulario.getCategoriaId().equals(cat.getIdCategoria()))
                    .map(Categoria::getNombre)
                    .findFirst()
                    .orElse(null);
        }

        model.addAttribute("categoriaSeleccionadaNombre", categoriaSeleccionada);
    }
}
