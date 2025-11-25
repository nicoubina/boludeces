package ar.edu.huergo.fastbid.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.fastbid.dto.HistorialDTO;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.historial.Historial;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;

@Component
public class HistorialMapper {

    public HistorialDTO toDto(Historial historial) {
        if (historial == null) {
            return null;
        }

        Subasta subasta = historial.getSubasta();
        Long subastaId = subasta != null ? subasta.getIdSubasta() : null;

        Producto producto = historial.getProducto();
        if (producto == null && subasta != null) {
            producto = subasta.getProducto();
        }
        Long productoId = producto != null ? producto.getIdProducto() : null;
        String productoNombre = producto != null ? producto.getNombre() : null;

        Usuario usuario = historial.getUsuario();
        Long usuarioId = usuario != null ? usuario.getId() : null;
        String usuarioEmail = usuario != null ? usuario.getUsername() : null;

        return new HistorialDTO(
                historial.getIdHistorial(),
                subastaId,
                productoId,
                productoNombre,
                usuarioId,
                usuarioEmail,
                historial.getTipoEvento(),
                historial.getDescripcion(),
                historial.getFechaHora()
        );
    }

    public Historial toEntity(HistorialDTO dto) {
        if (dto == null) {
            return null;
        }

        Historial historial = new Historial();
        historial.setIdHistorial(dto.idHistorial());
        if (dto.subastaId() != null) {
            Subasta subasta = new Subasta();
            subasta.setIdSubasta(dto.subastaId());
            historial.setSubasta(subasta);
        }
        if (dto.productoId() != null) {
            Producto producto = new Producto();
            producto.setIdProducto(dto.productoId());
            historial.setProducto(producto);
        }
        if (dto.usuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.usuarioId());
            historial.setUsuario(usuario);
        }
        historial.setTipoEvento(dto.tipoEvento());
        historial.setDescripcion(dto.descripcion());
        historial.setFechaHora(dto.fechaHora());
        return historial;
    }

    public List<HistorialDTO> toDtoList(List<Historial> historiales) {
        if (historiales == null) {
            return new ArrayList<>();
        }
        return historiales.stream().map(this::toDto).collect(Collectors.toList());
    }
}
