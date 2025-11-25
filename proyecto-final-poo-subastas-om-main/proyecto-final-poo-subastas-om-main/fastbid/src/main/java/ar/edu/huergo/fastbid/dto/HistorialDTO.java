package ar.edu.huergo.fastbid.dto;

import java.time.Instant;

import ar.edu.huergo.fastbid.entity.historial.HistorialTipoEvento;

public record HistorialDTO(
        Long idHistorial,
        Long subastaId,
        Long productoId,
        String productoNombre,
        Long usuarioId,
        String usuarioEmail,
        HistorialTipoEvento tipoEvento,
        String descripcion,
        Instant fechaHora
) {
}
