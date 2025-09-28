package ar.edu.huergo.lcarera.restaurante.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import ar.edu.huergo.lcarera.restaurante.entity.plato.Ingrediente;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;
import ar.edu.huergo.lcarera.restaurante.entity.pedido.Pedido;
import ar.edu.huergo.lcarera.restaurante.service.plato.IngredienteService;
import ar.edu.huergo.lcarera.restaurante.service.plato.PlatoService;
import ar.edu.huergo.lcarera.restaurante.service.security.UsuarioService;
import ar.edu.huergo.lcarera.restaurante.service.pedido.PedidoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controlador web para manejar las vistas del restaurante usando Thymeleaf
 * Este controlador sirve páginas HTML en lugar de respuestas JSON
 */
@Controller
@RequestMapping("/web")
public class RestauranteWebController {

    @Autowired
    private PlatoService platoService;

    @Autowired
    private IngredienteService ingredienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PedidoService pedidoService;

    /**
     * Página principal del restaurante - muestra todos los platos
     */
    @GetMapping({"", "/"})
    public String home(Model model) {
        List<Plato> platos = platoService.obtenerTodosLosPlatos();
        model.addAttribute("platos", platos);
        model.addAttribute("titulo", "Menú del Restaurante");
        return "platos/lista";
    }

    /**
     * Página para mostrar la lista de platos
     */
    @GetMapping("/platos")
    public String listarPlatos(Model model) {
        List<Plato> platos = platoService.obtenerTodosLosPlatos();
        model.addAttribute("platos", platos);
        model.addAttribute("titulo", "Lista de Platos");
        return "platos/lista";
    }

