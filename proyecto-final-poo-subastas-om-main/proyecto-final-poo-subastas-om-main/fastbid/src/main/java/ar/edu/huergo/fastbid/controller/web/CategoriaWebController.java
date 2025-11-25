package ar.edu.huergo.fastbid.controller.web;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.huergo.fastbid.dto.CategoriaForm;
import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaWebController {

    private final CategoriaService categoriaService;

    @GetMapping
    public String redirigirListado() {
        return "redirect:/categorias/ver";
    }

    @GetMapping("/ver")
    public String verCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.obtenerCategorias());
        return "categorias/ver-categorias";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        if (!model.containsAttribute("categoriaForm")) {
            model.addAttribute("categoriaForm", new CategoriaForm());
        }
        return "categorias/crear-categoria";
    }

    @PostMapping("/crear")
    public String crearCategoria(@Valid @ModelAttribute("categoriaForm") CategoriaForm categoriaForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (categoriaForm.getNombre() != null) {
            categoriaForm.setNombre(categoriaForm.getNombre().trim());
        }

        if (bindingResult.hasErrors()) {
            return "categorias/crear-categoria";
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaForm.getNombre());
        categoria.setDescripcion(categoriaForm.getDescripcion());
        categoria.setActiva(categoriaForm.isActiva());

        try {
            categoriaService.crearCategoria(categoria);
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("nombre", "categoria.nombre.duplicado", ex.getMessage());
            return "categorias/crear-categoria";
        }

        redirectAttributes.addFlashAttribute("success", "Categoría creada correctamente.");
        return "redirect:/categorias/ver";
    }

    @GetMapping("/eliminar")
    public String mostrarFormularioEliminar(Model model) {
        List<Categoria> categorias = categoriaService.obtenerCategorias();
        model.addAttribute("categorias", categorias);
        return "categorias/eliminar-categoria";
    }

    @PostMapping("/eliminar")
    public String eliminarCategorias(@RequestParam(name = "ids", required = false) List<Long> ids,
            RedirectAttributes redirectAttributes) {

        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selecciona al menos una categoría para eliminar.");
            return "redirect:/categorias/eliminar";
        }

        int eliminadas;
        try {
            eliminadas = categoriaService.eliminarCategoriasPorIds(ids);
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pueden eliminar las categorías seleccionadas porque tienen productos asociados.");
            return "redirect:/categorias/eliminar";
        }

        if (eliminadas == 0) {
            redirectAttributes.addFlashAttribute("error", "No se encontraron categorías para eliminar.");
            return "redirect:/categorias/eliminar";
        }

        String mensaje = eliminadas == 1 ? "Se eliminó 1 categoría." : "Se eliminaron " + eliminadas + " categorías.";
        redirectAttributes.addFlashAttribute("success", mensaje);
        return "redirect:/categorias/ver";
    }
}

