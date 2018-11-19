package excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;

public class ExcelReaderTest {
    public static final String SAMPLE_XLSX_FILE_PATH
            = "sample-xlsx-file.xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets: ");

        for (Sheet sheet : workbook)
            System.out.println("=> " + sheet.getSheetName());

        Sheet sheet = workbook.getSheetAt(0);

        DataFormatter dataFormatter = new DataFormatter();

        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = dataFormatter.formatCellValue(cell);
                System.out.println(cellValue + "\t");
            }
            System.out.println();
        }

        workbook.close();
    }

}