    /**
     * Página para mostrar los detalles de un plato específico
     */
    @GetMapping("/platos/{id}")
    public String verPlato(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Plato plato = platoService.obtenerPlatoPorId(id);
            model.addAttribute("plato", plato);
            model.addAttribute("titulo", "Detalles del Plato");
            return "platos/detalle";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Plato no encontrado");
            return "redirect:/web/platos";
        }
    }

    /**
     * Formulario para crear un nuevo plato
     */
    @GetMapping("/platos/nuevo")
    public String formularioNuevoPlato(Model model) {
        List<Ingrediente> ingredientes = ingredienteService.obtenerTodosLosIngredientes();
        model.addAttribute("plato", new Plato());
        model.addAttribute("ingredientes", ingredientes);
        model.addAttribute("titulo", "Crear Nuevo Plato");
        return "platos/formulario";
    }

    /**
     * Procesar la creación de un nuevo plato
     */
    @PostMapping("/platos")
    public String crearPlato(@Valid @ModelAttribute Plato plato, 
                           BindingResult result, 
                           Model model, 
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Ingrediente> ingredientes = ingredienteService.obtenerTodosLosIngredientes();
            model.addAttribute("ingredientes", ingredientes);
            model.addAttribute("titulo", "Crear Nuevo Plato");
            return "platos/formulario";
        }

        try {
            // Para simplificar, usamos todos los ingredientes disponibles
            List<Long> ingredientesIds = ingredienteService.obtenerTodosLosIngredientes()
                .stream()
                .map(Ingrediente::getId)
                .toList();
            
            platoService.crearPlato(plato, ingredientesIds);
            redirectAttributes.addFlashAttribute("success", "Plato creado exitosamente");
            return "redirect:/web/platos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el plato: " + e.getMessage());
            return "redirect:/web/platos/nuevo";
        }
    }

    /**
     * Página para mostrar la lista de ingredientes
     */
    @GetMapping("/ingredientes")
    public String listarIngredientes(Model model) {
        List<Ingrediente> ingredientes = ingredienteService.obtenerTodosLosIngredientes();
        model.addAttribute("ingredientes", ingredientes);
        model.addAttribute("titulo", "Lista de Ingredientes");
        return "ingredientes/lista";
    }

    /**
     * Formulario para crear nuevo ingrediente
     */
    @GetMapping("/ingredientes/nuevo")
    public String formularioNuevoIngrediente(Model model) {
        model.addAttribute("ingrediente", new Ingrediente());
        return "ingredientes/formulario";
    }

    /**
     * Formulario para editar ingrediente
     */
    @GetMapping("/ingredientes/{id}/editar")
    public String formularioEditarIngrediente(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Ingrediente ingrediente = ingredienteService.obtenerIngredientePorId(id);
            model.addAttribute("ingrediente", ingrediente);
            return "ingredientes/formulario";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Ingrediente no encontrado");
            return "redirect:/web/ingredientes";
        }
    }

    /**
     * Procesar creación o actualización de ingrediente
     */
    @PostMapping("/ingredientes")
    public String procesarIngrediente(@Valid @ModelAttribute Ingrediente ingrediente,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "ingredientes/formulario";
        }

        try {
            if (ingrediente.getId() == null) {
                ingredienteService.crearIngrediente(ingrediente);
                redirectAttributes.addFlashAttribute("success", "Ingrediente creado exitosamente");
            } else {
                ingredienteService.actualizarIngrediente(ingrediente.getId(), ingrediente);
                redirectAttributes.addFlashAttribute("success", "Ingrediente actualizado exitosamente");
            }
            return "redirect:/web/ingredientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar ingrediente: " + e.getMessage());
            return ingrediente.getId() == null ? "redirect:/web/ingredientes/nuevo" : 
                   "redirect:/web/ingredientes/" + ingrediente.getId() + "/editar";
        }
    }

    /**
     * Página de información del restaurante
     */
    @GetMapping("/acerca")
    public String acercaDe(Model model) {
        model.addAttribute("titulo", "Acerca del Restaurante");
        model.addAttribute("nombre", "Restaurante Huergo");
        model.addAttribute("descripcion", "Un restaurante moderno con los mejores platos de la ciudad");
        return "acerca";
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
        return "auth/login";
    }

    /**
     * Página de registro
     */
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    /**
     * Procesar registro de usuario
     */
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute Usuario usuario,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/registro";
        }

        try {
            usuarioService.registrarUsuario(usuario.getUsername(), usuario.getUsername(), usuario.getPassword());
            redirectAttributes.addFlashAttribute("success", "Usuario registrado exitosamente. Puedes iniciar sesión.");
            return "redirect:/web/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "redirect:/web/registro";
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
     * Panel de administración - Gestión de pedidos
     */
    @GetMapping("/admin/pedidos")
    public String gestionPedidos(@RequestParam(value = "fechaInicio", required = false) String fechaInicio,
                               @RequestParam(value = "fechaFin", required = false) String fechaFin,
                               Model model) {
        try {
            List<Pedido> pedidos;
            if (fechaInicio != null && fechaFin != null && !fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
                // Filtrar por fechas si se proporcionan
                pedidos = pedidoService.obtenerPedidosPorRangoFechas(
                    java.time.LocalDate.parse(fechaInicio).atStartOfDay(),
                    java.time.LocalDate.parse(fechaFin).atTime(23, 59, 59)
                );
                model.addAttribute("fechaInicio", fechaInicio);
                model.addAttribute("fechaFin", fechaFin);
            } else {
                pedidos = pedidoService.obtenerTodosLosPedidos();
            }
            
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("totalPedidos", pedidos.size());
            model.addAttribute("totalIngresos", pedidos.stream()
                .mapToDouble(Pedido::getTotal)
                .sum());
                
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pedidos: " + e.getMessage());
            model.addAttribute("pedidos", List.of());
            model.addAttribute("totalPedidos", 0);
            model.addAttribute("totalIngresos", 0.0);
        }
        
        return "admin/pedidos";
    }

    /**
     * Formulario para crear un nuevo pedido (solo admin)
     */
    @GetMapping("/admin/pedidos/nuevo")
    public String formularioNuevoPedido(Model model) {
        List<Plato> platos = platoService.obtenerTodosLosPlatos();
        model.addAttribute("platos", platos);
        return "admin/nuevo-pedido";
    }

    /**
     * Procesar creación de pedido desde web (admin)
     */
    @PostMapping("/admin/pedidos")
    public String crearPedidoWeb(@RequestParam("platosIds") List<Long> platosIds,
                               RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.crearPedido(platosIds);
            redirectAttributes.addFlashAttribute("success", 
                "Pedido #" + pedido.getId() + " creado exitosamente. Total: $" + pedido.getTotal());
            return "redirect:/web/admin/pedidos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear pedido: " + e.getMessage());
            return "redirect:/web/admin/pedidos/nuevo";
        }
    }

    /**
     * Página de pedidos del cliente autenticado
     */
    @GetMapping("/mis-pedidos")
    public String misPedidos(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            List<Pedido> pedidos = pedidoService.obtenerPedidosDeUsuario(username);
            
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("totalPedidos", pedidos.size());
            model.addAttribute("totalGastado", pedidos.stream()
                .mapToDouble(Pedido::getTotal)
                .sum());
            
            // Plato más pedido
            String platoFavorito = pedidos.stream()
                .flatMap(p -> p.getPlatos().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                    plato -> plato.getNombre(),
                    java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);
                
            model.addAttribute("platoFavorito", platoFavorito);
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pedidos: " + e.getMessage());
            model.addAttribute("pedidos", List.of());
            model.addAttribute("totalPedidos", 0);
            model.addAttribute("totalGastado", 0.0);
        }
        
        return "pedidos/mis-pedidos";
    }

    /**
     * Formulario para realizar nuevo pedido (cliente)
     */
    @GetMapping("/pedidos/nuevo")
    public String formularioNuevoPedidoCliente(Model model) {
        List<Plato> platos = platoService.obtenerTodosLosPlatos();
        model.addAttribute("platos", platos);
        return "pedidos/nuevo-pedido";
    }

    /**
     * Procesar pedido del cliente
     */
    @PostMapping("/pedidos")
    public String crearPedidoCliente(@RequestParam("platosIds") List<Long> platosIds,
                                   RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.crearPedido(platosIds);
            redirectAttributes.addFlashAttribute("success", 
                "¡Pedido realizado exitosamente! Número de pedido: #" + pedido.getId() + 
                ". Total: $" + pedido.getTotal() + ". Tiempo estimado: 20-30 minutos.");
            return "redirect:/web/mis-pedidos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al realizar pedido: " + e.getMessage());
            return "redirect:/web/pedidos/nuevo";
        }
    }
}
