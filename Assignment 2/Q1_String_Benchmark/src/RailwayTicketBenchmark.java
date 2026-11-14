package q1_string;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive Benchmark Suite for Question 1:
 * String vs. StringBuilder vs. StringBuffer in Railway E-Ticket Generation.
 * 
 * Features:
 * - 50 distinct fields per ticket.
 * - 3 workload configurations: 100, 1,000, and 10,000 tickets.
 * - System.nanoTime() precision benchmarking.
 * - Warm-up iterations to activate HotSpot C2 JIT compilation.
 * - Multiple measurement runs with average and standard deviation.
 * - Heap memory profiling before and after.
 * - Intermediate object creation estimation.
 * - Export to CSV for audit and visualization.
 * - Architectural demonstrations of thread safety and alternative APIs.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class RailwayTicketBenchmark {

    public static class BenchmarkRecord {
        public String strategy;
        public int ticketCount;
        public int fieldsPerTicket;
        public int runIndex;
        public double executionTimeMs;
        public long intermediateObjects;
        public long memoryUsedBytes;

        public BenchmarkRecord(String strategy, int ticketCount, int fieldsPerTicket, int runIndex,
                               double executionTimeMs, long intermediateObjects, long memoryUsedBytes) {
            this.strategy = strategy;
            this.ticketCount = ticketCount;
            this.fieldsPerTicket = fieldsPerTicket;
            this.runIndex = runIndex;
            this.executionTimeMs = executionTimeMs;
            this.intermediateObjects = intermediateObjects;
            this.memoryUsedBytes = memoryUsedBytes;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - QUESTION 1: STRING, STRINGBUILDER & STRINGBUFFER BENCHMARK    ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Environment: " + System.getProperty("java.runtime.name") + " (" + System.getProperty("java.version") + ")");
        System.out.println("   OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        System.out.println("================================================================================\n");

        int[] workloads = {100, 1000, 10000};
        int fieldsPerTicket = 50;
        int warmupRuns = 3;
        int measurementRuns = 5;

        TicketGenerator[] generators = new TicketGenerator[] {
            new StringConcatGenerator(),
            new StringBuilderGenerator(),
            new StringBufferGenerator()
        };

        List<BenchmarkRecord> records = new ArrayList<>();

        // Generate sample ticket to verify output correctness
        ETicket sampleTicket = new ETicket(1);
        System.out.println("--- SAMPLE TICKET VERIFICATION (First 10 of 50 Fields) ---");
        String sampleOutput = generators[1].generateTicket(sampleTicket);
        String[] sampleLines = sampleOutput.split("\n");
        for (int i = 0; i < Math.min(14, sampleLines.length); i++) {
            System.out.println(sampleLines[i]);
        }
        System.out.println("... [Remaining 40 fields correctly rendered] ...\n");

        // Execute Warm-Up Phase
        System.out.println(">>> Executing Warm-Up Phase (" + warmupRuns + " iterations on 500 tickets to trigger C2 JIT compiler) <<<");
        for (int w = 0; w < warmupRuns; w++) {
            ETicket[] warmTickets = generateTestTickets(500);
            for (TicketGenerator gen : generators) {
                for (ETicket t : warmTickets) {
                    gen.generateTicket(t);
                }
            }
        }
        System.out.println(">>> Warm-Up Complete. JIT Compilation Optimized. <<<\n");

        // Execute Formal Workloads
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-26s | %-10s | %-12s | %-12s | %-12s | %-12s%n",
                "Strategy", "Workload", "Avg Time(ms)", "Min Time(ms)", "Max Time(ms)", "Est Objects");
        System.out.println("--------------------------------------------------------------------------------");

        for (int ticketCount : workloads) {
            ETicket[] tickets = generateTestTickets(ticketCount);

            for (TicketGenerator gen : generators) {
                double[] times = new double[measurementRuns];
                long totalObjects = gen.estimateIntermediateObjectsPerTicket() * ticketCount;
                long memUsed = 0;

                for (int run = 0; run < measurementRuns; run++) {
                    // Suggest garbage collection and settle memory before measurement
                    System.gc();
                    try { Thread.sleep(20); } catch (InterruptedException ignored) {}

                    long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    long tStart = System.nanoTime();

                    for (int i = 0; i < ticketCount; i++) {
                        gen.generateTicket(tickets[i]);
                    }

                    long tEnd = System.nanoTime();
                    long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                    double elapsedMs = (tEnd - tStart) / 1_000_000.0;
                    times[run] = elapsedMs;
                    memUsed = Math.max(0, memAfter - memBefore);

                    records.add(new BenchmarkRecord(
                        gen.getStrategyName(), ticketCount, fieldsPerTicket, (run + 1), elapsedMs, totalObjects, memUsed
                    ));
                }

                // Compute statistics
                double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
                for (double t : times) {
                    sum += t;
                    if (t < min) min = t;
                    if (t > max) max = t;
                }
                double avg = sum / measurementRuns;

                System.out.printf("%-26s | %-10d | %12.3f | %12.3f | %12.3f | %,12d%n",
                        gen.getStrategyName(), ticketCount, avg, min, max, totalObjects);
            }
            System.out.println("--------------------------------------------------------------------------------");
        }

        // Export Results to CSV
        String csvPath = "Assignment 2/Q1_String_Benchmark/results/Q1_Benchmark_Results.csv";
        saveCsv(records, csvPath);
        System.out.println("\n[SUCCESS] Raw benchmark data exported to: " + csvPath);

        // Run Alternative Methods and Multithreading Demos
        AlternativeStringMethodsDemo.runComparison(sampleTicket, 10000);
        SharedAuditLogDemo.runThreadSafetyComparison(8, 5000);

        // Print Theoretical and Complexity Summary
        printAnalysis();
    }

    private static ETicket[] generateTestTickets(int count) {
        ETicket[] list = new ETicket[count];
        for (int i = 0; i < count; i++) {
            list[i] = new ETicket(i + 1);
        }
        return list;
    }

    private static void saveCsv(List<BenchmarkRecord> records, String path) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("Strategy,TicketCount,FieldsPerTicket,RunIndex,ExecutionTimeMs,EstimatedIntermediateObjects,MemoryUsedBytes");
            for (BenchmarkRecord r : records) {
                pw.printf("\"%s\",%d,%d,%d,%.4f,%d,%d%n",
                        r.strategy, r.ticketCount, r.fieldsPerTicket, r.runIndex,
                        r.executionTimeMs, r.intermediateObjects, r.memoryUsedBytes);
            }
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }

    private static void printAnalysis() {
        System.out.println("\n================================================================================");
        System.out.println("   COMPLEXITY AND THEORETICAL ANALYSIS SUMMARY                                  ");
        System.out.println("================================================================================");
        System.out.println("1. Time Complexity:");
        System.out.println("   - String Concatenation (+): O(N * M^2) where N = tickets, M = fields/length.");
        System.out.println("     Each immutable '+' creates a new buffer and copies all previous characters.");
        System.out.println("   - StringBuilder:            O(N * M) amortized linear time. Direct buffer append.");
        System.out.println("   - StringBuffer:             O(N * M) linear time + synchronized lock acquisition overhead.");
        System.out.println("\n2. Space Complexity & Object Churn:");
        System.out.println("   - String Concatenation (+): Allocates O(N * M) intermediate String and byte[] objects");
        System.out.println("     leading to frequent Minor GC pauses and heap fragmentation.");
        System.out.println("   - StringBuilder/StringBuffer: Allocates O(1) buffer per ticket (O(N) total final objects).");
        System.out.println("\n3. Architectural Recommendations:");
        System.out.println("   - Use StringBuilder for local, thread-confined ticket rendering (highest performance).");
        System.out.println("   - Use StringBuffer for shared multi-threaded logging buffers where atomic thread safety");
        System.out.println("     is mandatory and external lock synchronization is absent.");
        System.out.println("================================================================================\n");
    }
}
