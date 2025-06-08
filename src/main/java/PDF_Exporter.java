import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.swing.JTable;
import java.io.*;
// Utility class to export JTable data into a PDF file using Apache PDFBox
public class PDF_Exporter {

    // Main method to export JTable content to a formatted PDF document
    public static void export_to_pdf(JTable table, File file) throws IOException {
        PDDocument document = new PDDocument();

        // Font and layout setup
        PDType1Font font = PDType1Font.HELVETICA;
        float font_size = 10;
        float line_spacing = 14.5f;
        float margin = 40;
        float y_start = 720;
        int max_lines_per_page = 45;
        int line_count = 0;

        // Start first page
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);

        PDPageContentStream stream = new PDPageContentStream(document, page);
        stream.setFont(font, font_size);
        stream.beginText();
        stream.setLeading(line_spacing);
        stream.newLineAtOffset(margin, y_start);

        // Print header row (column names)
        for (int col = 0; col < table.getColumnCount(); col++) {
            stream.showText(pad_right(table.getColumnName(col), 20));
        }
        stream.newLine();
        line_count++;

        // Loop through table rows
        for (int row = 0; row < table.getRowCount(); row++) {

            // Start new page if max lines reached
            if (line_count >= max_lines_per_page) {
                stream.endText();
                stream.close();

                page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);
                stream = new PDPageContentStream(document, page);
                stream.setFont(font, font_size);
                stream.beginText();
                stream.setLeading(line_spacing);
                stream.newLineAtOffset(margin, y_start);

                // Reprint header on new page
                for (int col = 0; col < table.getColumnCount(); col++) {
                    stream.showText(pad_right(table.getColumnName(col), 20));
                }
                stream.newLine();
                line_count = 1;
            }

            // Print each cell value in the row
            for (int col = 0; col < table.getColumnCount(); col++) {
                Object value = table.getValueAt(row, col);
                String text = (value != null) ? value.toString() : "";
                stream.showText(pad_right(text, 20));
            }

            stream.newLine();
            line_count++;
        }

        // Finalize stream and document
        stream.endText();
        stream.close();

        document.save(file);
        document.close();
        // Ask user if they want to open the file now
        int option = javax.swing.JOptionPane.showConfirmDialog(null,
                "Export complete. Do you want to open the PDF now?", "Open PDF",
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

    // Helper method to pad or trim a string for fixed-width formatting
    private static String pad_right(String text, int length) {
        if (text.length() >= length) return text.substring(0, length - 1) + "…";
        return String.format("%-" + length + "s", text);
    }
}

