package ar.edu.huergo.clickservice.buscadorservicios.service.profesional;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.repository.profesional.ProfesionalRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProfesionalService {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Profesional> obtenerTodosLosProfesionales() {
        return profesionalRepository.findAll();
    }

    public Profesional obtenerProfesionalPorId(Long id) throws EntityNotFoundException {
        return profesionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
    }

    public Profesional crearProfesional(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }

    public Profesional actualizarProfesional(Long id, Profesional profesional) throws EntityNotFoundException {
        Profesional profesionalExistente = obtenerProfesionalPorId(id);
        
        profesionalExistente.setNombreCompleto(profesional.getNombreCompleto());
        profesionalExistente.setTelefono(profesional.getTelefono());
        profesionalExistente.setDescripcion(profesional.getDescripcion());
        profesionalExistente.setDisponible(profesional.getDisponible());
        profesionalExistente.setZonaTrabajo(profesional.getZonaTrabajo());
        
        return profesionalRepository.save(profesionalExistente);
    }

    public void eliminarProfesional(Long id) throws EntityNotFoundException {
        Profesional profesional = obtenerProfesionalPorId(id);
        profesionalRepository.delete(profesional);
    }

    public List<Profesional> obtenerProfesionalesPorServicio(Long servicioId) {
        return profesionalRepository.findByServiciosIdAndDisponibleTrue(servicioId);
    }

    public List<Profesional> obtenerProfesionalesDisponibles() {
        return profesionalRepository.findByDisponibleTrue();
    }

    public Profesional asignarServicios(Long profesionalId, Set<Long> serviciosIds) throws EntityNotFoundException {
        Profesional profesional = obtenerProfesionalPorId(profesionalId);
        Set<Servicio> servicios = Set.copyOf(servicioRepository.findAllById(serviciosIds));
        profesional.setServicios(servicios);
        return profesionalRepository.save(profesional);
    }

    public Profesional obtenerProfesionalPorUsuarioId(Long usuarioId) throws EntityNotFoundException {
        return profesionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
    }
}