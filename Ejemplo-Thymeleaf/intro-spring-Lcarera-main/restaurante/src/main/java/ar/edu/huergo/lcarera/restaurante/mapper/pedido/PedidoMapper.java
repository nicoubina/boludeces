package ar.edu.huergo.lcarera.restaurante.mapper.pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.huergo.lcarera.restaurante.dto.pedido.MostrarPedidoDTO;
import ar.edu.huergo.lcarera.restaurante.dto.plato.MostrarPlatoDTO;
import ar.edu.huergo.lcarera.restaurante.entity.pedido.Pedido;
import ar.edu.huergo.lcarera.restaurante.mapper.plato.PlatoMapper;

@Component
public class PedidoMapper {

    @Autowired
    private PlatoMapper platoMapper;

    public MostrarPedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        List<MostrarPlatoDTO> platos = platoMapper.toDTOList(pedido.getPlatos());
        String clienteUsername = pedido.getCliente() != null ? pedido.getCliente().getUsername() : null;
        return new MostrarPedidoDTO(pedido.getId(), platos, pedido.getTotal(), pedido.getFechaPedido(), clienteUsername);
    }

    public List<MostrarPedidoDTO> toDTOList(List<Pedido> pedidos) {
        if (pedidos == null) {
            return new ArrayList<>();
        }
        return pedidos.stream().map(this::toDTO).collect(Collectors.toList());
    }
}


