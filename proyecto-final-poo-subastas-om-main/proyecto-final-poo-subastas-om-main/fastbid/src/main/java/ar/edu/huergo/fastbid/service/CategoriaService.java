package ar.edu.huergo.fastbid.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;
import ar.edu.huergo.fastbid.repository.categoria.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private HistorialService historialService;

    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll().stream()
                .sorted(Comparator.comparing(Categoria::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Categoria> obtenerCategoriasActivas() {
        return categoriaRepository.findByActivaTrue().stream()
                .sorted(Comparator.comparing(Categoria::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Categoria obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public Categoria crearCategoria(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        Categoria guardada = categoriaRepository.save(categoria);
        historialService.registrarEvento(HistorialTipoEvento.CATEGORIA_CREADA,
                String.format("Categoría \"%s\" creada", guardada.getNombre()));
        return guardada;
    }

    public Categoria actualizarCategoria(Long id, Categoria categoriaDetalles) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    // Verificar si el nuevo nombre ya existe en otra categoría
                    if (!categoria.getNombre().equals(categoriaDetalles.getNombre()) &&
                        categoriaRepository.existsByNombre(categoriaDetalles.getNombre())) {
                        throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaDetalles.getNombre());
                    }
                    
                    categoria.setNombre(categoriaDetalles.getNombre());
                    categoria.setDescripcion(categoriaDetalles.getDescripcion());
                    categoria.setActiva(categoriaDetalles.isActiva());
                    return categoriaRepository.save(categoria);
                })
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));

        categoriaRepository.delete(categoria);
        historialService.registrarEvento(HistorialTipoEvento.CATEGORIA_ELIMINADA,
                String.format("Categoría \"%s\" eliminada", categoria.getNombre()));
    }

    public void desactivarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    public void activarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        categoria.setActiva(true);
        categoriaRepository.save(categoria);
    }

    public int eliminarCategoriasPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        List<Categoria> categorias = categoriaRepository.findAllById(ids);
        if (categorias.isEmpty()) {
            return 0;
        }

        categoriaRepository.deleteAll(categorias);
        categorias.forEach(categoria -> historialService.registrarEvento(HistorialTipoEvento.CATEGORIA_ELIMINADA,
                String.format("Categoría \"%s\" eliminada", categoria.getNombre())));
        return categorias.size();
    }
}