package ar.edu.huergo.lcarera.restaurante.service.plato;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.lcarera.restaurante.entity.plato.Ingrediente;
import ar.edu.huergo.lcarera.restaurante.repository.plato.IngredienteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class IngredienteService {
    @Autowired
    private IngredienteRepository ingredienteRepository;

    public List<Ingrediente> obtenerTodosLosIngredientes() {
        return ingredienteRepository.findAll();
    }

    public Ingrediente obtenerIngredientePorId(Long id) throws EntityNotFoundException {
        return ingredienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ingrediente no encontrado"));
    }

    public Ingrediente crearIngrediente(Ingrediente ingrediente) {
        return ingredienteRepository.save(ingrediente);
    }

    public Ingrediente actualizarIngrediente(Long id, Ingrediente ingrediente) throws EntityNotFoundException {
        Ingrediente ingredienteExistente = obtenerIngredientePorId(id);
        ingredienteExistente.setNombre(ingrediente.getNombre());
        return ingredienteRepository.save(ingredienteExistente);
    }
    
    public void eliminarIngrediente(Long id) throws EntityNotFoundException {
        Ingrediente ingrediente = obtenerIngredientePorId(id);
        ingredienteRepository.delete(ingrediente);
    }

    public List<Ingrediente> obtenerIngredientesPorNombre(String nombre) {
        return ingredienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Ingrediente> resolverIngredientes(List<Long> ingredientesIds) throws IllegalArgumentException, EntityNotFoundException {
        if (ingredientesIds == null || ingredientesIds.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un ingrediente");
        }
        List<Ingrediente> ingredientes = ingredienteRepository.findAllById(ingredientesIds);
        if (ingredientes.size() != ingredientesIds.stream().filter(Objects::nonNull).distinct()
                .count()) {
            throw new EntityNotFoundException("Uno o más ingredientes no existen");
        }
        return ingredientes;
    }
}
