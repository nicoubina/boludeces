package ar.edu.huergo.fastbid.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;
import ar.edu.huergo.fastbid.repository.producto.ProductoRepository;
import ar.edu.huergo.fastbid.repository.subasta.SubastaRepository;

@Service
public class SubastaService {

    @Autowired
    private SubastaRepository subastaRepository;

    @Autowired
    private HistorialService historialService;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Subasta> obtenerSubastas() {
        return subastaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Subasta> obtenerSubastasPorUsuario(Usuario usuario) {
        return subastaRepository.findByProductoUsuarioOrderByFechaInicioDesc(usuario);
    }

    public Subasta obtenerSubastaPorId(Long id) {
        return subastaRepository.findById(id).orElse(null);
    }

    public Subasta crearSubasta(Subasta subasta) {
        Producto producto = subasta.getProducto();
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("Debes asociar un producto existente a la subasta");
        }

        Long productoId = producto.getIdProducto();
        Producto productoPersistido = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto seleccionado no existe"));

        if (productoPersistido.getSubasta() != null
                || subastaRepository.existsByProductoIdProducto(productoId)) {
            throw new IllegalArgumentException("El producto seleccionado ya tiene una subasta activa");
        }

        subasta.setProducto(productoPersistido);

        Subasta guardada = subastaRepository.save(subasta);

        productoPersistido.setSubasta(guardada);
        Usuario propietario = productoPersistido.getUsuario();
        historialService.registrarEvento(guardada, productoPersistido, propietario,
                HistorialTipoEvento.CREACION_SUBASTA,
                "Subasta creada para " + productoPersistido.getNombre());

        return guardada;
    }

    @Transactional
    public int eliminarSubastasDeUsuario(List<Long> ids, Usuario usuario) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        List<Subasta> subastas = subastaRepository.findAllById(ids);
        List<Subasta> propias = subastas.stream()
                .filter(subasta -> subasta.getProducto() != null
                        && subasta.getProducto().getUsuario() != null
                        && subasta.getProducto().getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());

        if (propias.isEmpty()) {
            return 0;
        }

        propias.forEach(subasta -> {
            Producto producto = subasta.getProducto();
            Usuario propietario = producto != null ? producto.getUsuario() : null;
            historialService.registrarEvento(subasta, producto, propietario,
                    HistorialTipoEvento.SUBASTA_CANCELADA,
                    "Subasta eliminada por el vendedor");
            if (producto != null) {
                producto.setSubasta(null);
            }
        });

        subastaRepository.deleteAll(propias);
        return propias.size();
    }

    @Transactional(readOnly = true)
    public boolean existeSubastaParaProducto(Producto producto) {
        if (producto == null || producto.getIdProducto() == null) {
            return false;
        }

        if (producto.getSubasta() != null) {
            return true;
        }

        return subastaRepository.existsByProductoIdProducto(producto.getIdProducto());
    }

    public Subasta actualizarSubasta(Long id, Subasta subastaDetalles) {
        return subastaRepository.findById(id)
                .map(subasta -> {
                    SubastaEstado estadoAnterior = subasta.getEstado();
                    Usuario ganadorAnterior = subasta.getGanador();

                    subasta.setProducto(subastaDetalles.getProducto());
                    subasta.setFechaInicio(subastaDetalles.getFechaInicio());
                    subasta.setFechaFin(subastaDetalles.getFechaFin());
                    subasta.setPrecioInicial(subastaDetalles.getPrecioInicial());
                    subasta.setPrecioActual(subastaDetalles.getPrecioActual());
                    subasta.setEstado(subastaDetalles.getEstado());
                    subasta.setGanador(subastaDetalles.getGanador());
                    subasta.setIncrementoMinimo(subastaDetalles.getIncrementoMinimo());
                    subasta.setCompraInmediata(subastaDetalles.getCompraInmediata());
                    Subasta actualizada = subastaRepository.save(subasta);

                    Producto producto = actualizada.getProducto();
                    Usuario propietario = producto != null ? producto.getUsuario() : null;

                    if (estadoAnterior != actualizada.getEstado()) {
                        if (actualizada.getEstado() == SubastaEstado.CANCELADA) {
                            historialService.registrarEvento(actualizada, producto, propietario,
                                    HistorialTipoEvento.SUBASTA_CANCELADA,
                                    "La subasta fue cancelada");
                        } else if (actualizada.getEstado() == SubastaEstado.FINALIZADA) {
                            historialService.registrarEvento(actualizada, producto, propietario,
                                    HistorialTipoEvento.SUBASTA_FINALIZADA,
                                    "La subasta finalizó");
                        }
                    }

                    if (!Objects.equals(ganadorAnterior, actualizada.getGanador())
                            && actualizada.getGanador() != null) {
                        historialService.registrarEvento(actualizada, producto, actualizada.getGanador(),
                                HistorialTipoEvento.SUBASTA_GANADA,
                                String.format("%s ganó la subasta con una oferta de $%,.2f",
                                        actualizada.getGanador().getUsername(), actualizada.getPrecioActual()));
                    }

                    return actualizada;
                })
                .orElse(null);
    }

    public void eliminarSubasta(Long id) {
        subastaRepository.findById(id).ifPresent(subasta -> {
            Producto producto = subasta.getProducto();
            Usuario propietario = producto != null ? producto.getUsuario() : null;
            historialService.registrarEvento(subasta, producto, propietario,
                    HistorialTipoEvento.SUBASTA_CANCELADA,
                    "Subasta eliminada");
            if (producto != null) {
                producto.setSubasta(null);
            }
            subastaRepository.delete(subasta);
        });
    }
}
