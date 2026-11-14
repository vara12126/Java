package q1_string;

/**
 * Generates an E-Ticket using java.lang.StringBuilder.
 * 
 * Performance characteristic:
 * StringBuilder uses a mutable, resizable byte[] array buffer. It does not synchronize
 * any method calls, eliminating lock acquisition overhead. It allocates characters in place,
 * achieving optimal linear O(N * M) time complexity and near-zero intermediate object creation.
 */
public class StringBuilderGenerator implements TicketGenerator {

    @Override
    public String generateTicket(ETicket ticket) {
        String[][] fields = ticket.getFieldPairs();
        // Pre-allocate buffer capacity to eliminate dynamic array re-allocations
        StringBuilder sb = new StringBuilder(2048);
        sb.append("=================================================================\n");
        sb.append("           INDIAN RAILWAYS E-TICKETING PASSENGER RESERVATION      \n");
        sb.append("=================================================================\n");

        for (int i = 0; i < fields.length; i++) {
            if (i + 1 < 10) {
                sb.append("[0").append(i + 1).append("] ");
            } else {
                sb.append("[").append(i + 1).append("] ");
            }
            sb.append(fields[i][0]).append(" : ").append(fields[i][1]).append('\n');
        }

        sb.append("=================================================================\n");
        sb.append("       END OF ELECTRONIC TICKET RECORD - VERIFIED DIGITAL ENTRY   \n");
        sb.append("=================================================================\n");
        return sb.toString();
    }

    @Override
    public String getStrategyName() {
        return "StringBuilder (Unsynchronized)";
    }

    @Override
    public long estimateIntermediateObjectsPerTicket() {
        // Exactly 1 StringBuilder instance is created, followed by 1 final String returned by toString().
        // No intermediate String objects are created during the 50 field appends.
        return 1L;
    }
}
