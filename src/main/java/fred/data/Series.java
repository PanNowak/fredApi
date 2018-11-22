package fred.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing data series.
 */
public class Series {
    private Header header;
    private List<Observation> observationList;

    /**
     * Creates new {@code Series} object.
     * @param header {@code Header} object
     * @param observationList list of {@code Observation} objects
     */
    public Series(Header header, List<Observation> observationList) {
        this.header = header;
        this.observationList = observationList;
    }

    /**
     * Returns header of this object
     * @return {@code Header} object
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Returns list of observations of this object.
     * @return list of {@code Observation} objects
     */
    public List<Observation> getObservationList() {
        return observationList;
    }

    /**
     * Returns list of observations dated from first date to last date (inclusive).
     * Observations must be sorted in ascending order, i.e., from earliest to most recent.
     * @param firstDate starting date (inclusive)
     * @param lastDate ending date (inclusive)
     * @return list of {@code Observation} objects
     */
    public List<Observation> getObservationList(LocalDate firstDate, LocalDate lastDate) {
        List<Observation> observationsBetween = new ArrayList<>();
        for (Observation observation : observationList) {
            LocalDate dateOfObservation = observation.getDate();

            if (dateOfObservation.compareTo(firstDate) >= 0)
                observationsBetween.add(observation);

            if (dateOfObservation.compareTo(lastDate) >= 0)
                break;
        }

        return observationsBetween;
    }


    /**
     * Returns list of dates of all observations.
     * @return list of {@code LocalDate} objects
     */
    public List<LocalDate> getDateList() {
        List<LocalDate> dateList = new ArrayList<>();
        for (Observation obs : observationList)
            dateList.add(obs.getDate());

        return dateList;
    }

    /**
     * Returns list of values of all observations.
     * @return list of {@code BigDecimal} values
     */
    public List<BigDecimal> getValueList() {
        List<BigDecimal> valueList = new ArrayList<>();
        for (Observation obs : observationList)
            valueList.add(obs.getValue());

        return valueList;
    }

    @Override
    public String toString() {
        return "Series{" +
                "header=" + header +
                ",\nobservationList=" + observationList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Series series = (Series) o;
        return Objects.equals(header, series.header) &&
                Objects.equals(observationList, series.observationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, observationList);
    }
}
