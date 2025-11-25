package ar.edu.huergo.fastbid.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.fastbid.dto.PujaDTO;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Puja;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;

@Component
public class PujaMapper {

    public PujaDTO toDto(Puja puja) {
        if (puja == null) {
            return null;
        }

        Subasta subasta = puja.getSubasta();
        Long subastaId = subasta != null ? subasta.getIdSubasta() : null;

        Producto producto = null;
        if (subasta != null) {
            producto = subasta.getProducto();
        }
        Long productoId = producto != null ? producto.getIdProducto() : null;
        String productoNombre = producto != null ? producto.getNombre() : null;

        Usuario usuario = puja.getUsuario();
        Long usuarioId = usuario != null ? usuario.getId() : null;
        String usuarioEmail = usuario != null ? usuario.getUsername() : null;

        return new PujaDTO(
                puja.getIdPuja(),
                subastaId,
                productoId,
                productoNombre,
                usuarioId,
                usuarioEmail,
                puja.getMonto(),
                puja.getFechaHora()
        );
    }

    public Puja toEntity(PujaDTO dto) {
        if (dto == null) {
            return null;
        }

        Puja puja = new Puja();
        puja.setIdPuja(dto.idPuja());
        if (dto.subastaId() != null) {
            Subasta subasta = new Subasta();
            subasta.setIdSubasta(dto.subastaId());
            puja.setSubasta(subasta);
        }
        if (dto.usuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.usuarioId());
            puja.setUsuario(usuario);
        }
        puja.setMonto(dto.monto());
        puja.setFechaHora(dto.fechaHora());
        return puja;
    }

    public List<PujaDTO> toDtoList(List<Puja> pujas) {
        if (pujas == null) {
            return new ArrayList<>();
        }

        return pujas.stream().map(this::toDto).collect(Collectors.toList());
    }
}
