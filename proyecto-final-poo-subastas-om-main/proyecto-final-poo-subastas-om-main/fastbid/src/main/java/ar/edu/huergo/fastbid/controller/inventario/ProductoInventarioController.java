package ar.edu.huergo.fastbid.controller.inventario;

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

import ar.edu.huergo.fastbid.dto.ActualizarStockDTO;
import ar.edu.huergo.fastbid.dto.ProductoInventarioRequestDTO;
import ar.edu.huergo.fastbid.dto.ProductoInventarioResponseDTO;
import ar.edu.huergo.fastbid.dto.ResumenCategoriaDTO;
import ar.edu.huergo.fastbid.entity.inventario.ProductoInventario;
import ar.edu.huergo.fastbid.mapper.ProductoInventarioMapper;
import ar.edu.huergo.fastbid.service.ProductoInventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventario/productos")
public class ProductoInventarioController {

    @Autowired
    private ProductoInventarioService productoInventarioService;

    @Autowired
    private ProductoInventarioMapper productoInventarioMapper;

    @GetMapping
    public ResponseEntity<List<ProductoInventarioResponseDTO>> obtenerProductos() {
        List<ProductoInventario> productos = productoInventarioService.obtenerTodos();
        return ResponseEntity.ok(productoInventarioMapper.toDtoList(productos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoInventarioResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        ProductoInventario producto = productoInventarioService.obtenerPorId(id);
        return ResponseEntity.ok(productoInventarioMapper.toDto(producto));
    }

    @PostMapping
    public ResponseEntity<ProductoInventarioResponseDTO> crearProducto(
            @Valid @RequestBody ProductoInventarioRequestDTO request) {
        ProductoInventario producto = productoInventarioMapper.toEntity(request);
        ProductoInventario guardado = productoInventarioService.crear(producto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(location).body(productoInventarioMapper.toDto(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoInventarioResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoInventarioRequestDTO request) {
        ProductoInventario detalles = productoInventarioMapper.toEntity(request);
        ProductoInventario actualizado = productoInventarioService.actualizar(id, detalles);
        return ResponseEntity.ok(productoInventarioMapper.toDto(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoInventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoInventarioResponseDTO>> obtenerPorCategoria(@PathVariable String categoria) {
        List<ProductoInventario> productos = productoInventarioService.filtrarPorCategoria(categoria);
        return ResponseEntity.ok(productoInventarioMapper.toDtoList(productos));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoInventarioResponseDTO> actualizarStock(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarStockDTO stockRequest) {
        ProductoInventario actualizado = productoInventarioService.actualizarStock(id, stockRequest.getStock());
        return ResponseEntity.ok(productoInventarioMapper.toDto(actualizado));
    }

    @GetMapping("/categoria/{categoria}/resumen")
    public ResponseEntity<ResumenCategoriaDTO> obtenerResumen(@PathVariable String categoria) {
        ResumenCategoriaDTO resumen = productoInventarioService.obtenerResumenPorCategoria(categoria);
        return ResponseEntity.ok(resumen);
    }
}
