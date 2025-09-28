package ar.edu.huergo.lcarera.restaurante.dto.pedido;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportePedidosDTO {
    private long cantidadPedidos;
    private double totalRecaudado;
    private List<MostrarPedidoDTO> pedidos;
}


