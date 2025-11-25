package ar.edu.huergo.fastbid.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.historial.Historial;
import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.repository.historial.HistorialRepository;

@Service
public class HistorialService {

    @Autowired
    private HistorialRepository historialRepository;

    public List<Historial> obtenerHistoriales() {
        return historialRepository.findAll();
    }

    public Historial obtenerHistorialPorId(Long id) {
        return historialRepository.findById(id).orElse(null);
    }

    public Historial crearHistorial(Historial historial) {
        return historialRepository.save(historial);
    }

    public Historial registrarEvento(Subasta subasta, Producto producto, Usuario usuario,
            HistorialTipoEvento tipoEvento, String descripcion) {
        if (tipoEvento == null) {
            throw new IllegalArgumentException("El tipo de evento es obligatorio para registrar un historial");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria para registrar un historial");
        }

        Historial nuevoHistorial = new Historial();
        nuevoHistorial.setSubasta(subasta);
        if (producto != null) {
            nuevoHistorial.setProducto(producto);
        } else if (subasta != null && subasta.getProducto() != null) {
            nuevoHistorial.setProducto(subasta.getProducto());
        }
        nuevoHistorial.setUsuario(usuario);
        nuevoHistorial.setTipoEvento(tipoEvento);
        nuevoHistorial.setDescripcion(descripcion);
        nuevoHistorial.setFechaHora(Instant.now());
        return historialRepository.save(nuevoHistorial);
    }

    public Historial registrarEvento(Usuario usuario, HistorialTipoEvento tipoEvento, String descripcion) {
        return registrarEvento(null, null, usuario, tipoEvento, descripcion);
    }

    public Historial registrarEvento(HistorialTipoEvento tipoEvento, String descripcion) {
        return registrarEvento(null, null, null, tipoEvento, descripcion);
    }

    public Historial actualizarHistorial(Long id, Historial historialDetalles) {
        return historialRepository.findById(id)
                .map(historial -> {
                    historial.setSubasta(historialDetalles.getSubasta());
                    historial.setProducto(historialDetalles.getProducto());
                    historial.setUsuario(historialDetalles.getUsuario());
                    historial.setTipoEvento(historialDetalles.getTipoEvento());
                    historial.setDescripcion(historialDetalles.getDescripcion());
                    historial.setFechaHora(historialDetalles.getFechaHora());
                    return historialRepository.save(historial);
                })
                .orElse(null);
    }

    public void eliminarHistorial(Long id) {
        historialRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Historial> obtenerHistorialPorSubasta(Subasta subasta) {
        return historialRepository.findBySubastaOrderByFechaHoraDesc(subasta);
    }

    @Transactional(readOnly = true)
    public List<Historial> obtenerHistorialPorIdSubasta(Long idSubasta) {
        return historialRepository.findBySubastaIdSubastaOrderByFechaHoraDesc(idSubasta);
    }

    @Transactional(readOnly = true)
    public List<Historial> obtenerHistorialPorUsuario(Usuario usuario) {
        if (usuario == null) {
            return List.of();
        }

        List<Historial> comoPropietario = historialRepository
                .findBySubastaProductoUsuarioOrderByFechaHoraDesc(usuario);
        List<Historial> comoParticipante = historialRepository.findByUsuarioOrderByFechaHoraDesc(usuario);

        List<Historial> combinados = Stream.concat(comoPropietario.stream(), comoParticipante.stream())
                .sorted(Comparator.comparing(Historial::getFechaHora, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed())
                .collect(Collectors.toList());

        List<Historial> resultado = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (Historial historial : combinados) {
            Long id = historial.getIdHistorial();
            if (id == null || ids.add(id)) {
                resultado.add(historial);
            }
        }
        return resultado;
    }
}
