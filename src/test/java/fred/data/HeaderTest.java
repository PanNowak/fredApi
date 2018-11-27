package fred.data;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeaderTest {

    @Test
    void shouldntAcceptNullValues() {
        String id = "id";
        String title = "title";
        LocalDate observationStart = LocalDate.now();
        LocalDate observationEnd = LocalDate.now();
        String frequency = "frequency";
        String units = "units";

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new Header(null, title, observationStart,
                                observationEnd, frequency, units)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Header(id, null, observationStart,
                                observationEnd, frequency, units)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Header(id, title, null,
                                observationEnd, frequency, units)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Header(id, title, observationStart,
                                null, frequency, units)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Header(id, title, observationStart,
                                observationEnd, null, units)),
                () -> assertThrows(NullPointerException.class,
                        () -> new Header(id, title, observationStart,
                                observationEnd, frequency, null))
        );
    }

    @Test
    void equalsContract() {
        EqualsVerifier.forClass(Header.class).verify();
    }
}
