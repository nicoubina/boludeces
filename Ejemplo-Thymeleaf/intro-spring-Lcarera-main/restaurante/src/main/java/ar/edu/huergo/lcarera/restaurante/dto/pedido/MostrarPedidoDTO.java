package ar.edu.huergo.lcarera.restaurante.dto.pedido;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.huergo.lcarera.restaurante.dto.plato.MostrarPlatoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MostrarPedidoDTO {
    private Long id;
    private List<MostrarPlatoDTO> platos;
    private double total;
    private LocalDateTime fechaPedido;
    private String clienteUsername;
}


