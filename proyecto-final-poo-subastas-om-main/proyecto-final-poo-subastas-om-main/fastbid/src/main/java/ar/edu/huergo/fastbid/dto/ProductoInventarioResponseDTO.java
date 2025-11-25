package ar.edu.huergo.fastbid.dto;

import lombok.Data;

@Data
public class ProductoInventarioResponseDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private Double precio;
    private Integer stock;
}
