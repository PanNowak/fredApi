package fred.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that contains data about all recessions
 * in the United States.
 */
public class RecessionData {
    private List<XYAxesValues> axesValuesList;

    /**
     * Creates new {@code RecessionData} object.
     * @param listOfRecessionSeries list of {@code Series}
     *        objects in which one series contains
     *        data about one U.S recession
     */
    public RecessionData(List<Series> listOfRecessionSeries) {
        axesValuesList = createAxesValuesList(listOfRecessionSeries);
    }

    /**
     * Returns list of {@code XYContainer} objects.
     * @return list of XYContainers
     */
    public List<XYAxesValues> getAxesValuesList() {
        return axesValuesList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecessionData that = (RecessionData) o;
        return Objects.equals(axesValuesList, that.axesValuesList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(axesValuesList);
    }

    @Override
    public String toString() {
        return "RecessionData{" +
                "axesValuesList=" + axesValuesList +
                '}';
    }

    private List<XYAxesValues> createAxesValuesList(List<Series> listOfRecessionSeries) {
        List<XYAxesValues> axesValuesList = new ArrayList<>();

        for (Series recession : listOfRecessionSeries) {
            List<Observation> listOfRecessionObservations = recession.getObservationList();
            List<Double> xValues = new ArrayList<>();
            List<Double> topYValues = new ArrayList<>();
            List<Double> bottomYValues = new ArrayList<>();

            for (Observation obs : listOfRecessionObservations) {
                xValues.add((double) obs.getxMarker());
                //adding very large and very small values
                //so that gray strip covers all visible vertical area
                topYValues.add(1_000_000_000_000d);
                bottomYValues.add(-1_000_000_000_000d);
            }

            axesValuesList.add(new XYAxesValues(xValues, topYValues));
            axesValuesList.add(new XYAxesValues(xValues, bottomYValues));
        }

        return axesValuesList;
    }
}
