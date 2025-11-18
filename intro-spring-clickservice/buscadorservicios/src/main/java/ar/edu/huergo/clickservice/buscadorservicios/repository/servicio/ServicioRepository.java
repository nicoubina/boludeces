package ar.edu.huergo.clickservice.buscadorservicios.repository.servicio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
}