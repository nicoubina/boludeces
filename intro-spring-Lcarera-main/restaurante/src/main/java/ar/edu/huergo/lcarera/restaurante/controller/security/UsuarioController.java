package ar.edu.huergo.lcarera.restaurante.controller.security;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.huergo.lcarera.restaurante.dto.security.RegistrarDTO;
import ar.edu.huergo.lcarera.restaurante.dto.security.UsuarioDTO;
import ar.edu.huergo.lcarera.restaurante.entity.security.Usuario;
import ar.edu.huergo.lcarera.restaurante.mapper.security.UsuarioMapper;
import ar.edu.huergo.lcarera.restaurante.service.security.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrarCliente(@Valid @RequestBody RegistrarDTO registrarDTO) {
        Usuario usuario = usuarioMapper.toEntity(registrarDTO);
        Usuario nuevoUsuario = usuarioService.registrar(usuario, registrarDTO.password(), registrarDTO.verificacionPassword());
        UsuarioDTO nuevoUsuarioDTO = usuarioMapper.toDTO(nuevoUsuario);
        return ResponseEntity.ok(nuevoUsuarioDTO);
    }


    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        List<UsuarioDTO> usuarioDTOs = usuarioMapper.toDTOList(usuarios);
        return ResponseEntity.ok(usuarioDTOs);
    }
    
}
