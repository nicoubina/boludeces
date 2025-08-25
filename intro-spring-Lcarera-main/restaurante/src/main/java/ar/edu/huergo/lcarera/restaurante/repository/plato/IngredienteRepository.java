package ar.edu.huergo.lcarera.restaurante.repository.plato;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    List<Ingrediente> findByNombreContainingIgnoreCase(String nombre);
}
