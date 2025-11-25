package ar.edu.huergo.fastbid.repository.historial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.fastbid.entity.historial.Historial;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {
    List<Historial> findBySubastaOrderByFechaHoraDesc(Subasta subasta);

    List<Historial> findBySubastaIdSubastaOrderByFechaHoraDesc(Long idSubasta);

    List<Historial> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    List<Historial> findBySubastaProductoUsuarioOrderByFechaHoraDesc(Usuario usuario);
}
