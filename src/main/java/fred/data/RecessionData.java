package fred.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that contains data about all recessions
 * in the United States.
 */
public class RecessionData {
    private List<Series> recessionList;
    private List<XYListContainer> containerList;

    /**
     * Creates new {@code RecessionData} object.
     * @param recessionList list of {@code Series}
     *        objects in which one series contains
     *        data about one U.S recession
     */
    public RecessionData(List<Series> recessionList) {
        this.recessionList = recessionList;
        containerList = createRecessionContainerList();
    }

    /**
     * Returns list of {@code Series} objects in which
     * one series contains data about one U.S recession.
     * @return list of recessions
     */
    public List<Series> getRecessionList() {
        return recessionList;
    }

    /**
     * Returns list of {@code XYContainer} objects.
     * @return list of XYContainers
     */
    public List<XYListContainer> getContainerList() {
        return containerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecessionData that = (RecessionData) o;
        return Objects.equals(recessionList, that.recessionList) &&
                Objects.equals(containerList, that.containerList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recessionList, containerList);
    }

    @Override
    public String toString() {
        return "RecessionData{" +
                "recessionList=" + recessionList +
                ", containerList=" + containerList +
                '}';
    }

    private List<XYListContainer> createRecessionContainerList() {
        List<XYListContainer> containerList = new ArrayList<>();

        for (Series r : recessionList) {
            List<Observation> rObservationList = r.getObservationList();
            List<Double> xValues = new ArrayList<>();
            List<Double> y1Values = new ArrayList<>();
            List<Double> y2Values = new ArrayList<>();

            for (Observation obs : rObservationList) {
                xValues.add((double) obs.getxMarker());
                y1Values.add(1_000_000_000_000d);
                y2Values.add(-1_000_000_000_000d);
            }

            containerList.add(new XYListContainer(xValues, y1Values));
            containerList.add(new XYListContainer(xValues, y2Values));
        }

        return containerList;
    }
}
