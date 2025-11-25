package ar.edu.huergo.fastbid.dto;

import java.time.LocalDateTime;

import ar.edu.huergo.fastbid.entity.Producto;
import ar.edu.huergo.fastbid.entity.security.Usuario;
import ar.edu.huergo.fastbid.entity.subastas.SubastaEstado;

public record SubastaDTO(
        Long idSubasta,
        Producto producto,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        double precioInicial,
        double precioActual,
        SubastaEstado estado,
        Usuario ganador,
        double incrementoMinimo,
        Double compraInmediata
) {
}
