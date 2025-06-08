import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Parser utility for reading XML files and extracting CardTransaction data
public class XML_Parser {

    // Cache to store parsed data to avoid reprocessing same file
    private static final Map<String, List<Card_Transaction>> cache = new HashMap<>();

    // Parses all XML files in a given folder and returns combined transactions
    public static ArrayList<Card_Transaction> parse_folder(String folder_path) {
        ArrayList<Card_Transaction> all = new ArrayList<>();
        File folder = new File(folder_path);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder not found: " + folder_path);
            return all;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (files == null || files.length == 0) {
            System.out.println("No XML files found in: " + folder_path);
            return all;
        }

        // Loop through each XML file and parse transactions
        for (File file : files) {
            all.addAll(parse_single_file(file)); // Cached version
        }

        return all;
    }

    // Helper method to retrieve value from a specific tag inside an XML element
    private static String get_tag_value(Element parent, String tag_name) {
        NodeList nodes = parent.getElementsByTagName(tag_name);
        if (nodes != null && nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    // Parses a single XML file and extracts all CardTransaction records
    public static List<Card_Transaction> parse_single_file(File file) {
        String key = file.getAbsolutePath();
        if (cache.containsKey(key)) return cache.get(key);

        List<Card_Transaction> transactions = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();

            NodeList batch_list = doc.getElementsByTagName("Batch");

            for (int i = 0; i < batch_list.getLength(); i++) {
                Element batch = (Element) batch_list.item(i);
                String batch_date = get_tag_value(batch, "BatchDate");

                NodeList card_types = batch.getElementsByTagName("CardType");
                for (int j = 0; j < card_types.getLength(); j++) {
                    Element card = (Element) card_types.item(j);
                    String card_type = card.getAttribute("identType");
                    int quantity = Integer.parseInt(card.getAttribute("quantity"));
                    double gross = Double.parseDouble(card.getAttribute("grossAmount"));
                    double net = Double.parseDouble(card.getAttribute("netAmount"));

                    Element fee_element = (Element) card.getElementsByTagName("ChargeAmt").item(0);
                    double fee = Double.parseDouble(fee_element.getTextContent());

                    transactions.add(new Card_Transaction(batch_date, card_type, quantity, gross, net, fee));
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading file: " + file.getName());
            e.printStackTrace();
        }

        cache.put(key, transactions); // Save result in cache
        return transactions;
    }

    // Clears the cache (can be used during app refresh)
    public static void clear_cache() {
        cache.clear();
    }
}

