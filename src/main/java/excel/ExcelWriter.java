package excel;

import fred.data.Observation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.awt.Font.*;

public class ExcelWriter {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Observation> observationList;

    private Workbook workbook;
    private Sheet sheet;

    public ExcelWriter(String title, LocalDate startDate, LocalDate endDate,
                       List<Observation> observationList) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.observationList = observationList;

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Analysis");
    }

    public void writeToExcel(String path) throws IOException {
        setTitleCell(title, BOLD, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, uuuu");
        String start = formatter.format(startDate);
        String end = formatter.format(endDate);

        setTitleCell( start + " - " + end, ITALIC, 1);

        setTableCells();

        FileOutputStream out = new FileOutputStream(path);
        workbook.write(out);
        out.close();
        workbook.close();
    }

    private void setTitleCell(String name, int fontStyle, int rowNumber) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(createFont(11, fontStyle));

        Row firstRow = sheet.createRow(rowNumber);
        Cell cell = firstRow.createCell(0);
        cell.setCellValue(name);
        cell.setCellStyle(cellStyle);
    }

    private void setTableCells() {
        int rowNum = 3;

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(createFont(10, PLAIN));
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        Row headerRow = sheet.createRow(rowNum++);
        Cell dateHeaderCell = headerRow.createCell(0);
        dateHeaderCell.setCellValue("Date");
        dateHeaderCell.setCellStyle(headerCellStyle);
        sheet.setColumnWidth(0, 12 * 256);

        Cell valueHeaderCell = headerRow.createCell(1);
        valueHeaderCell.setCellValue("Value");
        valueHeaderCell.setCellStyle(headerCellStyle);
        sheet.setColumnWidth(1, 12 * 256);

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setFont(createFont(10, PLAIN));
        dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("M/d/yyyy"));

        CellStyle valueCellStyle = workbook.createCellStyle();
        valueCellStyle.setFont(createFont(10, PLAIN));
        valueCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        for (Observation observation : observationList) {
            Row row = sheet.createRow(rowNum++);

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(java.sql.Date.valueOf(observation.getDate()));
            dateCell.setCellStyle(dateCellStyle);

            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(observation.getValue().doubleValue());
            valueCell.setCellStyle(valueCellStyle);
        }
    }

    private Font createFont(int size, int style) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) size);

        if ((style & BOLD) == BOLD) font.setBold(true);
        if ((style & ITALIC) == ITALIC) font.setItalic(true);

        return font;
    }
}
