package ar.edu.huergo.fastbid.dto;

public record CategoriaDTO(
        Long idCategoria,
        String nombre,
        String descripcion,
        boolean activa
) {
}