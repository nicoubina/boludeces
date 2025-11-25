package ar.edu.huergo.fastbid.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.repository.producto.ProductoRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private HistorialService historialService;

    public List<Producto> obtenerProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorUsuario(Usuario usuario) {
        return productoRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosSinSubastaPorUsuario(Usuario usuario) {
        return productoRepository.findByUsuarioAndSubastaIsNull(usuario);
    }

    public Producto obteneProductoPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto crearProducto(Producto producto) {
        Producto guardado = productoRepository.save(producto);

        Usuario propietario = guardado.getUsuario();
        if (propietario != null) {
            historialService.registrarEvento(propietario,
                    HistorialTipoEvento.PRODUCTO_CREADO,
                    String.format("Producto \"%s\" creado", guardado.getNombre()));
        } else {
            historialService.registrarEvento(HistorialTipoEvento.PRODUCTO_CREADO,
                    String.format("Producto \"%s\" creado", guardado.getNombre()));
        }

        return guardado;
    }

    public Producto actualizarProducto(Long id, Producto productoDetalles) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            producto.setNombre(productoDetalles.getNombre());
            producto.setDescripcion(productoDetalles.getDescripcion());
            producto.setPrecioInicial(productoDetalles.getPrecioInicial());
            producto.setImagenes(productoDetalles.getImagenes());
            producto.setCategoria(productoDetalles.getCategoria());
            producto.setEstado(productoDetalles.getEstado());
            producto.setFechaPublicacion(productoDetalles.getFechaPublicacion());
            producto.setFechaFin(productoDetalles.getFechaFin());
            producto.setUsuario(productoDetalles.getUsuario());
            producto.setSubasta(productoDetalles.getSubasta());
            producto.setPrecioCompraInmediata(productoDetalles.getPrecioCompraInmediata());
            producto.setCondicion(productoDetalles.getCondicion());
            producto.setUbicacion(productoDetalles.getUbicacion());
            producto.setCantidad(productoDetalles.getCantidad());

            return productoRepository.save(producto);
        }
        return null;
    }

    public void eliminarProducto(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            Usuario propietario = producto.getUsuario();
            String nombreProducto = producto.getNombre();
            productoRepository.delete(producto);

            if (propietario != null) {
                historialService.registrarEvento(propietario,
                        HistorialTipoEvento.PRODUCTO_ELIMINADO,
                        String.format("Producto \"%s\" eliminado", nombreProducto));
            } else {
                historialService.registrarEvento(HistorialTipoEvento.PRODUCTO_ELIMINADO,
                        String.format("Producto \"%s\" eliminado", nombreProducto));
            }
        });
    }

    @Transactional
    public int eliminarProductosDeUsuario(List<Long> ids, Usuario usuario) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        List<Producto> productos = productoRepository.findAllById(ids);
        List<Producto> propios = productos.stream()
                .filter(producto -> producto.getUsuario() != null
                        && producto.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());

        if (propios.isEmpty()) {
            return 0;
        }

        propios.forEach(producto -> {
            String nombreProducto = producto.getNombre();
            productoRepository.delete(producto);
            historialService.registrarEvento(usuario,
                    HistorialTipoEvento.PRODUCTO_ELIMINADO,
                    String.format("Producto \"%s\" eliminado", nombreProducto));
        });

        return propios.size();
    }
}
