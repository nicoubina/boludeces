package ar.edu.huergo.clickservice.buscadorservicios.service.profesional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Reseña;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ProfesionalRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ReseñaRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.security.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ReseñaService {
    
    @Autowired
    private ReseñaRepository reseñaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProfesionalRepository profesionalRepository;

    public List<Reseña> obtenerTodasLasReseñas() {
        return reseñaRepository.findAll();
    }

    public Reseña obtenerReseñaPorId(Long id) throws EntityNotFoundException {
        return reseñaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reseña no encontrada"));
    }

    public List<Reseña> obtenerReseñasPorProfesional(Long profesionalId) {
        return reseñaRepository.findByProfesionalId(profesionalId);
    }

    public List<Reseña> obtenerReseñasPorUsuario(Long usuarioId) {
        return reseñaRepository.findByUsuarioId(usuarioId);
    }

    public Reseña obtenerReseñaPorOrden(Long ordenId) throws EntityNotFoundException {
        return reseñaRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new EntityNotFoundException("Reseña no encontrada para la orden especificada"));
    }

    public Reseña crearReseña(Reseña reseña, Long usuarioId, Long profesionalId) {
        // Validar que no exista una reseña para esta orden
        if (reseñaRepository.existsByOrdenId(reseña.getOrdenId())) {
            throw new IllegalArgumentException("Ya existe una reseña para esta orden");
        }

        // Buscar y asignar el usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        reseña.setUsuario(usuario);

        // Buscar y asignar el profesional
        Profesional profesional = profesionalRepository.findById(profesionalId)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
        reseña.setProfesional(profesional);

        // Establecer la fecha actual si no viene
        if (reseña.getFecha() == null) {
            reseña.setFecha(LocalDateTime.now());
        }

        return reseñaRepository.save(reseña);
    }

    public Reseña actualizarReseña(Long id, Reseña reseña) throws EntityNotFoundException {
        Reseña reseñaExistente = obtenerReseñaPorId(id);
        
        // Actualizar solo los campos editables
        reseñaExistente.setRating(reseña.getRating());
        reseñaExistente.setComentario(reseña.getComentario());
        
        // Actualizar la fecha de modificación
        reseñaExistente.setFecha(LocalDateTime.now());
        
        return reseñaRepository.save(reseñaExistente);
    }

    public void eliminarReseña(Long id) throws EntityNotFoundException {
        Reseña reseña = obtenerReseñaPorId(id);
        reseñaRepository.delete(reseña);
    }
}