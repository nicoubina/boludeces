package ar.edu.huergo.clickservice.buscadorservicios.mapper.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.ComisionDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Comision;

@Component
public class ComisionMapper {

    public ComisionDTO toDTO(Comision comision) {
        if (comision == null) {
            return null;
        }
        return new ComisionDTO(
                comision.getId(),
                comision.getPagoId(),
                comision.getTasa(),
                comision.getBase(),
                comision.getMonto(),
                comision.getFecha());
    }

    public Comision toEntity(ComisionDTO dto) {
        if (dto == null) {
            return null;
        }
        Comision comision = new Comision();
        comision.setId(dto.getId());
        comision.setPagoId(dto.getPagoId());
        comision.setTasa(dto.getTasa());
        comision.setBase(dto.getBase());
        comision.setMonto(dto.getMonto());
        comision.setFecha(dto.getFecha());
        return comision;
    }

    public List<ComisionDTO> toDTOList(List<Comision> comisiones) {
        if (comisiones == null) {
            return new ArrayList<>();
        }
        return comisiones.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Comision> toEntityList(List<ComisionDTO> comisionesDTO) {
        if (comisionesDTO == null) {
            return new ArrayList<>();
        }
        return comisionesDTO.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
