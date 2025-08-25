package ar.edu.huergo.lcarera.restaurante.entity.plato;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "platos")
@Data // Lombok: genera getters, setters, equals, hashCode, toString, requiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Plato {
    // JPA: clave primaria autogenerada con estrategia IDENTITY
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Validación: no permite nulos ni cadenas en blanco
    // Validación: exige longitud mínima y máxima del nombre
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    // Validación: limita la longitud máxima de la descripción
    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;
    
    // Validación: el precio debe ser mayor a 0
    @Positive(message = "El precio debe ser mayor a 0")
    private double precio;

    @NotNull(message = "Los ingredientes son obligatorios")
    @NotEmpty(message = "Los ingredientes son obligatorios")
    @ManyToMany
    @JoinTable(name = "platos_ingredientes",
        joinColumns = @JoinColumn(name = "plato_id"),
        inverseJoinColumns = @JoinColumn(name = "ingrediente_id"))
    private List<Ingrediente> ingredientes;
}
