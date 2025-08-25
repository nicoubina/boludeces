package ar.edu.huergo.lcarera.restaurante.repository.plato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {
}