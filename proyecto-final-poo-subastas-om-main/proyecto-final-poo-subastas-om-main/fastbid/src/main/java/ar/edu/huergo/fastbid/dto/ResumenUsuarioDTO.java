package ar.edu.huergo.fastbid.dto;

import lombok.Data;

@Data
public class ResumenUsuarioDTO {

    private String nombreUsuario;
    private Integer totalPrestamos;
    private Integer prestamosActivos;
    private Integer prestamosVencidos;
    private String libroMasPrestado;
    private Double tasaDevolucionPuntual;
}
