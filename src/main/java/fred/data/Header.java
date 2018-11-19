package fred.data;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Class representing header of the series.
 */
public class Header {
    private String id;
    private String title;
    private LocalDate observation_start;
    private LocalDate observation_end;
    private String frequency;
    private String units;

    /**
     * Creates new {@code Header} object.
     * @param id series identifier
     * @param title series title
     * @param observation_start date of the first observation
     * @param observation_end date of the last observation
     * @param frequency observation frequency
     * @param units observation's units
     */
    public Header(String id, String title, LocalDate observation_start,
                  LocalDate observation_end, String frequency, String units) {
        this.id = Objects.requireNonNull(id, "Identifier must not be null");
        this.title = Objects.requireNonNull(title, "Title must not be null");
        this.observation_start = Objects.requireNonNull(observation_start,
                "Observation start must not be null");
        this.observation_end = Objects.requireNonNull(observation_end,
                "Observation end must not be null");
        this.frequency = Objects.requireNonNull(frequency,
                "Frequency must not be null");
        this.units = Objects.requireNonNull(units, "Units must not be null");
    }

    /**
     * Returns id of this object.
     * @return series identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns title of this object.
     * @return series title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns date of the first observation.
     * @return date of the first observation
     */
    public LocalDate getObservation_start() {
        return observation_start;
    }

    /**
     * Returns date of the last observation.
     * @return date of the last observation
     */
    public LocalDate getObservation_end() {
        return observation_end;
    }

    /**
     * Returns observation frequency.
     * @return observation frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Returns observation's units.
     * @return observation's units
     */
    public String getUnits() {
        return units;
    }

    @Override
    public String toString() {
        return "Header{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", observation_start=" + observation_start +
                ", observation_end=" + observation_end +
                ", frequency='" + frequency + '\'' +
                ", units='" + units + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Header header = (Header) o;
        return Objects.equals(id, header.id) &&
                Objects.equals(title, header.title) &&
                Objects.equals(observation_start, header.observation_start) &&
                Objects.equals(observation_end, header.observation_end) &&
                Objects.equals(frequency, header.frequency) &&
                Objects.equals(units, header.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, observation_start,
                observation_end, frequency, units);
    }
}
