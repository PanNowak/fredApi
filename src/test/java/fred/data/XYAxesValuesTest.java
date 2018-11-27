package fred.data;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class XYAxesValuesTest {

    @Test
    void equalsContract() {
        EqualsVerifier.forClass(XYAxesValues.class).verify();
    }
}
