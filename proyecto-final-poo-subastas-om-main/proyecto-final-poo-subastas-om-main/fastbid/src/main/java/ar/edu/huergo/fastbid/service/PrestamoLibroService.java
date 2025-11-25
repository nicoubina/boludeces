package ar.edu.huergo.fastbid.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.huergo.fastbid.dto.ResumenUsuarioDTO;
import ar.edu.huergo.fastbid.entity.prestamo.PrestamoLibro;
import ar.edu.huergo.fastbid.repository.prestamo.PrestamoLibroRepository;

@Service
public class PrestamoLibroService {

    @Autowired
    private PrestamoLibroRepository prestamoLibroRepository;

    @Transactional(readOnly = true)
    public List<PrestamoLibro> obtenerTodos() {
        return prestamoLibroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PrestamoLibro obtenerPorId(Long id) {
        return prestamoLibroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));
    }

    public PrestamoLibro crear(PrestamoLibro prestamo) {
        LocalDate hoy = LocalDate.now();
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaDevolucion(hoy.plusDays(prestamo.getDiasPrestamo()));
        prestamo.setDevuelto(Boolean.FALSE);
        return prestamoLibroRepository.save(prestamo);
    }

    public PrestamoLibro actualizar(Long id, PrestamoLibro detalles) {
        return prestamoLibroRepository.findById(id)
                .map(actual -> {
                    actual.setTituloLibro(detalles.getTituloLibro());
                    actual.setNombreUsuario(detalles.getNombreUsuario());
                    actual.setDiasPrestamo(detalles.getDiasPrestamo());
                    if (actual.getFechaPrestamo() == null) {
                        actual.setFechaPrestamo(LocalDate.now());
                    }
                    actual.setFechaDevolucion(actual.getFechaPrestamo().plusDays(detalles.getDiasPrestamo()));
                    return prestamoLibroRepository.save(actual);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));
    }

    public void eliminar(Long id) {
        if (!prestamoLibroRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado");
        }
        prestamoLibroRepository.deleteById(id);
    }

    public PrestamoLibro marcarDevuelto(Long id) {
        PrestamoLibro prestamo = obtenerPorId(id);
        if (Boolean.TRUE.equals(prestamo.getDevuelto())) {
            return prestamo;
        }
        prestamo.setDevuelto(Boolean.TRUE);
        return prestamoLibroRepository.save(prestamo);
    }

    @Transactional(readOnly = true)
    public List<PrestamoLibro> obtenerVencidos() {
        LocalDate hoy = LocalDate.now();
        return prestamoLibroRepository.findByDevueltoFalseAndFechaDevolucionBefore(hoy);
    }

    @Transactional(readOnly = true)
    public List<PrestamoLibro> obtenerHistorialPorUsuario(String nombreUsuario) {
        return prestamoLibroRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
    }

    @Transactional(readOnly = true)
    public ResumenUsuarioDTO obtenerResumenPorUsuario(String nombreUsuario) {
        List<PrestamoLibro> prestamos = prestamoLibroRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if (prestamos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron préstamos para el usuario indicado");
        }

        long activos = prestamos.stream()
                .filter(p -> Boolean.FALSE.equals(p.getDevuelto()))
                .count();

        long vencidos = prestamos.stream()
                .filter(p -> Boolean.FALSE.equals(p.getDevuelto()) && p.getFechaDevolucion().isBefore(LocalDate.now()))
                .count();

        Map<String, Long> conteoLibros = prestamos.stream()
                .collect(Collectors.groupingBy(PrestamoLibro::getTituloLibro, Collectors.counting()));

        String libroMasPrestado = conteoLibros.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        long devueltos = prestamos.stream()
                .filter(PrestamoLibro::getDevuelto)
                .count();

        double tasaDevolucion = prestamos.isEmpty() ? 0 : (devueltos * 100.0) / prestamos.size();

        ResumenUsuarioDTO resumen = new ResumenUsuarioDTO();
        resumen.setNombreUsuario(nombreUsuario);
        resumen.setTotalPrestamos(prestamos.size());
        resumen.setPrestamosActivos((int) activos);
        resumen.setPrestamosVencidos((int) vencidos);
        resumen.setLibroMasPrestado(libroMasPrestado);
        resumen.setTasaDevolucionPuntual(tasaDevolucion);
        return resumen;
    }
}
