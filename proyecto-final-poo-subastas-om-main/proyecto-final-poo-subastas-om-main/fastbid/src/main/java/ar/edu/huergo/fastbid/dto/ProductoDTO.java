package ar.edu.huergo.fastbid.dto;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.huergo.fastbid.entity.Categoria;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.Subasta;

public record ProductoDTO(
        Long idProducto,
        String nombre,
        String descripcion,
        double precioInicial,
        List<String> imagenes,
        Categoria categoria,
        String estado,
        LocalDateTime fechaPublicacion,
        LocalDateTime fechaFin,
        Usuario usuario,
        Subasta subasta,
        Double precioCompraInmediata,
        String condicion,
        String ubicacion,
        int cantidad
) {}