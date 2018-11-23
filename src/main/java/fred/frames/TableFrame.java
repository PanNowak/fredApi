package fred.frames;

import fred.data.Observation;
import fred.data.Series;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TableFrame extends JFrame {
    private Series series;
    private List<Observation> observationList;
    private JTable table;

    public TableFrame(Series series, LocalDate startDate, LocalDate endDate) {
        this.series = series;
        observationList = getUpdatedObservationList(startDate, endDate);

        table = new JTable(new FedTableModel());
        table.setCellSelectionEnabled(true);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private List<Observation> getUpdatedObservationList(LocalDate startDate, LocalDate endDate) {
        return series.getObservationList(startDate, endDate);
    }

    public void updateTable(LocalDate startDate, LocalDate endDate) {
        observationList = getUpdatedObservationList(startDate, endDate);
        table.updateUI();
    }

    private class FedTableModel extends AbstractTableModel {
        @Override
        public String getColumnName(int column) {
            if (column == 0) return "Date";
            else return "Value";
        }

        @Override
        public int getRowCount() {
            return observationList.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Observation current = observationList.get(rowIndex);

            if (columnIndex == 0) return current.getDate();
            else return current.getValue();
        }
    }
}
