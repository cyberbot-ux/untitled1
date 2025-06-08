import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
// Manager class for loading, caching, and accessing card transaction data from XML files
public class Program_Manager {

    // In-memory cache to hold transactions by file name
    private static final Map<String, List<Card_Transaction>> transaction_cache = new HashMap<>();

    // Load and parse all XML files at startup
    public static void load_files(List<File> xml_files) {
        transaction_cache.clear(); // Clear existing cache

        for (File file : xml_files) {
            List<Card_Transaction> parsed = XML_Parser.parse_single_file(file);
            transaction_cache.put(file.getName(), parsed);
        }
    }

    // Add a new XML file to the cache (called during runtime import)
    public static void add_file_to_cache(File xml_file) {
        if (!transaction_cache.containsKey(xml_file.getName())) {
            List<Card_Transaction> parsed = XML_Parser.parse_single_file(xml_file);
            transaction_cache.put(xml_file.getName(), parsed);
        }
    }

    // Get transactions for a specific file, or all combined if "All Files" is selected
    public static List<Card_Transaction> get_transactions(String file_name) {
        if ("All Files".equals(file_name)) {
            return transaction_cache.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
        return transaction_cache.getOrDefault(file_name, Collections.emptyList());
    }

    // Optional helper to retrieve names of all cached files
    public static Set<String> get_cached_file_names() {
        return transaction_cache.keySet();
    }

    // Get all XML files from the xml_files directory
    public static List<File> get_all_files() {
        File folder = new File("xml_files");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
        return files != null ? Arrays.asList(files) : new ArrayList<>();
    }
}
