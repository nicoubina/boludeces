package ar.edu.huergo.clickservice.buscadorservicios.dto.security;

import java.util.List;

public record UsuarioDTO(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String telefono,
        String calle,
        Integer altura,
        String username,
        List<String> roles) {

}
