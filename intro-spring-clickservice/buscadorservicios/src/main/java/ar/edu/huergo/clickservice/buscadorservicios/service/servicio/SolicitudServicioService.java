package ar.edu.huergo.clickservice.buscadorservicios.service.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio.EstadoSolicitud;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.SolicitudServicioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SolicitudServicioService {

    @Autowired
    private SolicitudServicioRepository solicitudServicioRepository;

    public List<SolicitudServicio> obtenerTodasLasSolicitudes() {
        return solicitudServicioRepository.findAll();
    }

    public SolicitudServicio obtenerSolicitudPorId(Long id) throws EntityNotFoundException {
        return solicitudServicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada"));
    }

    public SolicitudServicio crearSolicitud(SolicitudServicio solicitud) {
        return solicitudServicioRepository.save(solicitud);
    }

    public SolicitudServicio actualizarSolicitud(Long id, SolicitudServicio solicitud) throws EntityNotFoundException {
        SolicitudServicio solicitudExistente = obtenerSolicitudPorId(id);
        
        solicitudExistente.setDescripcionProblema(solicitud.getDescripcionProblema());
        solicitudExistente.setDireccionServicio(solicitud.getDireccionServicio());
        solicitudExistente.setFechaSolicitada(solicitud.getFechaSolicitada());
        solicitudExistente.setFranjaHoraria(solicitud.getFranjaHoraria());
        solicitudExistente.setPresupuestoMaximo(solicitud.getPresupuestoMaximo());
        solicitudExistente.setComentariosAdicionales(solicitud.getComentariosAdicionales());
        
        return solicitudServicioRepository.save(solicitudExistente);
    }

    public void eliminarSolicitud(Long id) throws EntityNotFoundException {
        SolicitudServicio solicitud = obtenerSolicitudPorId(id);
        solicitudServicioRepository.delete(solicitud);
    }

    public List<SolicitudServicio> obtenerSolicitudesPorCliente(Long clienteId) {
        return solicitudServicioRepository.findByClienteId(clienteId);
    }

    public List<SolicitudServicio> obtenerSolicitudesPorEstado(EstadoSolicitud estado) {
        return solicitudServicioRepository.findByEstado(estado);
    }

    public List<SolicitudServicio> obtenerSolicitudesPorServicioYEstado(Long servicioId, EstadoSolicitud estado) {
        return solicitudServicioRepository.findByServicioIdAndEstado(servicioId, estado);
    }

    public SolicitudServicio cambiarEstadoSolicitud(Long id, EstadoSolicitud nuevoEstado) throws EntityNotFoundException {
        SolicitudServicio solicitud = obtenerSolicitudPorId(id);
        solicitud.setEstado(nuevoEstado);
        return solicitudServicioRepository.save(solicitud);
    }
}