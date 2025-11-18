package ar.edu.huergo.clickservice.buscadorservicios.mapper.security;

import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.security.RegistrarDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.security.UsuarioDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Rol;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;

@Component
public class UsuarioMapper {
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getTelefono(),
                usuario.getCalle(),
                usuario.getAltura(),
                usuario.getUsername(),
                usuario.getRoles().stream()
                .map(Rol::getNombre)
                .toList());
    }

    public List<UsuarioDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toDTO)
                .toList();
    }

    public Usuario toEntity(RegistrarDTO registrarDTO) {
        if (registrarDTO == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(registrarDTO.nombre());
        usuario.setApellido(registrarDTO.apellido());
        usuario.setDni(registrarDTO.dni());
        usuario.setTelefono(registrarDTO.telefono());
        usuario.setCalle(registrarDTO.calle());
        usuario.setAltura(registrarDTO.altura());
        usuario.setUsername(registrarDTO.username());
        return usuario;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setId(dto.id());
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setDni(dto.dni());
        usuario.setTelefono(dto.telefono());
        usuario.setCalle(dto.calle());
        usuario.setAltura(dto.altura());
        usuario.setUsername(dto.username());
        return usuario;
    }
}
