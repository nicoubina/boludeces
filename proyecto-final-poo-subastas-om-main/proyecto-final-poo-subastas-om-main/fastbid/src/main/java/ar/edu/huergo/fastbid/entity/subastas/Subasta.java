package ar.edu.huergo.fastbid.entity.subastas;

import java.time.LocalDateTime;
import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "subastas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Subasta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubasta;

    @NotNull(message = "El producto es obligatorio")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_subasta_producto"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Producto producto;     // FK → Producto.id (único: 1–a–1)

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Positive(message = "El precio inicial debe ser mayor a 0")
    @Column(nullable = false)
    private double precioInicial;

    @Positive(message = "El precio actual debe ser mayor a 0")
    @Column(nullable = false)
    private double precioActual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "El estado es obligatorio")
    private SubastaEstado estado; // ACTIVA | FINALIZADA | CANCELADA | PROGRAMADA

    // Opcionales
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ganador_id",
            foreignKey = @ForeignKey(name = "fk_subasta_ganador"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario ganador;       // FK → Usuario.id (opcional)

    //@Column(nullable = false)
    @Positive(message = "El incremento mínimo debe ser mayor a 0")
    private double incrementoMinimo; // Valor mínimo para superar la puja anterior

    @Positive(message = "La compra inmediata debe ser mayor a 0")
    private Double compraInmediata;  // Precio de compra directa (opcional)

}