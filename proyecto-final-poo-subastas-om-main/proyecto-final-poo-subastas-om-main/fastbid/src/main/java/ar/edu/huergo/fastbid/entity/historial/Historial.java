package ar.edu.huergo.fastbid.entity.historial;

import java.time.Instant;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "historiales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "subasta_id",
            foreignKey = @ForeignKey(name = "fk_historial_subasta"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Subasta subasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id",
            foreignKey = @ForeignKey(name = "fk_historial_producto"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_historial_usuario"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @NotNull(message = "El tipo de evento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 40)
    private HistorialTipoEvento tipoEvento;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción debe tener como máximo 255 caracteres")
    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;
}
