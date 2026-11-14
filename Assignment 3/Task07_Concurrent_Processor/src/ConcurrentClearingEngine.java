package task07_executor;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Task 07: High-Throughput Interbank Clearing Engine.
 * 
 * Demonstrates:
 * 1. Fixed Thread Pool management via ExecutorService.
 * 2. Callable<T> tasks returning typed results and throwing checked exceptions.
 * 3. Future<T> management with SLA timeout controls (get(timeout, unit)).
 * 4. Fault tolerance catching ExecutionException and TimeoutException.
 * 5. Concurrent aggregations via ConcurrentHashMap and LongAdder.
 * 6. Graceful shutdown protocol (shutdown() -> awaitTermination() -> shutdownNow()).
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class ConcurrentClearingEngine {

    private static final int POOL_SIZE = 4;
    private static final long SLA_TIMEOUT_MS = 250; // SLA Timeout limit for clearing responses

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 3 - TASK 07: CONCURRENT CLEARING ENGINE & EXECUTOR SERVICE        ");
        System.out.println("   Student A: Devi Vara Prasad | Reg: 192365032                                 ");
        System.out.println("   Student B: [Student B Name] | Reg: [Student B Register Number]               ");
        System.out.println("   Configuration: FixedThreadPool(Size=" + POOL_SIZE + ") | SLA Timeout=" + SLA_TIMEOUT_MS + "ms   ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

        // Prepare 12 diverse clearing tasks
        List<TransactionTask> tasks = Arrays.asList(
            new TransactionTask("ACH-TX-1001", 1250.00, 40, false),
            new TransactionTask("ACH-TX-1002", 4800.50, 60, false),
            new TransactionTask("ACH-TX-1003", 2500000.00, 50, false), // Compliance threshold breach
            new TransactionTask("ACH-TX-1004", 890.00, 30, false),
            new TransactionTask("ACH-TX-1005", 15200.00, 70, false),
            new TransactionTask("ACH-TX-1006", 999.99, 500, false),   // SLA Timeout (>250ms)
            new TransactionTask("ACH-TX-1007", 7450.00, 45, false),
            new TransactionTask("ACH-TX-1008", 3200.00, 50, true),    // Gateway Connection Failure
            new TransactionTask("ACH-TX-1009", 18500.00, 80, false),
            new TransactionTask("ACH-TX-1010", 610.25, 35, false),
            new TransactionTask("ACH-TX-1011", 620000.00, 90, false),
            new TransactionTask("ACH-TX-1012", 4100.00, 450, false)   // SLA Timeout (>250ms)
        );

        System.out.println("Submitting " + tasks.size() + " interbank clearing transactions to thread pool...");
        Map<String, Future<TransactionResult>> futureMap = new LinkedHashMap<>();

        long batchStartTime = System.currentTimeMillis();

        for (TransactionTask task : tasks) {
            Future<TransactionResult> future = executor.submit(task);
            futureMap.put(task.getTransactionId(), future);
        }

        // Metrics Tracking using Concurrent Collections
        ConcurrentHashMap<String, LongAdder> statusDistribution = new ConcurrentHashMap<>();
        AtomicLong totalSettledCents = new AtomicLong(0);
        AtomicLong totalFeesCents = new AtomicLong(0);
        List<TransactionResult> allOutcomes = new ArrayList<>();

        System.out.println("\n[CLEARING SETTLEMENT LOG]");
        System.out.printf("%-13s | %-11s | %-21s | %-9s | %-9s | %s%n",
                "Transaction ID", "Amount ($)", "Clearing Status", "Latency", "Fee ($)", "Details");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        for (Map.Entry<String, Future<TransactionResult>> entry : futureMap.entrySet()) {
            String txId = entry.getKey();
            Future<TransactionResult> future = entry.getValue();

            TransactionResult outcome;
            try {
                // Enforce SLA timeout constraint
                outcome = future.get(SLA_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            } catch (TimeoutException te) {
                // Cancel future if SLA breached
                future.cancel(true);
                outcome = new TransactionResult(txId, 0.0, TransactionResult.Status.TIMEOUT_BREACH,
                        SLA_TIMEOUT_MS, 0.0, "SLA Violated: Response exceeded " + SLA_TIMEOUT_MS + "ms timeout threshold");

            } catch (ExecutionException ee) {
                // Task threw an internal exception (e.g. gateway reset)
                outcome = new TransactionResult(txId, 0.0, TransactionResult.Status.GATEWAY_FAILURE,
                        0, 0.0, "Downstream gateway aborted: " + ee.getCause().getMessage());

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                outcome = new TransactionResult(txId, 0.0, TransactionResult.Status.GATEWAY_FAILURE,
                        0, 0.0, "Transaction interrupted during processing");
            }

            allOutcomes.add(outcome);

            // Thread-safe metric updates
            statusDistribution.computeIfAbsent(outcome.getStatus().name(), k -> new LongAdder()).increment();
            if (outcome.getStatus() == TransactionResult.Status.SUCCESS) {
                totalSettledCents.addAndGet(Math.round(outcome.getAmount() * 100));
                totalFeesCents.addAndGet(Math.round(outcome.getClearingFee() * 100));
            }

            System.out.printf("%-13s | $%10.2f | %-21s | %5d ms  | $%7.2f | %s%n",
                    outcome.getTransactionId(), outcome.getAmount(), outcome.getStatus(),
                    outcome.getLatencyMs(), outcome.getClearingFee(), outcome.getMessage());
        }

        long batchTotalTimeMs = System.currentTimeMillis() - batchStartTime;
        double throughputTps = (tasks.size() / (batchTotalTimeMs / 1000.0));

        System.out.println("--------------------------------------------------------------------------------------------------------");

        // Graceful Executor Shutdown Protocol
        System.out.println("\n[SHUTDOWN] Initiating graceful termination of ExecutorService thread pool...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                System.out.println("[SHUTDOWN] Immediate timeout: Forcing shutdownNow()...");
                executor.shutdownNow();
            } else {
                System.out.println("[SHUTDOWN] Thread pool terminated gracefully. All active workers retired.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Summary Analytics
        System.out.println("\n================================================================================");
        System.out.println("                      CLEARING BATCH SETTLEMENT SUMMARY                         ");
        System.out.println("================================================================================");
        System.out.printf("Total Transactions Processed : %d%n", tasks.size());
        System.out.printf("Batch Execution Time         : %d ms%n", batchTotalTimeMs);
        System.out.printf("Engine Throughput            : %.2f transactions/sec%n", throughputTps);
        System.out.printf("Total Settled Volume         : $%,.2f%n", (totalSettledCents.get() / 100.0));
        System.out.printf("Total Clearing Fees Accrued  : $%,.2f%n", (totalFeesCents.get() / 100.0));
        System.out.println("\nStatus Breakdown:");
        statusDistribution.forEach((status, count) -> {
            System.out.printf("  - %-22s: %2d (%.1f%%)%n", status, count.sum(), (count.sum() * 100.0 / tasks.size()));
        });
        System.out.println("================================================================================");

        // Export Metrics CSV
        exportMetricsCSV(allOutcomes, batchTotalTimeMs, throughputTps);
    }

    private static void exportMetricsCSV(List<TransactionResult> outcomes, long batchMs, double tps) throws Exception {
        File dir1 = new File("Assignment 3/Task07_Concurrent_Processor/results");
        dir1.mkdirs();
        File f1 = new File(dir1, "Task07_Throughput_Metrics.csv");

        File dir2 = new File("Assignment 3/Results");
        dir2.mkdirs();
        File f2 = new File(dir2, "Task07_Throughput_Metrics.csv");

        for (File f : Arrays.asList(f1, f2)) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Transaction_ID,Amount,Status,Latency_Ms,Clearing_Fee,Message");
                for (TransactionResult r : outcomes) {
                    pw.printf("%s,%.2f,%s,%d,%.2f,\"%s\"%n",
                            r.getTransactionId(), r.getAmount(), r.getStatus(),
                            r.getLatencyMs(), r.getClearingFee(), r.getMessage().replace("\"", "'"));
                }
            }
        }
        System.out.println("[CSV EXPORT] Throughput metrics exported to Task07_Throughput_Metrics.csv");
    }
}
