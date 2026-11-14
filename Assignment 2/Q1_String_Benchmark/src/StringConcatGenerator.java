package q1_string;

/**
 * Generates an E-Ticket using immutable String concatenation with the '+' operator.
 * 
 * Performance characteristic:
 * Because java.lang.String is immutable, each '+' concatenation in a loop creates a new
 * String object and copies the previous characters into a newly allocated byte[] array.
 * This leads to quadratic O(N * M^2) time complexity and massive intermediate object churn.
 */
public class StringConcatGenerator implements TicketGenerator {

    @Override
    public String generateTicket(ETicket ticket) {
        String[][] fields = ticket.getFieldPairs();
        String result = "=================================================================\n";
        result = result + "           INDIAN RAILWAYS E-TICKETING PASSENGER RESERVATION      \n";
        result = result + "=================================================================\n";

        for (int i = 0; i < fields.length; i++) {
            // Immutable string concatenation creates multiple temporary String objects per line
            result = result + String.format("[%02d] ", (i + 1)) + fields[i][0] + " : " + fields[i][1] + "\n";
        }

        result = result + "=================================================================\n";
        result = result + "       END OF ELECTRONIC TICKET RECORD - VERIFIED DIGITAL ENTRY   \n";
        result = result + "=================================================================\n";
        return result;
    }

    @Override
    public String getStrategyName() {
        return "String Concatenation (+)";
    }

    @Override
    public long estimateIntermediateObjectsPerTicket() {
        // In the loop of 50 fields:
        // Each loop iteration creates:
        // 1. formatted prefix String "[xx] "
        // 2. intermediate String for "[xx] " + fields[i][0]
        // 3. intermediate String for ... + " : "
        // 4. intermediate String for ... + fields[i][1]
        // 5. intermediate String for ... + "\n"
        // 6. result = result + ... (new heap String + byte[] buffer copy)
        // Header and footer add another 10 allocations.
        // Conservatively ~ 150 intermediate String and buffer objects per ticket.
        return 150L;
    }
}
