package fred.frames;

import excel.ExcelWriter;
import fred.enumeration.SeriesEnum;
import fred.data.Series;
import fred.gbc.GBC;
import fred.network.FredConnection;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChoiceFrame extends JFrame {
    private Map<SeriesEnum, Series> seriesMap;
    private static final int MAX_MAP_SIZE = 4;

    private JComboBox<SeriesEnum> seriesCombo;
    private JButton seriesButton;

    private JComboBox<LocalDate> startingDateCombo;
    private JComboBox<LocalDate> endingDateCombo;

    private ChartFrame chartFrame;
    private JButton chartButton;

    private TableFrame tableFrame;
    private JButton tableButton;

    private JButton excelButton;
    private JFileChooser fileChooser;
    private ExcelWriter excelWriter;

    private FredWorker worker;
    private JPanel seriesPanel;
    private JProgressBar progressBar;

    public ChoiceFrame() {
        setMetalLookAndFeel();

        seriesMap = getLimitedSizeMap();
        fileChooser = getExcelFileChooser();

        setLayout(new GridBagLayout());
        add(getSeriesPanel(), new GBC(0, 0).setInsets(2).setFill(GBC.BOTH));
        add(getDatePanel(), new GBC(0, 1).setInsets(2).setFill(GBC.BOTH));
        add(createButtonPanel(), new GBC(0, 2).setInsets(2).setFill(GBC.BOTH));

        pack();
    }

    private void setMetalLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<SeriesEnum, Series> getLimitedSizeMap() {
        return new LinkedHashMap<SeriesEnum, Series>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_MAP_SIZE;
            }
        };
    }

    private JFileChooser getExcelFileChooser() {
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        JFileChooser fileChooser = new JFileChooser(desktopDir);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Excel files", ".xlsx"));

        return fileChooser;
    }

    private JPanel getSeriesPanel() {
        seriesCombo = getSeriesCombo();
        seriesButton = getSeriesButton();

        seriesPanel = new JPanel();
        seriesPanel.add(seriesCombo);
        seriesPanel.add(seriesButton);

        Border etched = BorderFactory.createEtchedBorder();
        Border seriesBorder = BorderFactory.createTitledBorder(etched, "Data series selection");
        seriesPanel.setBorder(seriesBorder);

        return seriesPanel;
    }

    private JComboBox<SeriesEnum> getSeriesCombo() {
        JComboBox<SeriesEnum> seriesCombo = new JComboBox<>(SeriesEnum.values());
        seriesCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SeriesEnum)
                    setText(((SeriesEnum) value).getLabel());

                return this;
            }
        });

        seriesCombo.addActionListener(event -> {
            if (chartFrame != null) {
                chartFrame.setVisible(false);
                chartFrame = null;
            }
            if (tableFrame != null) {
                tableFrame.setVisible(false);
                tableFrame = null;
            }
            excelWriter = null;

            startingDateCombo.removeAllItems();
            endingDateCombo.removeAllItems();

            SeriesEnum se = getSelectedItem(seriesCombo);

            if (seriesMap.containsKey(se)) {
                seriesButton.setEnabled(false);
                seriesButton.setText("Downloaded");

                excelButton.setEnabled(true);
                chartButton.setEnabled(true);
                tableButton.setEnabled(true);

                prepareDateCombo(startingDateCombo, false);
                prepareDateCombo(endingDateCombo, true);
            } else {
                seriesButton.setEnabled(
                        this.seriesCombo.getSelectedIndex() != 0);
                seriesButton.setText("Download series");
                startingDateCombo.setEnabled(false);
                endingDateCombo.setEnabled(false);

                excelButton.setEnabled(false);
                chartButton.setEnabled(false);
                tableButton.setEnabled(false);
            }
        });

        return seriesCombo;
    }

    private JButton getSeriesButton() {
        JButton seriesButton = new JButton("Download series");
        seriesButton.setEnabled(false);
        Dimension seriesButtonSize = setPreferredSize(seriesButton);

        seriesButton.addActionListener(event -> {
            seriesButton.setEnabled(false);
            seriesPanel.remove(seriesButton);

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setPreferredSize(seriesButtonSize);
            seriesPanel.add(progressBar);
            validate();

            seriesCombo.setEnabled(false);

            SeriesEnum se = getSelectedItem(seriesCombo);
            worker = new FredWorker(se);
            worker.execute();

        });

        return seriesButton;
    }

    private Dimension setPreferredSize(JButton button) {
        Dimension currentPreferredSize = button.getPreferredSize();
        button.setPreferredSize(currentPreferredSize);

        return currentPreferredSize;
    }

    private JPanel getDatePanel() {
        JPanel datePanel = new JPanel(new GridBagLayout());

        startingDateCombo = createDateCombo();
        addLabelAndComboToPanel(datePanel, "Start Date: ",
                startingDateCombo, 0);

        endingDateCombo = createDateCombo();
        addLabelAndComboToPanel(datePanel, "End Date: ",
                endingDateCombo, 1);

        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border dateBorder = BorderFactory.createTitledBorder(etchedBorder,
                "Period covered selection");
        datePanel.setBorder(dateBorder);

        return datePanel;
    }

    private void addLabelAndComboToPanel(JPanel datePanel, String labelText,
                                         JComboBox<LocalDate> dateCombo, int gridx) {
        JLabel dateLabel = new JLabel(labelText);
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);
        datePanel.add(dateLabel, getDateGBC(2 * gridx));

        datePanel.add(dateCombo, getDateGBC(2 * gridx + 1));
    }

    private JComboBox<LocalDate> createDateCombo() {
        JComboBox<LocalDate> dateCombo = new JComboBox<>();

        dateCombo.addItem(LocalDate.now());
        dateCombo.setPreferredSize(dateCombo.getPreferredSize());
        dateCombo.removeAllItems();
        dateCombo.setEnabled(false);

        dateCombo.addItemListener(new ItemListener() {
            private LocalDate deselected;

            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.DESELECTED) {
                    deselected = (LocalDate) event.getItem();
                    return;
                }

                if (!isDateChoiceValid(startingDateCombo, endingDateCombo)) {
                    dateCombo.removeItemListener(this);
                    dateCombo.setSelectedItem(deselected);
                    dateCombo.addItemListener(this);
                    return;
                }

                Series series = seriesMap.get(getSelectedItem(seriesCombo));
                LocalDate start = getSelectedItem(startingDateCombo);
                LocalDate end = getSelectedItem(endingDateCombo);

                if (chartFrame != null)
                    chartFrame.updateAxes(start, end);
                if (tableFrame != null)
                    tableFrame.updateObservationList(series.getObservationList(start, end));
            }
        });

        return dateCombo;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        excelButton = new JButton("Create report");
        excelButton.setEnabled(false);
        excelButton.addActionListener(event -> {
            int state = fileChooser.showSaveDialog(this);
            if (state == JFileChooser.APPROVE_OPTION)
                createExcelReport(fileChooser.getSelectedFile());
        });

        buttonPanel.add(excelButton, new GBC(0, 0).setInsets(2)
                .setWeight(100, 0).setFill(GBC.BOTH));

        tableButton = new JButton("Show table");
        tableButton.setEnabled(false);
        tableButton.addActionListener(event -> {
            if (tableFrame == null) tableFrame = createTableFrame();

            if (!tableFrame.isVisible()) tableFrame.setVisible(true);
            else tableFrame.setVisible(false);
        });

        buttonPanel.add(tableButton, new GBC(1, 0).setInsets(2)
                .setWeight(100, 0).setFill(GBC.BOTH));

        chartButton = new JButton("Show chart");
        chartButton.setEnabled(false);
        chartButton.addActionListener(event -> {
            if (chartFrame == null) chartFrame = createChartFrame();

            if (!chartFrame.isVisible()) chartFrame.setVisible(true);
            else chartFrame.setVisible(false);
        });

        buttonPanel.add(chartButton, new GBC(2, 0).setInsets(2)
                .setWeight(100, 0).setFill(GBC.BOTH));

        return buttonPanel;
    }

    private void createExcelReport(File file) {
        Series series = seriesMap.get(getSelectedItem(seriesCombo));
        LocalDate start = getSelectedItem(startingDateCombo);
        LocalDate end = getSelectedItem(endingDateCombo);

        if (excelWriter == null) {
            excelWriter = new ExcelWriter(series, start, end);
        } else {
            excelWriter.setStartDate(start);
            excelWriter.setEndDate(end);
        }

        excelButton.setEnabled(false);

        new Thread(() -> {
            try {
                excelWriter.writeToExcel(file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "IO error", JOptionPane.ERROR_MESSAGE);

                e.printStackTrace();
            } finally {
                EventQueue.invokeLater(() -> excelButton.setEnabled(true));
            }
        }).start();
    }

    private ChartFrame createChartFrame() {
        Series series = seriesMap.get(getSelectedItem(seriesCombo));
        LocalDate start = getSelectedItem(startingDateCombo);
        LocalDate end = getSelectedItem(endingDateCombo);

        ChartFrame chartFrame = new ChartFrame(series, start, end);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;
        int macBar = scnMax.top;

        chartFrame.setBounds(getWidth(), 0, screenSize.width - getWidth(),
                screenSize.height - taskBarSize - macBar);

        return chartFrame;
    }

    private TableFrame createTableFrame() {
        Series series = seriesMap.get(getSelectedItem(seriesCombo));
        LocalDate start = getSelectedItem(startingDateCombo);
        LocalDate end = getSelectedItem(endingDateCombo);

        TableFrame tableFrame = new TableFrame(series.getObservationList(start, end));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;
        int macBar = scnMax.top;

        tableFrame.setBounds(0, getHeight() + macBar, getWidth(),
                screenSize.height - getHeight() - taskBarSize - macBar);

        return tableFrame;
    }

    private void prepareDateCombo(JComboBox<LocalDate> dateCombo, boolean isEndCombo) {
        SeriesEnum se = getSelectedItem(seriesCombo);
        List<LocalDate> dateList = seriesMap.get(se).getDateList();

        for (LocalDate localDate : dateList)
            dateCombo.addItem(localDate);

        if (isEndCombo)
            dateCombo.setSelectedIndex(dateCombo.getModel().getSize() - 1);

        dateCombo.setEnabled(true);
    }

    private boolean isDateChoiceValid(JComboBox<LocalDate> beforeCombo,
                                      JComboBox<LocalDate> afterCombo) {
        LocalDate beforeDate = getSelectedItem(beforeCombo);
        LocalDate afterDate = getSelectedItem(afterCombo);

        if (beforeDate == null || afterDate == null) return true;

        if (beforeDate.compareTo(afterDate) > 0) {
            beforeCombo.hidePopup();
            afterCombo.hidePopup();

            JOptionPane.showMessageDialog(this,
                    "Starting date may not be after ending date", "",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private GridBagConstraints getDateGBC(int gridx) {
        return new GBC(gridx, 0).setFill(GBC.BOTH)
                .setWeight(100, 0).setInsets(5);
    }

    private <R> R getSelectedItem(JComboBox<? extends R> comboBox) {
        int selectedIndex = comboBox.getSelectedIndex();
        return comboBox.getItemAt(selectedIndex);
    }

    private class FredWorker extends SwingWorker<Series, Void> {
        private SeriesEnum se;

        FredWorker(SeriesEnum se) {
            this.se = se;
        }

        @Override
        protected Series doInBackground() throws Exception {
            return FredConnection.getSeries(se.getId());
        }

        @Override
        protected void done() {
            try {
                seriesMap.put(se, get());

                prepareDateCombo(startingDateCombo, false);
                prepareDateCombo(endingDateCombo, true);

                seriesButton.setText("Downloaded");

                excelButton.setEnabled(true);

                chartButton.setEnabled(true);
                tableButton.setEnabled(true);
            } catch (ExecutionException | InterruptedException e) {
                JOptionPane.showMessageDialog(ChoiceFrame.this,
                        "Internal error", "", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();

                seriesButton.setEnabled(true);
                seriesButton.setText("Data download");
            } finally {
                seriesCombo.setEnabled(true);
                seriesPanel.remove(progressBar);
                seriesPanel.add(seriesButton);
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new ChoiceFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
