package ar.edu.huergo.fastbid.dto.security;

import java.util.List;

public record UsuarioDTO(String username, List<String> roles) {
    
}
