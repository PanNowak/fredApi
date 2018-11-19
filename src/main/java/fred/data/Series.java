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
     * Returns list of observations dated from start date to end date (inclusive).
     * @param start starting date (inclusive)
     * @param end ending date (inclusive)
     * @return list of {@code Observation} objects
     */
    public List<Observation> getObservationList(LocalDate start, LocalDate end) {
        List<Observation> middleObservations = new ArrayList<>();
        for (Observation o : observationList) {
            if (o.getDate().compareTo(start) >= 0)
                middleObservations.add(o);

            if (o.getDate().compareTo(end) >= 0)
                break;
        }

        return middleObservations;
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
