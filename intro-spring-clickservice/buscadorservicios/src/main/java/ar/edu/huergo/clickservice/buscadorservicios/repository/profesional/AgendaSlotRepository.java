package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.AgendaSlot;

@Repository
public interface AgendaSlotRepository extends JpaRepository<AgendaSlot, Long> {

    List<AgendaSlot> findByProfesionalId(Long profesionalId);

    List<AgendaSlot> findByProfesionalIdAndDisponibleTrue(Long profesionalId);

    // Solapamiento estricto: start < existingEnd && end > existingStart
    boolean existsByProfesionalIdAndFechaInicioLessThanAndFechaFinGreaterThan(
            Long profesionalId,
            LocalDateTime fechaFin,
            LocalDateTime fechaInicio
    );
}
