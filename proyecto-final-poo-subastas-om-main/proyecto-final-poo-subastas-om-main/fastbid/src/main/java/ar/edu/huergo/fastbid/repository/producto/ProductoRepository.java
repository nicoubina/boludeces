package ar.edu.huergo.fastbid.repository.producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByUsuario(Usuario usuario);

    List<Producto> findByUsuarioAndSubastaIsNull(Usuario usuario);
}
