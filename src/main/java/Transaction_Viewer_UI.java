import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
// UI class to display and manage card transaction data
public class Transaction_Viewer_UI extends JFrame {

    // UI Components
    private JTextArea output_area;
    private JComboBox<String> options_box;
    private JComboBox<String> file_selector_box;
    private List<File> xml_files;
    private JComboBox<String> sort_order_box;
    private JScrollPane table_scroll_pane;
    private JTable current_table;

    // Constructor that builds the UI
    public Transaction_Viewer_UI() {
        setTitle("Card Transactions Viewer");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sort, option, file selection dropdowns
        sort_order_box = new JComboBox<>(new String[]{"Newest to Oldest", "Oldest to Newest"});
        options_box = new JComboBox<>(new String[]{"Group by Date with Summary", "Group by Card Type"});
        file_selector_box = new JComboBox<>();

        // Top toolbar for controls
        JToolBar tool_bar = new JToolBar();
        tool_bar.setFloatable(false);

        tool_bar.add(new JLabel("Sort:"));
        tool_bar.add(sort_order_box);
        tool_bar.add(new JLabel("Option:"));
        tool_bar.add(options_box);
        tool_bar.add(new JLabel("File:"));
        tool_bar.add(file_selector_box);

        // Export and Add buttons
        JButton export_pdf_button = new JButton("Export PDF");
        JButton export_excel_button = new JButton("Export Excel");
        JButton add_xml_button = new JButton("Add XML File");

        // Action listeners
        export_pdf_button.addActionListener(e -> export_to_file("pdf"));
        export_excel_button.addActionListener(e -> export_to_file("excel"));
        add_xml_button.addActionListener(e -> import_xml_file());

        tool_bar.add(export_pdf_button);
        tool_bar.add(export_excel_button);
        tool_bar.add(add_xml_button);

        add(tool_bar, BorderLayout.NORTH);

        // Main display area
        output_area = new JTextArea();
        output_area.setEditable(false);
        table_scroll_pane = new JScrollPane(output_area);
        add(table_scroll_pane, BorderLayout.CENTER);

        // Load XML files
        xml_files = Program_Manager.get_all_files();
        Program_Manager.load_files(xml_files);

        file_selector_box.addItem("All Files");
        for (File file : xml_files) {
            file_selector_box.addItem(file.getName());
        }

        // Trigger view updates on dropdown changes
        options_box.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) update_view();
        });

        file_selector_box.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) update_view();
        });

        sort_order_box.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) update_view();
        });

        if (file_selector_box.getItemCount() > 0) {
            file_selector_box.setSelectedIndex(0);
        }
    }

    // Method to update the displayed table based on selected file/option
    private void update_view() {
        String file_name = (String) file_selector_box.getSelectedItem();
        String option = (String) options_box.getSelectedItem();

        if (file_name == null) {
            JTable empty_table = new JTable(new String[][]{{"No XML files found."}}, new String[]{"Message"});
            table_scroll_pane.setViewportView(empty_table);
            return;
        }

        List<Card_Transaction> transactions = Program_Manager.get_transactions(file_name);

        if (transactions.isEmpty()) {
            JTable empty_table = new JTable(new String[][]{{"No transactions found."}}, new String[]{"Message"});
            table_scroll_pane.setViewportView(empty_table);
            return;
        }

        boolean ascending = "Oldest to Newest".equals(sort_order_box.getSelectedItem());

        // Choose report type based on selected option
        JTable result_table = option.equals("Group by Card Type")
                ? Report_Analyzer.group_by_card_type(transactions)
                : Report_Analyzer.group_by_date_with_summary(transactions, ascending);

        table_scroll_pane.setViewportView(result_table);
        current_table = result_table;
    }

    // Method to export the currently shown table to PDF or Excel
    private void export_to_file(String type) {
        if (current_table == null || current_table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No table data to export.");
            return;
        }

        String file_name = "Report_" + System.currentTimeMillis();
        try {
            String base_dir = new File(".").getCanonicalPath();
            File export_file;

            if ("pdf".equalsIgnoreCase(type)) {
                File dir = new File(base_dir, "save_pdf");
                dir.mkdirs();
                export_file = new File(dir, file_name + ".pdf");
                export_to_pdf_in_background(current_table, export_file);
            } else {
                File dir = new File(base_dir, "save_excel");
                dir.mkdirs();
                export_file = new File(dir, file_name + ".xlsx");
                export_to_excel_in_background(current_table, export_file);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Export error:\n" + ex.getMessage());
        }
    }

    // Run PDF export in background to avoid UI freeze
    private void export_to_pdf_in_background(JTable table, File file) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    PDF_Exporter.export_to_pdf(table, file);
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(Transaction_Viewer_UI.this, "Export failed: " + ex.getMessage())
                    );
                }
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                if (file.exists()) {
                    int result = JOptionPane.showConfirmDialog(Transaction_Viewer_UI.this,
                            "PDF saved at:\n" + file.getAbsolutePath() + "\n\nOpen it now?",
                            "Export Successful",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(Transaction_Viewer_UI.this, "Cannot open file: " + e.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(Transaction_Viewer_UI.this, "Export failed — file not created.");
                }
            }
        }.execute();
    }

    // Run Excel export in background
    private void export_to_excel_in_background(JTable table, File file) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    Excel_Exporter.export_to_excel(table, file);
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(Transaction_Viewer_UI.this, "Export failed: " + ex.getMessage())
                    );
                }
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(Transaction_Viewer_UI.this, "Excel export successful!");
            }
        }.execute();
    }

    // Allow user to import XML file dynamically and refresh view
    private void import_xml_file() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select XML File");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            File dest = new File("xml_files", selected.getName());

            try {
                Files.copy(selected.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        Program_Manager.add_file_to_cache(dest);
                        xml_files = Program_Manager.get_all_files();
                        return null;
                    }

                    @Override
                    protected void done() {
                        file_selector_box.removeAllItems();
                        file_selector_box.addItem("All Files");
                        for (File f : xml_files) {
                            file_selector_box.addItem(f.getName());
                        }

                        file_selector_box.setSelectedItem(dest.getName());
                        update_view();
                        setCursor(Cursor.getDefaultCursor());

                        JOptionPane.showMessageDialog(Transaction_Viewer_UI.this, "File added and transactions updated!");
                    }
                }.execute();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to copy file: " + ex.getMessage());
            }
        }
    }
}

