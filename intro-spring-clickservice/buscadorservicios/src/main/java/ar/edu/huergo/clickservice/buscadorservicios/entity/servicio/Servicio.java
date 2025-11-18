package ar.edu.huergo.clickservice.buscadorservicios.entity.servicio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//repository, entity, dto, >>>>>mapper, controller, service, 
/**
 * Entidad que representa un servicio en la plataforma ClickService.
 * 
 * Un servicio define el tipo de trabajo que puede realizar un proveedor
 * (plomería, gas, electricidad, albañilería, jardinería, carpintería)
 * junto con su precio por hora.
 */
@Entity
@Table(name = "servicios")
@Data // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    // JPA: clave primaria autogenerada con estrategia IDENTITY
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Validación: no permite nulos ni cadenas en blanco
    // Validación: exige longitud mínima y máxima del nombre
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre del servicio debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    // Validación: el precio por hora debe ser mayor a 0
    @NotNull(message = "El precio por hora es obligatorio")
    @Positive(message = "El precio por hora debe ser mayor a 0")
    @Column(nullable = false, name = "precio_hora")
    private Double precioHora;

    public void setDescripcion(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDescripcion'");
    }

    public void setCategoria(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCategoria'");
    }


}