import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.stream.Collectors;
// Analyzer utility class for grouping card transactions into summary tables
public class Report_Analyzer {

    // Groups transactions by date, adds totals per date, and a grand total row
    public static JTable group_by_date_with_summary(List<Card_Transaction> transactions, boolean ascending) {
        String[] columns = {"Date", "Card Type", "Qty", "Gross", "Net", "Fee"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        if (transactions == null || transactions.isEmpty()) {
            return new JTable(model); // Empty table
        }

        // Group transactions by date
        Map<String, List<Card_Transaction>> by_date = transactions.stream()
                .collect(Collectors.groupingBy(Card_Transaction::get_batch_date));

        List<String> sorted_dates = new ArrayList<>(by_date.keySet());
        sorted_dates.sort(ascending ? Comparator.naturalOrder() : Comparator.reverseOrder());

        int grand_qty = 0;
        double grand_gross = 0, grand_net = 0, grand_fee = 0;

        for (String date : sorted_dates) {
            List<Card_Transaction> list = by_date.get(date);
            list.sort(Comparator.comparing(Card_Transaction::get_card_type));

            int total_qty = 0;
            double total_gross = 0, total_net = 0, total_fee = 0;

            // Add individual transactions
            for (Card_Transaction tx : list) {
                model.addRow(new Object[]{
                        tx.get_batch_date(),
                        tx.get_card_type(),
                        tx.get_quantity(),
                        tx.get_gross_amount(),
                        tx.get_net_amount(),
                        tx.get_fee()
                });

                total_qty += tx.get_quantity();
                total_gross += tx.get_gross_amount();
                total_net += tx.get_net_amount();
                total_fee += tx.get_fee();
            }

            // Add subtotal row for each date
            model.addRow(new Object[]{
                    "", "Total", total_qty,
                    String.format("%.2f", total_gross),
                    String.format("%.2f", total_net),
                    String.format("%.2f", total_fee)
            });
            model.addRow(new Object[]{"", "", "", "", "", ""}); // Empty row

            grand_qty += total_qty;
            grand_gross += total_gross;
            grand_net += total_net;
            grand_fee += total_fee;
        }

        // Add grand total row
        model.addRow(new Object[]{
                "", "Grand Total", grand_qty,
                String.format("%.2f", grand_gross),
                String.format("%.2f", grand_net),
                String.format("%.2f", grand_fee)
        });

        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        return table;
    }

    // Groups transactions by card type, sorts by date within each type, and adds totals
    public static JTable group_by_card_type(List<Card_Transaction> transactions) {
        String[] columns = {"Date", "Card Type", "Qty", "Gross", "Net", "Fee"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        if (transactions == null || transactions.isEmpty()) {
            return new JTable(model); // Empty table
        }

        // Group transactions by card type
        Map<String, List<Card_Transaction>> grouped = transactions.stream()
                .collect(Collectors.groupingBy(Card_Transaction::get_card_type));

        List<String> sorted_types = new ArrayList<>(grouped.keySet());
        sorted_types.sort(String::compareToIgnoreCase);

        int grand_qty = 0;
        double grand_gross = 0, grand_net = 0, grand_fee = 0;

        for (String type : sorted_types) {
            List<Card_Transaction> tx_list = grouped.get(type);
            tx_list.sort(Comparator.comparing(Card_Transaction::get_batch_date));

            int total_qty = 0;
            double total_gross = 0, total_net = 0, total_fee = 0;

            // Add individual transactions
            for (Card_Transaction tx : tx_list) {
                model.addRow(new Object[]{
                        tx.get_batch_date(),
                        tx.get_card_type(),
                        tx.get_quantity(),
                        tx.get_gross_amount(),
                        tx.get_net_amount(),
                        tx.get_fee()
                });

                total_qty += tx.get_quantity();
                total_gross += tx.get_gross_amount();
                total_net += tx.get_net_amount();
                total_fee += tx.get_fee();
            }

            // Add subtotal row for each card type
            model.addRow(new Object[]{
                    "", "Total", total_qty,
                    String.format("%.2f", total_gross),
                    String.format("%.2f", total_net),
                    String.format("%.2f", total_fee)
            });
            model.addRow(new Object[]{"", "", "", "", "", ""}); // Empty row

            grand_qty += total_qty;
            grand_gross += total_gross;
            grand_net += total_net;
            grand_fee += total_fee;
        }

        // Add grand total row
        model.addRow(new Object[]{
                "", "Grand Total", grand_qty,
                String.format("%.2f", grand_gross),
                String.format("%.2f", grand_net),
                String.format("%.2f", grand_fee)
        });

        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        return table;
    }
}
