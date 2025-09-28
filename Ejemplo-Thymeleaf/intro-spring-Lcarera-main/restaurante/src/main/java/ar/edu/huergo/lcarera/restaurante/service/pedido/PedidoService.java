package ar.edu.huergo.lcarera.restaurante.service.pedido;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ar.edu.huergo.lcarera.restaurante.entity.pedido.Pedido;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;
import ar.edu.huergo.lcarera.restaurante.repository.pedido.PedidoRepository;
import ar.edu.huergo.lcarera.restaurante.repository.plato.PlatoRepository;
import ar.edu.huergo.lcarera.restaurante.repository.security.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final PlatoRepository platoRepository;
    private final UsuarioRepository usuarioRepository;

    public Pedido crearPedido(List<Long> platosIds) {
        if (platosIds == null || platosIds.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un plato");
        }

        List<Plato> platos = platoRepository.findAllById(platosIds);
        long distintos = platosIds.stream().filter(Objects::nonNull).distinct().count();
        if (platos.size() != distintos) {
            throw new EntityNotFoundException("Uno o más platos no existen");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario cliente = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        double total = platos.stream().mapToDouble(Plato::getPrecio).sum();

        Pedido pedido = new Pedido();
        pedido.setPlatos(platos);
        pedido.setTotal(total);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setCliente(cliente);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerPedidosDeFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return pedidoRepository.findByFechaPedidoBetween(inicio, fin);
    }

    public double totalRecaudadoEnFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return pedidoRepository.totalRecaudadoEntre(inicio, fin);
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> obtenerPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
    }

    public List<Pedido> obtenerPedidosDeUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return pedidoRepository.findByClienteOrderByFechaPedidoDesc(usuario);
    }
}


