package fred.data;

import java.util.List;
import java.util.Objects;

/**
 * Class containing data necessary to create single
 * <i>gray strip</i> that indicates recession on an area chart.
 */
public final class XYAxesValues {
    private final List<Double> xValues;
    private final List<Double> yValues;

    /**
     * Creates new {@code XYAxesValues} object.
     * @param xValues list of values from x-axis
     * @param yValues list of values fram y-axis
     */
    public XYAxesValues(List<Double> xValues, List<Double> yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }

    /**
     * Returns list of values from x-axis.
     * @return x-axis values
     */
    public List<Double> getxValues() {
        return xValues;
    }

    /**
     * Returns list of values from y-axis.
     * @return y-axis values
     */
    public List<Double> getyValues() {
        return yValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XYAxesValues that = (XYAxesValues) o;
        return Objects.equals(xValues, that.xValues) &&
                Objects.equals(yValues, that.yValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xValues, yValues);
    }

    @Override
    public String toString() {
        return "XYAxesValues{" +
                "xValues=" + xValues +
                ", yValues=" + yValues +
                '}';
    }
}
