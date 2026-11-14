package task05_concurrency;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Task 05: Race Conditions, Synchronization Techniques & Performance Benchmark.
 * 
 * Compares:
 * 1. Unsynchronized Account (Data race & lost updates)
 * 2. Synchronized Methods (Intrinsic lock)
 * 3. Synchronized Blocks (Fine-grained object lock)
 * 4. AtomicAccount (Lock-free AtomicLong with CAS)
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class SynchronizationBenchmark {

    public static final double INITIAL_BALANCE = 10000.00;
    public static final int NUM_THREADS = 10;
    public static final int OPS_PER_THREAD = 1000; // 1,000 deposits + 1,000 withdrawals = 2,000 ops/thread -> 20,000 ops total
    public static final int NUM_RUNS = 10;

    public static class RunResult {
        public final int runNumber;
        public final String implementation;
        public final double finalBalance;
        public final double balanceError;
        public final long transactionCount;
        public final double elapsedMs;
        public final boolean isConsistent;

        public RunResult(int runNumber, String implementation, double finalBalance, 
                         double balanceError, long transactionCount, double elapsedMs, boolean isConsistent) {
            this.runNumber = runNumber;
            this.implementation = implementation;
            this.finalBalance = finalBalance;
            this.balanceError = balanceError;
            this.transactionCount = transactionCount;
            this.elapsedMs = elapsedMs;
            this.isConsistent = isConsistent;
        }
    }

    public static RunResult executeSingleRun(int runNum, BankAccount account) throws InterruptedException {
        account.reset(INITIAL_BALANCE);
        Thread[] threads = new Thread[NUM_THREADS];

        long startTime = System.nanoTime();

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPS_PER_THREAD; j++) {
                    account.deposit(100.0);
                    account.withdraw(100.0);
                }
            }, "Worker-" + i);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long elapsedNanos = System.nanoTime() - startTime;
        double elapsedMs = elapsedNanos / 1_000_000.0;

        double finalBal = account.getBalance();
        double error = Math.abs(finalBal - INITIAL_BALANCE);
        boolean consistent = (error < 0.001);

        return new RunResult(runNum, account.getImplementationName(), finalBal, error, 
                             account.getTransactionCount(), elapsedMs, consistent);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 3 - TASK 05: RACE CONDITIONS & SYNCHRONIZATION BENCHMARK         ");
        System.out.println("   Student A: Devi Vara Prasad | Reg: 192365032                                 ");
        System.out.println("   Student B: [Student B Name] | Reg: [Student B Register Number]               ");
        System.out.println("   Configuration: " + NUM_THREADS + " Threads | 20,000 Operations/Run | Initial: $10,000.00  ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        List<BankAccount> candidates = Arrays.asList(
            new UnsynchronizedAccount(INITIAL_BALANCE),
            new SynchronizedMethodAccount(INITIAL_BALANCE),
            new SynchronizedBlockAccount(INITIAL_BALANCE),
            new AtomicAccount(INITIAL_BALANCE)
        );

        Map<String, List<RunResult>> allResults = new LinkedHashMap<>();
        for (BankAccount candidate : candidates) {
            allResults.put(candidate.getImplementationName(), new ArrayList<>());
        }

        // Warm-up iteration
        for (BankAccount candidate : candidates) {
            executeSingleRun(0, candidate);
        }

        // Execute 10 Benchmark Runs
        for (int run = 1; run <= NUM_RUNS; run++) {
            for (BankAccount candidate : candidates) {
                RunResult res = executeSingleRun(run, candidate);
                allResults.get(candidate.getImplementationName()).add(res);
            }
        }

        // Print Summary Tables
        System.out.printf("%-38s | %-12s | %-14s | %-10s | %-12s%n",
                "Implementation", "Avg Time(ms)", "StdDev(ms)", "Consistent?", "Avg Error($)");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        List<String[]> csvRows = new ArrayList<>();

        for (Map.Entry<String, List<RunResult>> entry : allResults.entrySet()) {
            String name = entry.getKey();
            List<RunResult> runs = entry.getValue();

            double totalTime = 0;
            double totalError = 0;
            int consistentCount = 0;

            for (RunResult r : runs) {
                totalTime += r.elapsedMs;
                totalError += r.balanceError;
                if (r.isConsistent) consistentCount++;
                csvRows.add(new String[]{
                    String.valueOf(r.runNumber),
                    r.implementation,
                    String.format(Locale.US, "%.2f", r.finalBalance),
                    String.format(Locale.US, "%.2f", r.balanceError),
                    String.valueOf(r.transactionCount),
                    String.format(Locale.US, "%.3f", r.elapsedMs),
                    r.isConsistent ? "PASSED" : "CORRUPTED"
                });
            }

            double meanTime = totalTime / runs.size();
            double avgError = totalError / runs.size();

            // Calculate Standard Deviation
            double sumSqDiff = 0;
            for (RunResult r : runs) {
                sumSqDiff += Math.pow(r.elapsedMs - meanTime, 2);
            }
            double stdDev = Math.sqrt(sumSqDiff / runs.size());

            String consistencyStr = consistentCount + "/" + NUM_RUNS + " (" + (consistentCount * 10) + "%)";
            System.out.printf("%-38s | %10.3f ms | %10.3f ms   | %-10s | $%10.2f%n",
                    name, meanTime, stdDev, consistencyStr, avgError);
        }

        System.out.println("--------------------------------------------------------------------------------------------------------");

        // Export CSV
        exportCSV(csvRows);

        System.out.println("\nKey Concurrency Findings:");
        System.out.println("1. Unsynchronized Account suffers catastrophic data loss and ledger corruption (Lost Updates).");
        System.out.println("2. Synchronized methods guarantee 100% data integrity but impose lock acquisition overhead.");
        System.out.println("3. Synchronized blocks reduce critical section footprint, improving contention.");
        System.out.println("4. Atomic CAS provides 100% correctness with superior throughput by eliminating OS context switching.");
        System.out.println("================================================================================\n");
    }

    private static void exportCSV(List<String[]> rows) throws Exception {
        File dir1 = new File("Assignment 3/Task05_Race_Conditions/results");
        dir1.mkdirs();
        File f1 = new File(dir1, "Task05_Sync_Benchmark.csv");

        File dir2 = new File("Assignment 3/Results");
        dir2.mkdirs();
        File f2 = new File(dir2, "Task05_Sync_Benchmark.csv");

        for (File f : Arrays.asList(f1, f2)) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Run_Number,Implementation,Final_Balance,Balance_Error,Transaction_Count,Elapsed_Ms,Consistency_Status");
                for (String[] r : rows) {
                    pw.printf("%s,\"%s\",%s,%s,%s,%s,%s%n", r[0], r[1], r[2], r[3], r[4], r[5], r[6]);
                }
            }
        }
        System.out.println("[CSV EXPORT] 10-run benchmark metrics exported to Task05_Sync_Benchmark.csv");
    }
}
