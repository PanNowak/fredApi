package fred.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Class representing one observation.
 */
public class Observation {
    /**
     * Static starting point used to count number of days between
     * it and date of this observation.
     */
    private static final LocalDate FIRST_OBSERVATION_DATE =
            LocalDate.of(1500, 1, 1);

    private LocalDate date;
    private BigDecimal value;
    private long xMarker;

    /**
     * Creates new {@code Observation} object.
     * @param date date of this observation
     * @param value value of this observation
     */
    public Observation(LocalDate date, BigDecimal value) {
        this.date = Objects.requireNonNull(date, "Date must not be null");
        this.value = Objects.requireNonNull(value, "Value must not be null");
        this.xMarker = DAYS.between(FIRST_OBSERVATION_DATE, date);
    }

    /**
     * Returns date of this observation.
     * @return date of the observation
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns value of this observation.
     * @return value of this observation
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Returns number of days that passed from 1500-01-01
     * until the day of this observation. Using it simplifies
     * mapping between dates and their position on x-axis of the chart.
     * @return x-axis marker associated with this observation
     */
    public long getxMarker() {
        return xMarker;
    }

    @Override
    public String toString() {
        return "Observation{" +
                "date=" + date +
                ", value=" + value +
                ", xMarker=" + xMarker +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observation that = (Observation) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, value);
    }
}
