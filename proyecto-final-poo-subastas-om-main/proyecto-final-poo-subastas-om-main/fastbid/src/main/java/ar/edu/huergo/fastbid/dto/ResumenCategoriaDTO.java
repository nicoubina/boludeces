package ar.edu.huergo.fastbid.dto;

import lombok.Data;

@Data
public class ResumenCategoriaDTO {
    private String nombreCategoria;
    private Integer totalProductos;
    private Double valorTotalInventario;
    private String productoMasCaro;
    private String productoMasBarato;
    private Double promedioPrecios;
}
