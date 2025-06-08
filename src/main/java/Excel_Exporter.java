import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JTable;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
// Utility class to export JTable data into an Excel (.xlsx) file using Apache POI
public class Excel_Exporter {

    // Common date formats for parsing string values as dates
    private static final String[] date_formats = {
            "yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy", "dd/MM/yyyy"
    };

    // Exports the given JTable data to an Excel file
    public static void export_to_excel(JTable table, File file) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        CreationHelper create_helper = workbook.getCreationHelper();

        // Cell style for date formatting
        CellStyle date_cell_style = workbook.createCellStyle();
        date_cell_style.setDataFormat(create_helper.createDataFormat().getFormat("yyyy-mm-dd"));

        // Bold font style for header
        Font bold_font = workbook.createFont();
        bold_font.setBold(true);
        CellStyle bold_style = workbook.createCellStyle();
        bold_style.setFont(bold_font);

        int row_index = 0;
        int column_count = table.getColumnCount();
        int[] max_column_widths = new int[column_count]; // Track max width per column

        // Create and style header row
        Row header_row = sheet.createRow(row_index++);
        for (int col = 0; col < column_count; col++) {
            String header = table.getColumnName(col);
            max_column_widths[col] = header.length(); // track width

            Cell cell = header_row.createCell(col);
            cell.setCellValue(header);
            cell.setCellStyle(bold_style);
        }

        // Write data rows to Excel sheet
        for (int row = 0; row < table.getRowCount(); row++) {
            Row excel_row = sheet.createRow(row_index++);
            for (int col = 0; col < column_count; col++) {
                Cell cell = excel_row.createCell(col);
                Object value = table.getValueAt(row, col);

                String string_value = "";

                if (value == null) {
                    cell.setCellValue("");
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                    string_value = value.toString();
                } else if (value instanceof String && is_likely_date((String) value)) {
                    Date parsed_date = parse_date((String) value);
                    if (parsed_date != null) {
                        cell.setCellValue(parsed_date);
                        cell.setCellStyle(date_cell_style);
                        string_value = value.toString();
                    } else {
                        cell.setCellValue(value.toString());
                        string_value = value.toString();
                    }
                } else {
                    try {
                        double num = Double.parseDouble(value.toString());
                        cell.setCellValue(num);
                        string_value = value.toString();
                    } catch (Exception e) {
                        cell.setCellValue(value.toString());
                        string_value = value.toString();
                    }
                }

                // Track max column width
                if (string_value != null) {
                    max_column_widths[col] = Math.max(max_column_widths[col], string_value.length());
                }
            }
        }

        // Resize columns based on maximum content width
        for (int col = 0; col < column_count; col++) {
            int width = (max_column_widths[col] + 2) * 256; // add padding
            sheet.setColumnWidth(col, Math.min(width, 10000)); // cap width to avoid extreme stretch
        }

        // Write workbook to file
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }

        workbook.close(); // Clean up
        workbook.close(); // Clean up

        // Ask user if they want to open the file now
        int option = javax.swing.JOptionPane.showConfirmDialog(null,
                "Export complete. Do you want to open the file now?", "Open File",
                javax.swing.JOptionPane.YES_NO_OPTION);
        if (option == javax.swing.JOptionPane.YES_OPTION) {
            if (java.awt.Desktop.isDesktopSupported()) {
                try {
                    java.awt.Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Unable to open the file: " + e.getMessage());
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Desktop not supported on this system.");
            }
        }

    }

    // Check if a string is likely to be a date
    private static boolean is_likely_date(String value) {
        for (String format : date_formats) {
            try {
                new SimpleDateFormat(format).parse(value);
                return true;
            } catch (ParseException ignored) {
            }
        }
        return false;
    }

    // Attempt to parse a date string using supported formats
    private static Date parse_date(String value) {
        for (String format : date_formats) {
            try {
                return new SimpleDateFormat(format).parse(value);
            } catch (ParseException ignored) {
            }
        }
        return null;
    }
}
