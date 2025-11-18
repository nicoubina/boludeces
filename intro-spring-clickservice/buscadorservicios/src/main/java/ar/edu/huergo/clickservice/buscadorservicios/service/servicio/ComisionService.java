package ar.edu.huergo.clickservice.buscadorservicios.service.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Comision;
import ar.edu.huergo.clickservice.buscadorservicios.repository.servicio.ComisionRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ComisionService {

    @Autowired
    private ComisionRepository comisionRepository;

    public List<Comision> obtenerTodasLasComisiones() {
        return comisionRepository.findAll();
    }

    public Comision obtenerComisionPorId(Long id) throws EntityNotFoundException {
        return comisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comisión no encontrada"));
    }

    public Comision crearComision(Comision comision) {
        return comisionRepository.save(comision);
    }

    public Comision actualizarComision(Long id, Comision comision) throws EntityNotFoundException {
        Comision comisionExistente = obtenerComisionPorId(id);
        comisionExistente.setPagoId(comision.getPagoId());
        comisionExistente.setTasa(comision.getTasa());
        comisionExistente.setBase(comision.getBase());
        comisionExistente.setMonto(comision.getMonto());
        comisionExistente.setFecha(comision.getFecha());
        return comisionRepository.save(comisionExistente);
    }

    public void eliminarComision(Long id) throws EntityNotFoundException {
        Comision comision = obtenerComisionPorId(id);
        comisionRepository.delete(comision);
    }
}
