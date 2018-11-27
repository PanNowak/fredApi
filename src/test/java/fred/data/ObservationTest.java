package fred.data;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObservationTest {

    @Test
    void shouldntAcceptNullValues() {
        LocalDate date = LocalDate.now();
        BigDecimal value = BigDecimal.ZERO;

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new Observation(null, value)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Observation(date, null))
        );
    }

    @Test
    void equalsContract() {
        EqualsVerifier.forClass(Observation.class).verify();
    }
}
