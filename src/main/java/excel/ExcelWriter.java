package excel;

import fred.data.Observation;
import fred.data.Series;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static java.awt.Font.*;

public class ExcelWriter {
    private Series series;
    private LocalDate startDate;
    private LocalDate endDate;

    private int rowNum = 3;

    public ExcelWriter(Series series, LocalDate startDate, LocalDate endDate) {
        this.series = series;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void writeToExcel(File file) throws IOException {
        String path = file.getAbsolutePath();
        if (!path.endsWith(".xlsx")) path += ".xlsx";

        try (FileOutputStream out = new FileOutputStream(path);
             Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Analysis");

            String title = series.getHeader().getTitle();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.US);
            String start = formatter.format(startDate);
            String end = formatter.format(endDate);

            setTitleCell(workbook, sheet, title, BOLD, 0);
            setTitleCell(workbook, sheet, start + " - " + end, ITALIC, 1);

            setTableCells(workbook, sheet);
            createChart(sheet);

            workbook.write(out);
        }
    }

    private void setTitleCell(Workbook workbook, Sheet sheet,
                              String text, int fontStyle, int rowNumber) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(createFont(workbook, 11, fontStyle));

        Row firstRow = sheet.createRow(rowNumber);
        Cell cell = firstRow.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(cellStyle);
    }

    private void setTableCells(Workbook workbook, Sheet sheet) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(createFont(workbook, 10, PLAIN));
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        Row headerRow = sheet.createRow(rowNum++);
        setHeaderCell(sheet, headerRow, 0, "Date", headerCellStyle);
        setHeaderCell(sheet, headerRow, 1, "Value", headerCellStyle);

        setDataCells(workbook, sheet);
    }

    private void setHeaderCell(Sheet sheet, Row row, int colNum,
                               String value, CellStyle style) {
        Cell headerCell = row.createCell(colNum);
        headerCell.setCellValue(value);
        headerCell.setCellStyle(style);
        sheet.setColumnWidth(colNum, 12 * 256);
    }

    private CellStyle createDataCellStyle(Workbook workbook, String format) {
        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setFont(createFont(workbook, 10, PLAIN));
        dataCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        dataCellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));

        return dataCellStyle;
    }

    private void setDataCells(Workbook workbook, Sheet sheet) {
        CellStyle dateCellStyle = createDataCellStyle(workbook, "");
        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("M/d/uuuu");

        CellStyle valueCellStyle = createDataCellStyle(workbook, "0.00");

        List<Observation> observationList = series.getObservationList(startDate, endDate);
        for (Observation observation : observationList) {
            Row row = sheet.createRow(rowNum++);

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(dateFormatter.format(observation.getDate()));
            dateCell.setCellStyle(dateCellStyle);

            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(observation.getValue().doubleValue());
            valueCell.setCellStyle(valueCellStyle);
        }
    }

    private void createChart(Sheet sheet) {
        XSSFSheet chartSheet = (XSSFSheet) sheet;
        XSSFDrawing drawing = chartSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0,
                3, 3, 17, 32);

        XSSFChart lineChart = drawing.createChart(anchor);
        LineChartData data = lineChart.getChartDataFactory().createLineChartData();

        ChartAxis bottomAxis = lineChart.getChartAxisFactory().createDateAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = lineChart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        bottomAxis.setMajorTickMark(AxisTickMark.NONE);
        leftAxis.setMajorTickMark(AxisTickMark.NONE);

        ChartDataSource<Number> xAxisData = DataSources.fromNumericCellRange(
                chartSheet, new CellRangeAddress(4, rowNum - 1, 0, 0));
        ChartDataSource<Number> yAxisData = DataSources.fromNumericCellRange(
                chartSheet, new CellRangeAddress(4, rowNum - 1, 1, 1));
        data.addSeries(xAxisData, yAxisData);
        lineChart.plot(data, bottomAxis, leftAxis);
    }

    private Font createFont(Workbook workbook, int size, int style) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) size);

        if ((style & BOLD) == BOLD) font.setBold(true);
        if ((style & ITALIC) == ITALIC) font.setItalic(true);

        return font;
    }
}
