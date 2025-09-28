package ar.edu.huergo.lcarera.restaurante.repository.pedido;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.lcarera.restaurante.entity.pedido.Pedido;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByFechaPedidoBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("select coalesce(sum(p.total), 0) from Pedido p where p.fechaPedido between :inicio and :fin")
    double totalRecaudadoEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<Pedido> findByClienteOrderByFechaPedidoDesc(Usuario cliente);
}


