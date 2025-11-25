package ar.edu.huergo.fastbid.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PrestamoLibroResponseDTO {

    private Long id;
    private String tituloLibro;
    private String nombreUsuario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private Boolean devuelto;
}
