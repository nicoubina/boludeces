package ar.edu.huergo.lcarera.restaurante.controller.pedido;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.huergo.lcarera.restaurante.dto.pedido.CrearPedidoDTO;
import ar.edu.huergo.lcarera.restaurante.dto.pedido.MostrarPedidoDTO;
import ar.edu.huergo.lcarera.restaurante.dto.pedido.ReportePedidosDTO;
import ar.edu.huergo.lcarera.restaurante.entity.pedido.Pedido;
import ar.edu.huergo.lcarera.restaurante.mapper.pedido.PedidoMapper;
import ar.edu.huergo.lcarera.restaurante.service.pedido.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    private final PedidoMapper pedidoMapper;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<MostrarPedidoDTO> crearPedido(@Valid @RequestBody CrearPedidoDTO dto) {
        Pedido pedido = pedidoService.crearPedido(dto.getPlatosIds());
        return ResponseEntity.ok(pedidoMapper.toDTO(pedido));
    }

    @GetMapping("/reporte")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportePedidosDTO> reportePorFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosDeFecha(fecha);
        double totalRecaudado = pedidoService.totalRecaudadoEnFecha(fecha);
        List<MostrarPedidoDTO> pedidosDTO = pedidoMapper.toDTOList(pedidos);
        ReportePedidosDTO reporte = new ReportePedidosDTO(pedidos.size(), totalRecaudado, pedidosDTO);
        return ResponseEntity.ok(reporte);
    }
}


