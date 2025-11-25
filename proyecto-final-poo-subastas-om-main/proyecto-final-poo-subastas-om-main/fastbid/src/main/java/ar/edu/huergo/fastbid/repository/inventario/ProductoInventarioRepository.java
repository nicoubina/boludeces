package ar.edu.huergo.fastbid.repository.inventario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.huergo.fastbid.entity.inventario.ProductoInventario;

public interface ProductoInventarioRepository extends JpaRepository<ProductoInventario, Long> {
    List<ProductoInventario> findByCategoriaIgnoreCase(String categoria);
}
