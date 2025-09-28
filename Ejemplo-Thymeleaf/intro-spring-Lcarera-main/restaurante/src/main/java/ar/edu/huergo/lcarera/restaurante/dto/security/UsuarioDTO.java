package ar.edu.huergo.lcarera.restaurante.dto.security;

import java.util.List;

public record UsuarioDTO(String username, List<String> roles) {
    
}
