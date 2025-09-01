package ar.edu.huergo.lcarera.restaurante.dto.pedido;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CrearPedidoDTO {
    @NotEmpty(message = "Debe especificar al menos un plato")
    private List<Long> platosIds;
}


