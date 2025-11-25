package ar.edu.huergo.fastbid.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Puja;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.repository.puja.PujaRepository;

@Service
public class PujaService {

    @Autowired
    private PujaRepository pujaRepository;

    @Autowired
    private HistorialService historialService;

    public List<Puja> obtenerPujas() {
        return pujaRepository.findAll();
    }

    public Puja obtenerPujaPorId(Long id) {
        return pujaRepository.findById(id).orElse(null);
    }

    public Puja crearPuja(Puja puja) {
        Puja guardada = pujaRepository.save(puja);

        Subasta subasta = guardada.getSubasta();
        Producto producto = subasta != null ? subasta.getProducto() : null;
        Usuario actor = guardada.getUsuario();
        String descripcion = actor != null
                ? String.format("Nueva puja de %s por $%,.2f", actor.getUsername(), guardada.getMonto())
                : String.format("Nueva puja registrada por $%,.2f", guardada.getMonto());

        historialService.registrarEvento(subasta, producto, actor,
                HistorialTipoEvento.PUJA_NUEVA, descripcion);

        return guardada;
    }

    public Puja actualizarPuja(Long id, Puja pujaDetalles) {
        return pujaRepository.findById(id)
                .map(puja -> {
                    puja.setSubasta(pujaDetalles.getSubasta());
                    puja.setUsuario(pujaDetalles.getUsuario());
                    puja.setMonto(pujaDetalles.getMonto());
                    puja.setFechaHora(pujaDetalles.getFechaHora());
                    return pujaRepository.save(puja);
                })
                .orElse(null);
    }

    public void eliminarPuja(Long id) {
        pujaRepository.deleteById(id);
    }
}
