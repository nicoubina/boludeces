package ar.edu.huergo.fastbid.controller.categoria;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ar.edu.huergo.fastbid.dto.CategoriaDTO;
import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.mapper.CategoriaMapper;
import ar.edu.huergo.fastbid.service.CategoriaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaMapper categoriaMapper;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerCategorias() {
        List<Categoria> categorias = categoriaService.obtenerCategorias();
        List<CategoriaDTO> categoriasDto = categoriaMapper.toDtoList(categorias);
        return ResponseEntity.ok(categoriasDto);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaDTO>> obtenerCategoriasActivas() {
        List<Categoria> categorias = categoriaService.obtenerCategoriasActivas();
        List<CategoriaDTO> categoriasDto = categoriaMapper.toDtoList(categorias);
        return ResponseEntity.ok(categoriasDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
        CategoriaDTO categoriaDto = categoriaMapper.toDto(categoria);
        return ResponseEntity.ok(categoriaDto);
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDto) {
        Categoria categoria = categoriaMapper.toEntity(categoriaDto);
        Categoria categoriaCreada = categoriaService.crearCategoria(categoria);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(categoriaCreada.getIdCategoria()).toUri();
        return ResponseEntity.created(location).body(categoriaMapper.toDto(categoriaCreada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id, 
                                                            @Valid @RequestBody CategoriaDTO categoriaDto) {
        Categoria categoria = categoriaMapper.toEntity(categoriaDto);
        Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoria);
        return ResponseEntity.ok(categoriaMapper.toDto(categoriaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable Long id) {
        categoriaService.desactivarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarCategoria(@PathVariable Long id) {
        categoriaService.activarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}