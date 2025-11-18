package ar.edu.huergo.clickservice.buscadorservicios.mapper.servicio;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.servicio.SolicitudServicioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.security.UsuarioMapper;

@Component
public class SolicitudServicioMapper {

    @Autowired
    private ServicioMapper servicioMapper;
    
    @Autowired
    private UsuarioMapper usuarioMapper;

    /**
     * Convierte una entidad SolicitudServicio a SolicitudServicioDTO
     */
    public SolicitudServicioDTO toDTO(SolicitudServicio solicitudServicio) {
        if (solicitudServicio == null) {
            return null;
        }

        SolicitudServicioDTO dto = new SolicitudServicioDTO();
        dto.setId(solicitudServicio.getId());
        dto.setServicio(servicioMapper.toDTO(solicitudServicio.getServicio()));
        dto.setCliente(usuarioMapper.toDTO(solicitudServicio.getCliente()));
        dto.setDescripcionProblema(solicitudServicio.getDescripcionProblema());
        dto.setDireccionServicio(solicitudServicio.getDireccionServicio());
        dto.setFechaSolicitada(solicitudServicio.getFechaSolicitada());
        dto.setFranjaHoraria(solicitudServicio.getFranjaHoraria());
        dto.setPresupuestoMaximo(solicitudServicio.getPresupuestoMaximo());
        dto.setEstado(solicitudServicio.getEstado());
        dto.setFechaCreacion(solicitudServicio.getFechaCreacion());
        dto.setComentariosAdicionales(solicitudServicio.getComentariosAdicionales());

        return dto;
    }

    /**
     * Convierte un SolicitudServicioDTO a entidad SolicitudServicio
     */
    public SolicitudServicio toEntity(SolicitudServicioDTO dto) {
        if (dto == null) {
            return null;
        }

        SolicitudServicio solicitudServicio = new SolicitudServicio();
        solicitudServicio.setId(dto.getId());
        solicitudServicio.setServicio(servicioMapper.toEntity(dto.getServicio()));
        solicitudServicio.setCliente(usuarioMapper.toEntity(dto.getCliente()));
        solicitudServicio.setDescripcionProblema(dto.getDescripcionProblema());
        solicitudServicio.setDireccionServicio(dto.getDireccionServicio());
        solicitudServicio.setFechaSolicitada(dto.getFechaSolicitada());
        solicitudServicio.setFranjaHoraria(dto.getFranjaHoraria());
        solicitudServicio.setPresupuestoMaximo(dto.getPresupuestoMaximo());
        solicitudServicio.setEstado(dto.getEstado());
        solicitudServicio.setFechaCreacion(dto.getFechaCreacion());
        solicitudServicio.setComentariosAdicionales(dto.getComentariosAdicionales());

        return solicitudServicio;
    }

    /**
     * Convierte una lista de entidades SolicitudServicio a lista de SolicitudServicioDTO
     */
    public List<SolicitudServicioDTO> toDTOList(List<SolicitudServicio> solicitudesServicio) {
        if (solicitudesServicio == null) {
            return null;
        }
        
        return solicitudesServicio.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de SolicitudServicioDTO a lista de entidades SolicitudServicio
     */
    public List<SolicitudServicio> toEntityList(List<SolicitudServicioDTO> solicitudesServicioDTO) {
        if (solicitudesServicioDTO == null) {
            return null;
        }
        
        return solicitudesServicioDTO.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}