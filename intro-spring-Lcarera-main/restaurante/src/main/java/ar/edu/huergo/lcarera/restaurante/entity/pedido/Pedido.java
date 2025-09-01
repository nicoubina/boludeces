package ar.edu.huergo.lcarera.restaurante.entity.pedido;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "pedidos_platos",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "plato_id"))
    @NotEmpty(message = "El pedido debe contener al menos un plato")
    private List<Plato> platos;

    @PositiveOrZero(message = "El total no puede ser negativo")
    @Column(nullable = false)
    private double total;

    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;
}


