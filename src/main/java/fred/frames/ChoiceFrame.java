package fred.frames;

import excel.ExcelWriter;
import fred.enumeration.SeriesEnum;
import fred.data.Series;
import fred.function.TriFunction;
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

    private JPanel seriesPanel;
    private JProgressBar progressBar;

    public ChoiceFrame() {
        setMetalLookAndFeel();

        seriesMap = getLimitedSizeMap();
        fileChooser = getExcelFileChooser();

        setLayout(new GridBagLayout());
        add(getSeriesPanel(), getFrameGBC(0));
        add(getDatePanel(), getFrameGBC(1));
        add(getButtonPanel(), getFrameGBC(2));

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

    private GridBagConstraints getFrameGBC(int gridy) {
        return new GBC(0, gridy).setInsets(2).setFill(GBC.BOTH);
    }

    private JPanel getSeriesPanel() {
        seriesCombo = getSeriesCombo();
        seriesButton = getSeriesButton();

        seriesPanel = new JPanel();
        seriesPanel.add(seriesCombo);
        seriesPanel.add(seriesButton);

        Border etched = BorderFactory.createEtchedBorder();
        Border seriesBorder = BorderFactory.createTitledBorder(
                etched, "Data series selection");
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
            clearAllComponents();
            if (isDataDownloaded()) {
                setDataControlButtonsEnabled(true, "Downloaded");

                prepareDateCombo(startingDateCombo, false);
                prepareDateCombo(endingDateCombo, true);
            } else {
                setDataControlButtonsEnabled(false, "Download");
            }
        });

        return seriesCombo;
    }

    private void clearAllComponents() {
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
    }

    private boolean isDataDownloaded() {
        SeriesEnum se = getSelectedItem(seriesCombo);
        return seriesMap.containsKey(se);
    }

    private void setDataControlButtonsEnabled(boolean isEnabled, String seriesButtonText) {
        seriesButton.setEnabled(!isEnabled &&
                seriesCombo.getSelectedIndex() != 0);
        seriesButton.setText(seriesButtonText);

        startingDateCombo.setEnabled(isEnabled);
        endingDateCombo.setEnabled(isEnabled);

        excelButton.setEnabled(isEnabled);
        chartButton.setEnabled(isEnabled);
        tableButton.setEnabled(isEnabled);
    }

    private JButton getSeriesButton() {
        JButton seriesButton = new JButton("Download series");
        seriesButton.setEnabled(false);
        Dimension seriesButtonSize = setPreferredSize(seriesButton);

        seriesButton.addActionListener(event -> {
            replaceSeriesButtonWithProgressBar(seriesButtonSize);
            seriesCombo.setEnabled(false);

            new FredWorker().execute();
        });

        return seriesButton;
    }

    private void replaceSeriesButtonWithProgressBar(Dimension seriesButtonSize) {
        seriesButton.setEnabled(false);
        seriesPanel.remove(seriesButton);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(seriesButtonSize);
        seriesPanel.add(progressBar);
        validate();
    }

    private Dimension setPreferredSize(JButton button) {
        Dimension currentPreferredSize = button.getPreferredSize();
        button.setPreferredSize(currentPreferredSize);

        return currentPreferredSize;
    }

    private JPanel getDatePanel() {
        JPanel datePanel = new JPanel(new GridBagLayout());

        addLabelAndComboToPanel(datePanel, "Start Date: ",
                startingDateCombo = getDateCombo(), 0);
        addLabelAndComboToPanel(datePanel, "End Date: ",
                endingDateCombo = getDateCombo(), 1);

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

    private JComboBox<LocalDate> getDateCombo() {
        JComboBox<LocalDate> dateCombo = new JComboBox<>();
        dateCombo.setEnabled(false);
        setPreferredSize(dateCombo);

        dateCombo.addItemListener(new ItemListener() {
            private LocalDate deselected;

            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.DESELECTED) {
                    deselected = (LocalDate) event.getItem();
                    return;
                }

                if (!isDateChoiceValid()) {
                    dateCombo.removeItemListener(this);
                    dateCombo.setSelectedItem(deselected);
                    dateCombo.addItemListener(this);
                    return;
                }

                updateDataFrames();
            }
        });

        return dateCombo;
    }

    private void setPreferredSize(JComboBox<? super LocalDate> comboBox) {
        comboBox.addItem(LocalDate.now());
        Dimension currentPreferredSize = comboBox.getPreferredSize();
        comboBox.setPreferredSize(currentPreferredSize);
        comboBox.removeAllItems();
    }

    private void updateDataFrames() {
        LocalDate start = getSelectedItem(startingDateCombo);
        LocalDate end = getSelectedItem(endingDateCombo);

        if (chartFrame != null)
            chartFrame.updateAxes(start, end);
        if (tableFrame != null)
            tableFrame.updateTable(start, end);
    }

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.add(excelButton = getExcelButton(), getButtonPanelGBC(0));
        buttonPanel.add(tableButton = getTableButton(), getButtonPanelGBC(1));
        buttonPanel.add(chartButton = getChartButton(), getButtonPanelGBC(2));

        return buttonPanel;
    }

    private JButton getExcelButton() {
        JButton excelButton = new JButton("Create report");
        excelButton.setEnabled(false);
        excelButton.addActionListener(event -> {
            int state = fileChooser.showSaveDialog(this);
            if (state == JFileChooser.APPROVE_OPTION) {
                excelButton.setEnabled(false);
                createExcelReport(fileChooser.getSelectedFile());
            }
        });

        return excelButton;
    }

    private JButton getTableButton() {
        JButton tableButton = new JButton("Show table");
        tableButton.setEnabled(false);
        tableButton.addActionListener(event -> {
            if (tableFrame == null) tableFrame = getTableFrame();

            if (!tableFrame.isVisible()) tableFrame.setVisible(true);
            else tableFrame.setVisible(false);
        });

        return tableButton;
    }

    private JButton getChartButton() {
        JButton chartButton = new JButton("Show chart");
        chartButton.setEnabled(false);
        chartButton.addActionListener(event -> {
            if (chartFrame == null) chartFrame = getChartFrame();

            if (!chartFrame.isVisible()) chartFrame.setVisible(true);
            else chartFrame.setVisible(false);
        });

        return chartButton;
    }

    private GridBagConstraints getButtonPanelGBC(int gridx) {
        return new GBC(gridx, 0).setInsets(2)
                .setWeight(100, 0).setFill(GBC.BOTH);
    }

    private void createExcelReport(File file) {
        prepareExcelWriter();
        getExcelWritingThread(file).start();
    }

    private void prepareExcelWriter() {
        LocalDate start = getSelectedItem(startingDateCombo);
        LocalDate end = getSelectedItem(endingDateCombo);

        if (excelWriter == null) {
            Series series = seriesMap.get(getSelectedItem(seriesCombo));
            excelWriter = new ExcelWriter(series, start, end);
        } else {
            excelWriter.setStartDate(start);
            excelWriter.setEndDate(end);
        }
    }

    private Thread getExcelWritingThread(File file) {
        return new Thread(() -> {
            try {
                excelWriter.writeToExcel(file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "IO error", JOptionPane.ERROR_MESSAGE);

                e.printStackTrace();
            } finally {
                EventQueue.invokeLater(() -> excelButton.setEnabled(true));
            }
        });
    }

    private ChartFrame getChartFrame() {
        ChartFrame chartFrame = getDataFrame(ChartFrame::new);
        setBounds(chartFrame, false);

        return chartFrame;
    }

    private TableFrame getTableFrame() {
        TableFrame tableFrame = getDataFrame(TableFrame::new);
        setBounds(tableFrame, true);

        return tableFrame;
    }

    private <R extends JFrame> R getDataFrame(
            TriFunction<Series, LocalDate, LocalDate, ? extends R> constructor) {
        Series series = seriesMap.get(getSelectedItem(seriesCombo));
        LocalDate start = getSelectedItem(startingDateCombo);
        LocalDate end = getSelectedItem(endingDateCombo);

        return constructor.apply(series, start, end);
    }

    private void setBounds(JFrame dataFrame, boolean isBelowMainFrame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;
        int macBar = scnMax.top;

        if (!isBelowMainFrame)
            dataFrame.setBounds(getWidth(), 0, screenSize.width - getWidth(),
                screenSize.height - taskBarSize - macBar);
        else
            dataFrame.setBounds(0, getHeight() + macBar, getWidth(),
                    screenSize.height - getHeight() - taskBarSize - macBar);
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

    private boolean isDateChoiceValid() {
        LocalDate beforeDate = getSelectedItem(startingDateCombo);
        LocalDate afterDate = getSelectedItem(endingDateCombo);

        if (beforeDate == null || afterDate == null) return true;

        if (beforeDate.compareTo(afterDate) > 0) {
            startingDateCombo.hidePopup();
            endingDateCombo.hidePopup();

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

        FredWorker() {
            se = getSelectedItem(seriesCombo);
        }

        @Override
        protected Series doInBackground() throws Exception {
            return FredConnection.getSeries(se.getId());
        }

        @Override
        protected void done() {
            try {
                seriesMap.put(se, get());
                setDataControlButtonsEnabled(true, "Downloaded");

                prepareDateCombo(startingDateCombo, false);
                prepareDateCombo(endingDateCombo, true);
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
}
