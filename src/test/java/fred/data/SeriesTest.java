package fred.data;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeriesTest {

    private Series getMockedSeries() {
        List<Observation> observationList = new ArrayList<>();
        BigDecimal value = BigDecimal.ZERO;
        for (int i = 1900; i <= 2018; i++) {
            LocalDate date = LocalDate.of(i, 1, 1);
            value = value.add(BigDecimal.ONE);
            observationList.add(new Observation(date, value));
        }

        return new Series(null, observationList);
    }

    @Test
    void givenFirstDateAfterLastDateShouldThrowAnException() {
        LocalDate firstDate = LocalDate.of(1950, 1, 1);
        LocalDate wrongLastDate = LocalDate.of(1949, 12, 31);
        LocalDate correctLastDate = LocalDate.of(1950, 1, 2);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> getMockedSeries().getObservationList(firstDate, wrongLastDate)),
                () -> assertDoesNotThrow(
                        () -> getMockedSeries().getObservationList(firstDate, firstDate)),
                () -> assertDoesNotThrow(
                        () -> getMockedSeries().getObservationList(firstDate, correctLastDate))
        );
    }

    @Test
    void dateAndValueListsShouldBeSizeOfObservationList() {
        int observationListSize = getMockedSeries().getObservationList().size();

        assertAll(
                () -> assertEquals(observationListSize,
                        getMockedSeries().getDateList().size()),
                () -> assertEquals(observationListSize,
                        getMockedSeries().getValueList().size())
        );
    }

    @Test
    void observationListBetweenDatesShouldBeEqualToExpected() {
        List<Observation> expectedObservationList = new ArrayList<>();
        List<Observation> mockedObservationList =
                getMockedSeries().getObservationList();

        for (int i = 50; i <= 60; i++)
            expectedObservationList.add(mockedObservationList.get(i));

        LocalDate correctFirstDate = LocalDate.of(1950, 1, 1);
        LocalDate correctBeforeFirstDate = LocalDate.of(1949, 1, 2);
        LocalDate wrongBeforeFirstDate = LocalDate.of(1949, 1, 1);

        LocalDate correctLastDate = LocalDate.of(1960, 1, 1);
        LocalDate correctAfterLastDate = LocalDate.of(1960, 12, 31);
        LocalDate wrongAfterLastDate = LocalDate.of(1961, 1, 1);

        assertAll(
                () -> assertEquals(expectedObservationList, getMockedSeries()
                        .getObservationList(correctFirstDate, correctLastDate)),
                () -> assertEquals(expectedObservationList, getMockedSeries()
                        .getObservationList(correctBeforeFirstDate, correctAfterLastDate)),
                () -> assertNotEquals(expectedObservationList, getMockedSeries()
                        .getObservationList(wrongBeforeFirstDate, wrongAfterLastDate)),
                () -> assertNotEquals(expectedObservationList, getMockedSeries()
                        .getObservationList(wrongBeforeFirstDate, correctLastDate)),
                () -> assertNotEquals(expectedObservationList, getMockedSeries()
                        .getObservationList(correctFirstDate, wrongAfterLastDate))
        );
    }

    @Test
    void equalsContract() {
        EqualsVerifier.forClass(Series.class).verify();
    }
}
