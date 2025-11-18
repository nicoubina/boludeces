package ar.edu.huergo.clickservice.buscadorservicios.repository.servicio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio.EstadoSolicitud;

@Repository
public interface SolicitudServicioRepository extends JpaRepository<SolicitudServicio, Long> {
    
    // Buscar solicitudes por cliente ID (historial del cliente)
    List<SolicitudServicio> findByClienteId(Long clienteId);
    
    // Buscar solicitudes por estado (para dashboards y gestión)
    List<SolicitudServicio> findByEstado(EstadoSolicitud estado);
    
    // Buscar solicitudes por servicio y estado (para asignar a profesionales)
    List<SolicitudServicio> findByServicioIdAndEstado(Long servicioId, EstadoSolicitud estado);
}