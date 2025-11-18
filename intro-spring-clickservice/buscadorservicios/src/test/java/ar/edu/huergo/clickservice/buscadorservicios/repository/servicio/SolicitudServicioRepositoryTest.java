package ar.edu.huergo.clickservice.buscadorservicios.repository.servicio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio.EstadoSolicitud;

@DataJpaTest
class SolicitudServicioRepositoryTest {

    @Autowired
    private SolicitudServicioRepository solicitudServicioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deberiaEncontrarSolicitudesPorCliente() {
        Servicio servicioLimpieza = persistirServicio(1L);
        Servicio servicioPintura = persistirServicio(2L);

        Usuario clienteJuan = persistirUsuario(1L, "Juan", "Perez");
        Usuario clienteAna = persistirUsuario(2L, "Ana", "Gomez");

        persistirSolicitud(10L, servicioLimpieza, clienteJuan, EstadoSolicitud.PENDIENTE);
        persistirSolicitud(11L, servicioPintura, clienteJuan, EstadoSolicitud.ASIGNADA);
        persistirSolicitud(12L, servicioPintura, clienteAna, EstadoSolicitud.PENDIENTE);

        entityManager.clear();

        List<SolicitudServicio> solicitudes =
            solicitudServicioRepository.findByClienteId(clienteJuan.getId());

        assertThat(solicitudes)
            .hasSize(2)
            .allMatch(solicitud -> solicitud.getCliente().getId().equals(clienteJuan.getId()));
    }

    @Test
    void deberiaEncontrarSolicitudesPorEstado() {
        Servicio servicioGas = persistirServicio(3L);
        Usuario clienteLuis = persistirUsuario(3L, "Luis", "Martinez");
        Usuario clienteLaura = persistirUsuario(4L, "Laura", "Lopez");

        persistirSolicitud(20L, servicioGas, clienteLuis, EstadoSolicitud.PENDIENTE);
        persistirSolicitud(21L, servicioGas, clienteLaura, EstadoSolicitud.PENDIENTE);
        persistirSolicitud(22L, servicioGas, clienteLuis, EstadoSolicitud.CANCELADA);

        entityManager.clear();

        List<SolicitudServicio> solicitudesPendientes =
            solicitudServicioRepository.findByEstado(EstadoSolicitud.PENDIENTE);

        assertThat(solicitudesPendientes)
            .hasSize(2)
            .allMatch(solicitud -> solicitud.getEstado() == EstadoSolicitud.PENDIENTE);
    }

    @Test
    void deberiaEncontrarSolicitudesPorServicioYEstado() {
        Servicio servicioPlomeria = persistirServicio(5L);
        Servicio servicioElectricidad = persistirServicio(6L);

        Usuario clienteMario = persistirUsuario(5L, "Mario", "Diaz");
        Usuario clienteRosa = persistirUsuario(6L, "Rosa", "Suarez");

        persistirSolicitud(30L, servicioPlomeria, clienteMario, EstadoSolicitud.PENDIENTE);
        persistirSolicitud(31L, servicioPlomeria, clienteRosa, EstadoSolicitud.PENDIENTE);
        persistirSolicitud(32L, servicioPlomeria, clienteMario, EstadoSolicitud.COMPLETADA);
        persistirSolicitud(33L, servicioElectricidad, clienteMario, EstadoSolicitud.PENDIENTE);

        entityManager.clear();

        List<SolicitudServicio> solicitudesPendientes =
            solicitudServicioRepository.findByServicioIdAndEstado(
                servicioPlomeria.getId(), EstadoSolicitud.PENDIENTE);

        assertThat(solicitudesPendientes)
            .hasSize(2)
            .allMatch(solicitud -> solicitud.getServicio().getId().equals(servicioPlomeria.getId())
                && solicitud.getEstado() == EstadoSolicitud.PENDIENTE);
    }

    private SolicitudServicio persistirSolicitud(Long indice, Servicio servicio, Usuario cliente, EstadoSolicitud estado) {
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicio);
        solicitud.setCliente(cliente);
        solicitud.setDescripcionProblema(
            "Descripcion detallada del problema numero " + indice + " con mas de diez caracteres");
        solicitud.setDireccionServicio("Calle " + indice + " 1234, Piso 1");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(indice.intValue() + 1));
        solicitud.setFranjaHoraria("09:00-12:00");
        solicitud.setPresupuestoMaximo(2500.0 + indice);
        solicitud.setComentariosAdicionales("Comentario adicional para solicitud " + indice);
        solicitud.setEstado(estado);

        entityManager.persist(solicitud);
        entityManager.flush();
        return solicitud;
    }

    private Servicio persistirServicio(Long indice) {
        Servicio servicio = new Servicio();
        servicio.setNombre("Servicio " + indice + " Especializado");
        servicio.setPrecioHora(1800.0 + indice);

        entityManager.persist(servicio);
        entityManager.flush();
        return servicio;
    }

    private Usuario persistirUsuario(Long indice, String nombre, String apellido) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDni(String.format("%08d", 30000000 + indice));
        usuario.setTelefono("+5491100" + String.format("%04d", indice));
        usuario.setCalle("Calle Principal " + indice);
        usuario.setAltura(500 + indice.intValue());
        usuario.setUsername("usuario" + indice + "@mail.com");
        usuario.setPassword("ContrasenaSegura" + indice + "XYZ");

        entityManager.persist(usuario);
        entityManager.flush();
        return usuario;
    }
}