package ar.edu.huergo.fastbid.repository.subasta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;

@Repository
public interface SubastaRepository extends JpaRepository<Subasta, Long> {
    List<Subasta> findByProductoUsuarioOrderByFechaInicioDesc(Usuario usuario);

    boolean existsByProducto(Producto producto);

    boolean existsByProductoIdProducto(Long productoId);
}
