package ar.edu.huergo.clickservice.buscadorservicios.entity.profesional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AgendaSlotValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    private AgendaSlot crearSlot(LocalDateTime inicio, LocalDateTime fin) {
        AgendaSlot slot = new AgendaSlot();
        slot.setProfesional(new Profesional());
        slot.setFechaInicio(inicio);
        slot.setFechaFin(fin);
        slot.setDisponible(Boolean.TRUE);
        return slot;
    }

    @Nested
    @DisplayName("Validación del rango horario")
    class RangoHorario {

        @Test
        @DisplayName("debería aceptar un rango donde la fecha de fin es posterior")
        void deberiaAceptarRangoHorarioValido() {
            AgendaSlot slot = crearSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(1));

            Set<ConstraintViolation<AgendaSlot>> violaciones = validator.validate(slot);

            assertThat(violaciones)
                .as("No debería haber violaciones cuando el rango horario es válido")
                .isEmpty();
        }

        @Test
        @DisplayName("debería rechazar un rango donde la fecha de fin es anterior o igual a la de inicio")
        void deberiaRechazarRangosHorariosInvalidos() {
            LocalDateTime inicio = LocalDateTime.of(2025, 1, 10, 10, 0);
            LocalDateTime fin = inicio.minusMinutes(30);
            AgendaSlot slot = crearSlot(inicio, fin);

            Set<ConstraintViolation<AgendaSlot>> violaciones = validator.validate(slot);

            assertThat(violaciones)
                .as("Debe existir al menos una violación por rango horario inválido")
                .isNotEmpty();

            assertThat(violaciones)
                .anySatisfy(violacion -> assertThat(violacion.getMessage())
                    .isEqualTo("La fecha de fin debe ser posterior a la fecha de inicio"));
        }
    }

    @Test
    @DisplayName("debería mantener disponible en true cuando no se establece explícitamente")
    void deberiaMantenerDisponibleTruePorDefecto() throws Exception {
        AgendaSlot slot = crearSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        slot.setDisponible(null);

        var metodo = AgendaSlot.class.getDeclaredMethod("ensureDefaults");
        metodo.setAccessible(true);
        metodo.invoke(slot);

        assertThat(slot.getDisponible()).isTrue();
    }
}