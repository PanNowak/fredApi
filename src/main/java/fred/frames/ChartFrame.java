package fred.frames;

import fred.data.Header;
import fred.data.Observation;
import fred.data.Series;
import fred.data.XYListContainer;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static fred.network.FredConnection.RECESSION_DATA;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public class ChartFrame extends JFrame {
    private Series series;
    private LocalDate startDate;
    private LocalDate endDate;

    private XYChart chart;

    private List<Double> xAxisValues;
    private List<Double> yAxisValues;

    public ChartFrame(Series series, LocalDate startDate, LocalDate endDate) {
        this.series = series;
        this.startDate = startDate;
        this.endDate = endDate;

        prepareAxesLists();
        chart = createChart(series.getHeader());
        setAxesProperties();

        if (RECESSION_DATA != null) addRecessionBars();

        add(new XChartPanel<>(chart), BorderLayout.CENTER);
    }

    public void updateAxes(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        prepareAxesLists();
        chart.updateXYSeries(series.getHeader().getId(), xAxisValues,
                yAxisValues, null);
        setAxesProperties();

        repaint();
    }

    private XYChart createChart(Header header) {
        XYChart chart = new XYChartBuilder().title(header.getTitle())
                .xAxisTitle("Date")
                .yAxisTitle(header.getUnits()).build();

        chart.getStyler().setPlotMargin(10);
        chart.getStyler().setPlotContentSize(0.9);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setYAxisDecimalPattern("#0.00");

        Color fedColor = new Color(0xe1e9f0);
        chart.getStyler().setChartBackgroundColor(fedColor);
        chart.getStyler().setChartTitleBoxBackgroundColor(fedColor);
        chart.getStyler().setChartTitleBoxBorderColor(fedColor);

        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBorderColor(Color.BLACK);

        chart.addSeries(header.getId(), xAxisValues, yAxisValues)
                .setMarker(SeriesMarkers.NONE).setLineColor(Color.BLUE)
                .setLineStyle(SeriesLines.SOLID);

        return chart;
    }

    private void prepareAxesLists() {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        List<Observation> observationList = series.getObservationList(startDate, endDate);
        for (Observation observation : observationList) {
            xAxisValues.add((double) observation.getxMarker());
            yAxisValues.add(observation.getValue().doubleValue());
        }
    }

    private void addRecessionBars() {
        Color g = Color.LIGHT_GRAY;
        Color gray = new Color(g.getRed(), g.getGreen(), g.getBlue(), 100);

        List<XYListContainer> containerList = RECESSION_DATA.getContainerList();
        for (XYListContainer container : containerList) {
            chart.addSeries(container.toString(),
                    container.getxValues(), container.getyValues())
                    .setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area)
                    .setMarker(SeriesMarkers.NONE).setFillColor(gray);
        }
    }

    private void setAxesProperties() {
        List<Observation> observationList =
                series.getObservationList(startDate, endDate);
        long xAxisStart = observationList.get(0).getxMarker();
        long xAxisEnd = observationList.get(observationList.size() - 1).getxMarker();

        setAxesMinMax(xAxisStart, xAxisEnd);
        setXAxisLabelOverrideMap(xAxisStart, xAxisEnd);
    }

    private void setAxesMinMax(long xAxisStart, long xAxisEnd) {
        double min = yAxisValues.get(0);
        double max = yAxisValues.get(0);

        for (Double yAxisValue : yAxisValues) {
            if (min > yAxisValue) min = yAxisValue;
            if (max < yAxisValue) max = yAxisValue;
        }

        chart.getStyler().setYAxisMin(min);
        chart.getStyler().setYAxisMax(max);

        chart.getStyler().setXAxisMin((double) xAxisStart);
        chart.getStyler().setXAxisMax((double) xAxisEnd);
    }

    private void setXAxisLabelOverrideMap(long xAxisStart, long xAxisEnd) {
        Map<Double, Object> xMarkMap = new TreeMap<>();
        DateTimeFormatter formatter = createDateTimeFormatter();

        int parts = 6;
        long days = DAYS.between(startDate, endDate);
        double dayStep = days / (double) parts;

        for (int i = 0; i < parts; i++) {
            LocalDate currentTickDate = startDate.plusDays(Math.round(i * dayStep));
            xMarkMap.put(i * dayStep + xAxisStart, currentTickDate.format(formatter));
        }
        xMarkMap.put((double) xAxisEnd, endDate.format(formatter));

        chart.setXAxisLabelOverrideMap(xMarkMap);
    }

    private DateTimeFormatter createDateTimeFormatter() {
        long years = YEARS.between(startDate, endDate);

        return years > 0 ? DateTimeFormatter.ofPattern("uuuu-MM")
                : DateTimeFormatter.ISO_LOCAL_DATE;
    }
}
