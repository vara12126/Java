package q1_string;

import java.util.StringJoiner;

/**
 * Demonstrates and benchmarks alternative string formatting mechanisms in modern Java:
 * 1. String.join()
 * 2. StringJoiner
 * 3. String.format()
 * 
 * Explains performance differences, parsing overhead, and object creation.
 */
public class AlternativeStringMethodsDemo {

    public static void runComparison(ETicket sampleTicket, int iterations) {
        System.out.println("\n-----------------------------------------------------------------");
        System.out.println("   ALTERNATIVE STRING FORMATTING COMPARISON (Iterations: " + iterations + ")");
        System.out.println("-----------------------------------------------------------------");

        String[][] pairs = sampleTicket.getFieldPairs();
        String[] formattedPairs = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            formattedPairs[i] = pairs[i][0] + "=" + pairs[i][1];
        }

        // 1. StringJoiner
        long t0 = System.nanoTime();
        String sjResult = "";
        for (int it = 0; it < iterations; it++) {
            StringJoiner sj = new StringJoiner(" | ", "[TICKET_START: ", " :TICKET_END]");
            for (String p : formattedPairs) {
                sj.add(p);
            }
            sjResult = sj.toString();
        }
        long t1 = System.nanoTime();
        double timeStringJoinerMs = (t1 - t0) / 1_000_000.0;

        // 2. String.join()
        long t2 = System.nanoTime();
        String sjoinResult = "";
        for (int it = 0; it < iterations; it++) {
            sjoinResult = "[TICKET_START: " + String.join(" | ", formattedPairs) + " :TICKET_END]";
        }
        long t3 = System.nanoTime();
        double timeStringJoinMs = (t3 - t2) / 1_000_000.0;

        // 3. String.format()
        long t4 = System.nanoTime();
        String sformatResult = "";
        for (int it = 0; it < iterations; it++) {
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < pairs.length; i++) {
                temp.append(String.format("[%02d] %-15s : %s\n", (i + 1), pairs[i][0], pairs[i][1]));
            }
            sformatResult = temp.toString();
        }
        long t5 = System.nanoTime();
        double timeStringFormatMs = (t5 - t4) / 1_000_000.0;

        System.out.printf("%-25s : %10.3f ms | Throughput: %8.1f ops/sec%n", 
                "StringJoiner", timeStringJoinerMs, (iterations / (timeStringJoinerMs / 1000.0)));
        System.out.printf("%-25s : %10.3f ms | Throughput: %8.1f ops/sec%n", 
                "String.join()", timeStringJoinMs, (iterations / (timeStringJoinMs / 1000.0)));
        System.out.printf("%-25s : %10.3f ms | Throughput: %8.1f ops/sec%n", 
                "String.format()", timeStringFormatMs, (iterations / (timeStringFormatMs / 1000.0)));

        System.out.println("\nKey Technical Insights:");
        System.out.println("1. StringJoiner vs String.join():");
        System.out.println("   String.join() internally constructs a StringJoiner. Both leverage a shared");
        System.out.println("   internal compact char/byte array and pre-calculate lengths when possible.");
        System.out.println("2. String.format() Bottleneck:");
        System.out.println("   String.format() is substantially slower (typically 10x-30x slower) because it parses");
        System.out.println("   the format string via regex/state-machine on every call, allocates an Object[] varargs");
        System.out.println("   array, autoboxes all primitive parameters, and queries the host Locale.");
        System.out.println("3. Production Recommendation:");
        System.out.println("   Avoid String.format() in high-throughput hot paths (e.g. 100k tickets/sec); prefer");
        System.out.println("   StringBuilder.append() or StringJoiner for delimited serialization.");
    }
}
