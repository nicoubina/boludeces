package ar.edu.huergo.fastbid.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoForm{

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 250, message = "La descripción no puede superar 250 caracteres")
    private String descripcion;

    @NotNull(message = "El precio inicial es obligatorio")
    @Positive(message = "El precio inicial debe ser mayor a 0")
    private Double precioInicial;

    @NotBlank(message = "Debes ingresar al menos una URL de imagen")
    private String imagenesTexto;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La fecha de finalización es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;

    @Positive(message = "El precio de compra inmediata debe ser mayor a 0")
    private Double precioCompraInmediata;

    private String condicion;

    private String ubicacion;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
}
