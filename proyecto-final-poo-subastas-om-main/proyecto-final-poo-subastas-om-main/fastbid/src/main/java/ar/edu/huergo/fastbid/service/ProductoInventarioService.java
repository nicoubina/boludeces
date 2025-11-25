package ar.edu.huergo.fastbid.service;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.huergo.fastbid.dto.ResumenCategoriaDTO;
import ar.edu.huergo.fastbid.entity.inventario.ProductoInventario;
import ar.edu.huergo.fastbid.repository.inventario.ProductoInventarioRepository;

@Service
public class ProductoInventarioService {

    @Autowired
    private ProductoInventarioRepository productoInventarioRepository;

    @Transactional(readOnly = true)
    public List<ProductoInventario> obtenerTodos() {
        return productoInventarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductoInventario obtenerPorId(Long id) {
        return productoInventarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public ProductoInventario crear(ProductoInventario producto) {
        if (producto.getStock() == null) {
            producto.setStock(0);
        }
        return productoInventarioRepository.save(producto);
    }

    public ProductoInventario actualizar(Long id, ProductoInventario detalles) {
        return productoInventarioRepository.findById(id)
                .map(actual -> {
                    actual.setNombre(detalles.getNombre());
                    actual.setCategoria(detalles.getCategoria());
                    actual.setPrecio(detalles.getPrecio());
                    return productoInventarioRepository.save(actual);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public void eliminar(Long id) {
        if (!productoInventarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        productoInventarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductoInventario> filtrarPorCategoria(String categoria) {
        return productoInventarioRepository.findByCategoriaIgnoreCase(categoria);
    }

    public ProductoInventario actualizarStock(Long id, Integer stock) {
        if (stock == null || stock < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock debe ser 0 o mayor");
        }

        return productoInventarioRepository.findById(id)
                .map(producto -> {
                    producto.setStock(stock);
                    return productoInventarioRepository.save(producto);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Transactional(readOnly = true)
    public ResumenCategoriaDTO obtenerResumenPorCategoria(String categoria) {
        List<ProductoInventario> productos = productoInventarioRepository.findByCategoriaIgnoreCase(categoria);

        if (productos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay productos para la categoría indicada");
        }

        double valorTotal = productos.stream()
                .mapToDouble(p -> p.getPrecio() * p.getStock())
                .sum();

        OptionalDouble promedioOptional = productos.stream()
                .mapToDouble(ProductoInventario::getPrecio)
                .average();

        ProductoInventario masCaro = productos.stream()
                .max(Comparator.comparing(ProductoInventario::getPrecio))
                .orElse(null);

        ProductoInventario masBarato = productos.stream()
                .min(Comparator.comparing(ProductoInventario::getPrecio))
                .orElse(null);

        ResumenCategoriaDTO resumen = new ResumenCategoriaDTO();
        resumen.setNombreCategoria(categoria);
        resumen.setTotalProductos(productos.size());
        resumen.setValorTotalInventario(valorTotal);
        resumen.setProductoMasCaro(masCaro != null ? masCaro.getNombre() : null);
        resumen.setProductoMasBarato(masBarato != null ? masBarato.getNombre() : null);
        resumen.setPromedioPrecios(promedioOptional.orElse(0));
        return resumen;
    }
}
