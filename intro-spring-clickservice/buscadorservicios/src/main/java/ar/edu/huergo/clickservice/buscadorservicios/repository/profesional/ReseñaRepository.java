package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Reseña;

@Repository
public interface ReseñaRepository extends JpaRepository<Reseña, Long> {
    
    // Buscar todas las reseñas de un profesional específico
    List<Reseña> findByProfesionalId(Long profesionalId);
    
    // Buscar todas las reseñas hechas por un usuario específico
    List<Reseña> findByUsuarioId(Long usuarioId);
    
    // Buscar reseña de una orden específica
    Optional<Reseña> findByOrdenId(Long ordenId);
    
    // Verificar si ya existe una reseña para una orden
    boolean existsByOrdenId(Long ordenId);
}