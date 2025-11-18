package ar.edu.huergo.clickservice.buscadorservicios.entity.security;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Column(nullable = false, length = 60)
    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    @NotBlank(message = "El DNI es requerido")
    @Pattern(regexp = "^\\d{7,10}$", message = "El DNI debe tener entre 7 y 10 dígitos")
    private String dni;

    @Column(nullable = false, length = 25)
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,19}$", message = "El formato del teléfono no es válido")
    private String telefono;

    @Column(nullable = false, length = 120)
    @NotBlank(message = "La calle es requerida")
    private String calle;

    @Column(nullable = false)
    @NotNull(message = "La altura es requerida")
    @Positive(message = "La altura debe ser un número positivo")
    private Integer altura;

    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "El nombre de usuario debe ser un email válido")
    @NotBlank(message = "El nombre de usuario es requerido")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 16, message = "La contraseña debe tener al menos 16 caracteres")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }
}


