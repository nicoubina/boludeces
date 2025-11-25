package ar.edu.huergo.fastbid.controller.producto;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ar.edu.huergo.fastbid.dto.ProductoDTO;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.mapper.ProductoMapper;
import ar.edu.huergo.fastbid.service.ProductoService;
import jakarta.validation.Valid;




@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoMapper productoMapper;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerProductos() {
        List<Producto> productos = productoService.obtenerProductos();
        List<ProductoDTO> productosDto = productoMapper.toDtoList(productos);
        return ResponseEntity.ok(productosDto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.obteneProductoPorId(id);
        ProductoDTO productoDto = productoMapper.toDto(producto);
        return ResponseEntity.ok(productoDto);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDto) {
        Producto producto = productoMapper.toEntity(productoDto);
        Producto productoCreado = productoService.crearProducto(producto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(productoCreado.getIdProducto()).toUri();
        return ResponseEntity.created(location).body(productoMapper.toDto(productoCreado));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDto) {
        Producto producto = productoMapper.toEntity(productoDto);
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        return ResponseEntity.ok(productoMapper.toDto(productoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
