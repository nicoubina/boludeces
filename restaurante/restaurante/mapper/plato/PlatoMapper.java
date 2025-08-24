package ar.edu.huergo.lcarera.restaurante.mapper.plato;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.huergo.lcarera.restaurante.dto.plato.CrearActualizarPlatoDTO;
import ar.edu.huergo.lcarera.restaurante.dto.plato.IngredienteDTO;
import ar.edu.huergo.lcarera.restaurante.dto.plato.MostrarPlatoDTO;
import ar.edu.huergo.lcarera.restaurante.entity.plato.Plato;

// Un Mapper es una clase que se encarga de convertir un objeto de un tipo a otro.
// En este caso, se encarga de convertir un objeto Plato a un objeto PlatoDTO y viceversa.
// Esto es útil para evitar que el controlador se encargue de la conversión de objetos.
@Component
public class PlatoMapper {

    @Autowired
    private IngredienteMapper ingredienteMapper;

    /**
     * Convierte una entidad Plato a PlatoDTO
     */
    public MostrarPlatoDTO toDTO(Plato plato) {
        if (plato == null) {
            return null;
        }
        List<IngredienteDTO> ingredientes = ingredienteMapper.toDTOList(plato.getIngredientes());
        return new MostrarPlatoDTO(plato.getId(), plato.getNombre(), plato.getDescripcion(),
                plato.getPrecio(), ingredientes);
    }

    /**
     * Convierte un PlatoDTO a entidad Plato
     */
    public Plato toEntity(CrearActualizarPlatoDTO dto) {
        if (dto == null) {
            return null;
        }
        Plato plato = new Plato();
        plato.setNombre(dto.getNombre());
        plato.setDescripcion(dto.getDescripcion());
        plato.setPrecio(dto.getPrecio());
        return plato;
    }

    /**
     * Convierte una lista de entidades Plato a lista de PlatoDTO
     */
    public List<MostrarPlatoDTO> toDTOList(List<Plato> platos) {
        if (platos == null) {
            return new ArrayList<>();
        }
        return platos.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
