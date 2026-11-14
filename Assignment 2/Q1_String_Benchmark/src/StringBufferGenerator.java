package q1_string;

/**
 * Generates an E-Ticket using java.lang.StringBuffer.
 * 
 * Performance characteristic:
 * StringBuffer operates similarly to StringBuilder but wraps every append and manipulation
 * method in a 'synchronized' block. While thread-safe, invoking 50+ synchronized methods
 * per ticket adds unnecessary monitor lock acquisition and cache invalidation overhead
 * in single-threaded environments.
 */
public class StringBufferGenerator implements TicketGenerator {

    @Override
    public String generateTicket(ETicket ticket) {
        String[][] fields = ticket.getFieldPairs();
        // Pre-allocated buffer with thread-safe synchronized methods
        StringBuffer sb = new StringBuffer(2048);
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
        return "StringBuffer (Synchronized)";
    }

    @Override
    public long estimateIntermediateObjectsPerTicket() {
        // Exactly 1 StringBuffer instance is created, followed by 1 final String returned by toString().
        // No intermediate String objects are created during the appends, but 54 synchronized method calls occur.
        return 1L;
    }
}
