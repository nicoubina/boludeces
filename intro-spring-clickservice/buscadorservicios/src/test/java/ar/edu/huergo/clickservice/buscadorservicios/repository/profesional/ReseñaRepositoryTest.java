package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Reseña;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;

@DataJpaTest
class ReseñaRepositoryTest {

    @Autowired
    private ReseñaRepository reseñaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deberiaObtenerReseñasPorProfesional() {
        Profesional profesional = persistirProfesional(1L);
        Usuario clienteUno = persistirUsuario(100L);
        Usuario clienteDos = persistirUsuario(101L);

        persistirReseña(10L, clienteUno, profesional, 5, "Servicio excelente 123");
        persistirReseña(11L, clienteDos, profesional, 4, "Muy buen trabajo 4567");

        Profesional otroProfesional = persistirProfesional(2L);
        persistirReseña(12L, persistirUsuario(102L), otroProfesional, 3, "Correcto pero mejorable");

        entityManager.clear();

        List<Reseña> reseñas = reseñaRepository.findByProfesionalId(profesional.getId());

        assertThat(reseñas)
            .hasSize(2)
            .allMatch(r -> r.getProfesional().getId().equals(profesional.getId()));
    }

    @Test
    void deberiaObtenerReseñasPorUsuario() {
        Usuario usuario = persistirUsuario(200L);
        Profesional profesional = persistirProfesional(3L);
        persistirReseña(20L, usuario, profesional, 5, "Gran servicio brindado");
        persistirReseña(21L, usuario, profesional, 4, "Trabajo muy prolijo 1");
        persistirReseña(22L, persistirUsuario(201L), profesional, 2, "No cumplio lo esperado");

        entityManager.clear();

        List<Reseña> reseñas = reseñaRepository.findByUsuarioId(usuario.getId());

        assertThat(reseñas)
            .hasSize(2)
            .allMatch(r -> r.getUsuario().getId().equals(usuario.getId()));
    }

    @Test
    void deberiaEncontrarPorOrdenId() {
        Usuario usuario = persistirUsuario(300L);
        Profesional profesional = persistirProfesional(4L);
        Reseña reseña = persistirReseña(30L, usuario, profesional, 5, "Experiencia sobresaliente");

        entityManager.clear();

        assertThat(reseñaRepository.findByOrdenId(30L))
            .isPresent()
            .get()
            .extracting(Reseña::getId)
            .isEqualTo(reseña.getId());

        assertThat(reseñaRepository.existsByOrdenId(30L)).isTrue();
        assertThat(reseñaRepository.existsByOrdenId(31L)).isFalse();
    }

    private Reseña persistirReseña(Long ordenId, Usuario usuario, Profesional profesional, int rating, String comentario) {
        Reseña reseña = new Reseña();
        reseña.setOrdenId(ordenId);
        reseña.setUsuario(usuario);
        reseña.setProfesional(profesional);
        reseña.setRating(rating);
        reseña.setComentario(comentario);
        reseña.setFecha(LocalDateTime.now());

        entityManager.persist(reseña);
        entityManager.flush();
        return reseña;
    }

    private Profesional persistirProfesional(Long indice) {
        Usuario usuario = persistirUsuario(indice);
        Servicio servicio = persistirServicio(indice);

        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto("Profesional " + indice);
        profesional.setTelefono("+541100000" + indice);
        profesional.setDescripcion("Descripcion profesional " + indice);
        profesional.setDisponible(true);
        profesional.setZonaTrabajo("Zona " + indice);
        profesional.setServicios(new HashSet<>(Collections.singleton(servicio)));

        entityManager.persist(profesional);
        entityManager.flush();
        return profesional;
    }

    private Usuario persistirUsuario(Long indice) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre" + indice);
        usuario.setApellido("Apellido" + indice);
        usuario.setDni(String.format("%08d", 20000000 + indice));
        usuario.setTelefono("+54911123" + String.format("%04d", indice));
        usuario.setCalle("Calle " + indice);
        usuario.setAltura(100 + indice.intValue());
        usuario.setUsername("usuario" + indice + "@mail.com");
        usuario.setPassword("ContrasenaSegura" + indice + "XYZ");

        entityManager.persist(usuario);
        entityManager.flush();
        return usuario;
    }

    private Servicio persistirServicio(Long indice) {
        Servicio servicio = new Servicio();
        servicio.setNombre("Servicio " + indice);
        servicio.setPrecioHora(1500.0 + indice);

        entityManager.persist(servicio);
        entityManager.flush();
        return servicio;
    }
}