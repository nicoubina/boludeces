package ar.edu.huergo.clickservice.buscadorservicios.service.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ServicioService {
    @Autowired
    private ServicioRepository servicioRepository;

    public List<Servicio> obtenerTodosLosServicios() {
        return servicioRepository.findAll();
    }

    public Servicio obtenerServicioPorId(Long id) throws EntityNotFoundException {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));
    }

    public Servicio crearServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    public Servicio actualizarServicio(Long id, Servicio servicio) throws EntityNotFoundException {
        Servicio servicioExistente = obtenerServicioPorId(id);
        servicioExistente.setNombre(servicio.getNombre());
        servicioExistente.setPrecioHora(servicio.getPrecioHora());
        return servicioRepository.save(servicioExistente);
    }

    public void eliminarServicio(Long id) throws EntityNotFoundException {
        Servicio servicio = obtenerServicioPorId(id);
        servicioRepository.delete(servicio);
    }
}