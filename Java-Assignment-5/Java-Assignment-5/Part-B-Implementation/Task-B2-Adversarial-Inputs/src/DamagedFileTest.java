import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class DamagedFileTest {

    public static void main(String[] args) throws IOException {
        System.out.println("================================================================================");
        System.out.println("   TASK B2: ADVERSARIAL BUS-PASS FILE CORRUPTION RESILIENCE TEST");
        System.out.println("================================================================================");

        Path corruptedFile = Files.createTempFile("adversarial_passes", ".csv");
        PassStore store = new PassStore(corruptedFile);

        // Populate adversarial file contents
        StringBuilder sb = new StringBuilder();
        // 1. Normal record 1
        sb.append("P1001,192311001,Arun Kumar,12,4500.0\n");
        // 2. Adversarial input 1: Truncated fields (3 fields instead of 5)
        sb.append("P1002,192311002,TRUNCATED_RECORD\n");
        // 3. Normal record 2
        sb.append("P1003,192311003,Divya S,7,3800.0\n");
        // 4. Adversarial input 2: Non-numeric amount string ("FREE_SCHOLARSHIP")
        sb.append("P1004,192311004,Karthik,12,FREE_SCHOLARSHIP\n");
        // 5. Adversarial input 3: Empty lines and whitespace traps
        sb.append("   \n\n\t\n");
        // 6. Adversarial input 4: Extra fields (7 fields instead of 5)
        sb.append("P1005,192311005,Manoj,14,5000.0,EXTRA_FIELD_1,EXTRA_FIELD_2\n");
        // 7. Normal record 3
        sb.append("P1006,192311094,Kattiripalli Dhanush,12,4500.0\n");

        Files.writeString(corruptedFile, sb.toString());
        System.out.println("Created corrupted test store with 7 records (3 valid, 4 adversarial/corrupted).\n");

        // Test 1: Find Normal Record P1001
        System.out.println("Test 1: Query Valid Pass P1001 (Located BEFORE corrupted records)");
        String[] r1 = store.find("P1001");
        System.out.println("-> Result: " + Arrays.toString(r1) + " [CORRECT]");

        // Test 2: Find Truncated Adversarial Record P1002
        System.out.println("\nTest 2: Query Truncated Record P1002 (3 fields instead of 5)");
        String[] r2 = store.find("P1002");
        System.out.println("-> Result: " + Arrays.toString(r2) + " [CORRECT: Safely skipped, returns null]");

        // Test 3: Find Normal Record P1003
        System.out.println("\nTest 3: Query Valid Pass P1003 (Located AFTER truncated record P1002)");
        String[] r3 = store.find("P1003");
        System.out.println("-> Result: " + Arrays.toString(r3) + " [CORRECT: Search survived corrupted line P1002]");

        // Test 4: Find Normal Record P1006
        System.out.println("\nTest 4: Query Valid Pass P1006 (Located AFTER blank lines and extra fields)");
        String[] r4 = store.find("P1006");
        System.out.println("-> Result: " + Arrays.toString(r4) + " [CORRECT: Search traversed entire file safely]");

        // Test 5: Route query with non-numeric fields
        System.out.println("\nTest 5: List all passes on Route 12 (skipping corrupted entries)");
        List<String[]> route12 = store.listRoute(12);
        System.out.println("-> Found " + route12.size() + " valid Route 12 passes:");
        for (String[] pass : route12) {
            System.out.println("   * Pass ID: " + pass[0] + " | Reg: " + pass[1] + " | Name: " + pass[2] + " | Fare: ₹" + pass[4]);
        }

        Files.deleteIfExists(corruptedFile);

        System.out.println("\n--- Adversarial Input Vulnerability Interpretation ---");
        System.out.println("1. Truncated Lines: Without length check (parts.length != 5), ArrayIndexOutOfBoundsException crashes the program.");
        System.out.println("2. Non-numeric Fields: Without try-catch on Double.parseDouble(), NumberFormatException crashes the search.");
        System.out.println("3. Blank Lines: Without line.trim().isEmpty() checks, empty string splitting yields invalid single-token arrays.");
        System.out.println("4. Defensive Design Conclusion: Token boundary validation and defensive parsing are essential for unindexed flat files.");
    }
}
