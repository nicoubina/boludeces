package ar.edu.huergo.lcarera.restaurante.service.plato;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Ingrediente;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;
import ar.edu.huergo.lcarera.restaurante.repository.plato.PlatoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PlatoService {
    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private IngredienteService ingredienteService;

    public List<Plato> obtenerTodosLosPlatos() {
        return platoRepository.findAll();
    }

    public Plato obtenerPlatoPorId(Long id) throws EntityNotFoundException {
        return platoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado"));
    }

    public Plato crearPlato(Plato plato, List<Long> ingredientesIds) {
        List<Ingrediente> ingredientes = ingredienteService.resolverIngredientes(ingredientesIds);
        plato.setIngredientes(ingredientes);
        return platoRepository.save(plato);
    }

    public Plato actualizarPlato(Long id, Plato plato, List<Long> ingredientesIds) throws EntityNotFoundException {
        Plato platoExistente = obtenerPlatoPorId(id);
        platoExistente.setNombre(plato.getNombre());
        platoExistente.setDescripcion(plato.getDescripcion());
        platoExistente.setPrecio(plato.getPrecio());
        if (ingredientesIds != null) {
            List<Ingrediente> ingredientes = ingredienteService.resolverIngredientes(ingredientesIds);
            platoExistente.setIngredientes(ingredientes);
        }
        return platoRepository.save(platoExistente);
    }

    public void eliminarPlato(Long id) throws EntityNotFoundException {
        Plato plato = obtenerPlatoPorId(id);
        platoRepository.delete(plato);
    }
}
