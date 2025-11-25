package ar.edu.huergo.fastbid.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.fastbid.dto.SubastaDTO;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;

@Component
public class SubastaMapper {

    public SubastaDTO toDto(Subasta subasta) {
        if (subasta == null) {
            return null;
        }

        return new SubastaDTO(
                subasta.getIdSubasta(),
                subasta.getProducto(),
                subasta.getFechaInicio(),
                subasta.getFechaFin(),
                subasta.getPrecioInicial(),
                subasta.getPrecioActual(),
                subasta.getEstado(),
                subasta.getGanador(),
                subasta.getIncrementoMinimo(),
                subasta.getCompraInmediata()
        );
    }

    public Subasta toEntity(SubastaDTO dto) {
        if (dto == null) {
            return null;
        }

        Subasta subasta = new Subasta();
        subasta.setIdSubasta(dto.idSubasta());
        subasta.setProducto(dto.producto());
        subasta.setFechaInicio(dto.fechaInicio());
        subasta.setFechaFin(dto.fechaFin());
        subasta.setPrecioInicial(dto.precioInicial());
        subasta.setPrecioActual(dto.precioActual());
        subasta.setEstado(dto.estado());
        subasta.setGanador(dto.ganador());
        subasta.setIncrementoMinimo(dto.incrementoMinimo());
        subasta.setCompraInmediata(dto.compraInmediata());

        return subasta;
    }

    public List<SubastaDTO> toDtoList(List<Subasta> subastas) {
        if (subastas == null) {
            return new ArrayList<>();
        }
        return subastas.stream().map(this::toDto).collect(Collectors.toList());
    }
}
