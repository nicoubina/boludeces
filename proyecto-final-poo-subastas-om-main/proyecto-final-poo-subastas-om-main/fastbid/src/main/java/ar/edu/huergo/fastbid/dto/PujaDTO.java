package ar.edu.huergo.fastbid.dto;

import java.time.LocalDateTime;

public record PujaDTO(
        Long idPuja,
        Long subastaId,
        Long productoId,
        String productoNombre,
        Long usuarioId,
        String usuarioEmail,
        double monto,
        LocalDateTime fechaHora
) {
}
