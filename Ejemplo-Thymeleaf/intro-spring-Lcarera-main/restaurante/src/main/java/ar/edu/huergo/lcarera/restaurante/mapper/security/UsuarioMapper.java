package ar.edu.huergo.lcarera.restaurante.mapper.security;

import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.huergo.lcarera.restaurante.dto.security.RegistrarDTO;
import ar.edu.huergo.lcarera.restaurante.dto.security.UsuarioDTO;
import ar.edu.huergo.lcarera.restaurante.entity.security.Rol;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;

@Component
public class UsuarioMapper {
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioDTO(usuario.getUsername(), usuario.getRoles().stream()
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
        usuario.setUsername(registrarDTO.username());
        return usuario;
    }
}
