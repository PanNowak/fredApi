package fred.frames;

import fred.data.Observation;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class TableFrame extends JFrame {
    private List<Observation> observationList;
    private JTable table;

    public TableFrame(List<Observation> observationList) {
        this.observationList = observationList;

        table = new JTable(new FedTableModel());
        table.setCellSelectionEnabled(true);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateObservationList(List<Observation> observationList) {
        this.observationList = observationList;
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
