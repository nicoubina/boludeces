package ar.edu.huergo.fastbid.repository.prestamo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.fastbid.entity.prestamo.PrestamoLibro;

@Repository
public interface PrestamoLibroRepository extends JpaRepository<PrestamoLibro, Long> {

    List<PrestamoLibro> findByDevueltoFalseAndFechaDevolucionBefore(LocalDate fecha);

    List<PrestamoLibro> findByNombreUsuarioIgnoreCase(String nombreUsuario);
}
