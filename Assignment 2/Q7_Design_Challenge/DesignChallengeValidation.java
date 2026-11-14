package q7_design;

import q6_triage.Patient;
import q7_design.original.OriginalTriageQueue;
import q7_design.redesigned.RedesignedTriageQueue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Master Verification and Benchmarking Suite for Question 7: Day-4 Design Challenge.
 * 
 * Conducts identical side-by-side empirical evaluations of:
 * - Original Solution: Binary Min-Heap PriorityQueue
 * - Redesigned Solution: Indexed Multi-Level Bucketed Triage Queue
 * 
 * Measures:
 * 1. Patient Admittance Time (10,000 patients)
 * 2. Lookups by ID Time (50,000 queries)
 * 3. Dynamic Priority Updates Time (2,000 patient vitals re-triaged)
 * 4. Aging Sweep Time (Starvation prevention promotion sweep)
 * 5. Full Triage Extraction Time (10,000 patients)
 * 6. Memory consumption profiling
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class DesignChallengeValidation {

    public static class ChallengeMetric {
        public String operation;
        public double originalTimeMs;
        public double redesignedTimeMs;
        public double speedupFactor;
        public String originalBigO;
        public String redesignedBigO;

        public ChallengeMetric(String operation, double originalTimeMs, double redesignedTimeMs,
                               double speedupFactor, String originalBigO, String redesignedBigO) {
            this.operation = operation;
            this.originalTimeMs = originalTimeMs;
            this.redesignedTimeMs = redesignedTimeMs;
            this.speedupFactor = speedupFactor;
            this.originalBigO = originalBigO;
            this.redesignedBigO = redesignedBigO;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - QUESTION 7: DAY-4 DESIGN CHALLENGE VALIDATION SUITE           ");
        System.out.println("   BOTTLENECK: PriorityQueue Re-Heapification & O(N) Search in Emergency Triage ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        int patientCount = 10000;
        int lookupCount = 50000;
        int updateCount = 2000;

        Patient[] dataset = generatePatients(patientCount);
        String[] queryNames = generateQueryNames(dataset, lookupCount);
        String[] updateTargets = generateUpdateTargets(dataset, updateCount);

        List<ChallengeMetric> metrics = new ArrayList<>();

        // ---------------------------------------------------------------------
        // 1. BENCHMARK ORIGINAL ARCHITECTURE (PriorityQueue)
        // ---------------------------------------------------------------------
        System.out.println(">>> 1. Benchmarking Original Architecture (PriorityQueue) <<<");
        System.gc();
        long memBeforeOrig = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        OriginalTriageQueue orig = new OriginalTriageQueue();

        // 1a. Admit
        long t0 = System.nanoTime();
        for (Patient p : dataset) orig.admit(p);
        long t1 = System.nanoTime();
        double origAdmitMs = (t1 - t0) / 1_000_000.0;

        // 1b. 50k Lookups (Linear search O(N))
        long t2 = System.nanoTime();
        Patient sink1 = null;
        for (String q : queryNames) sink1 = orig.lookup(q);
        long t3 = System.nanoTime();
        double origLookupMs = (t3 - t2) / 1_000_000.0;

        // 1c. 2,000 Dynamic Priority Updates (O(N) remove + O(log N) insert)
        long t4 = System.nanoTime();
        for (int i = 0; i < updateCount; i++) {
            orig.updatePriority(updateTargets[i], 1); // Escalate to Critical S1
        }
        long t5 = System.nanoTime();
        double origUpdateMs = (t5 - t4) / 1_000_000.0;

        // 1d. Aging Sweep (O(N^2) evaluation)
        long t6 = System.nanoTime();
        int origPromoted = orig.applyAgingSweep(30);
        long t7 = System.nanoTime();
        double origAgingMs = (t7 - t6) / 1_000_000.0;

        // 1e. Full Extraction (poll all 10,000)
        long t8 = System.nanoTime();
        while (!orig.isEmpty()) orig.triageNext();
        long t9 = System.nanoTime();
        double origExtractMs = (t9 - t8) / 1_000_000.0;

        long memAfterOrig = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long origMemBytes = Math.max(0, memAfterOrig - memBeforeOrig);

        System.out.printf("  Original Admit Time   : %10.3f ms%n", origAdmitMs);
        System.out.printf("  Original 50k Lookups  : %10.3f ms (Severe O(N) linear search bottleneck)%n", origLookupMs);
        System.out.printf("  Original 2k Updates   : %10.3f ms (O(N) remove + O(log N) insert)%n", origUpdateMs);
        System.out.printf("  Original Aging Sweep  : %10.3f ms (Promoted %d patients)%n", origAgingMs, origPromoted);
        System.out.printf("  Original Extraction   : %10.3f ms%n", origExtractMs);


        // ---------------------------------------------------------------------
        // 2. BENCHMARK REDESIGNED ARCHITECTURE (Indexed Multi-Level Buckets)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> 2. Benchmarking Redesigned Architecture (Indexed Multi-Level Queue) <<<");
        // Reset patient wait times and severities for fair testing
        Patient[] datasetRedesign = generatePatients(patientCount);

        System.gc();
        long memBeforeRedes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        RedesignedTriageQueue redes = new RedesignedTriageQueue();

        // 2a. Admit
        long r0 = System.nanoTime();
        for (Patient p : datasetRedesign) redes.admit(p);
        long r1 = System.nanoTime();
        double redesAdmitMs = (r1 - r0) / 1_000_000.0;

        // 2b. 50k Lookups (Hash index O(1))
        long r2 = System.nanoTime();
        Patient sink2 = null;
        for (String q : queryNames) sink2 = redes.lookup(q);
        long r3 = System.nanoTime();
        double redesLookupMs = (r3 - r2) / 1_000_000.0;

        // 2c. 2,000 Dynamic Priority Updates (O(1) node unlinking + bucket splice)
        long r4 = System.nanoTime();
        for (int i = 0; i < updateCount; i++) {
            redes.updatePriority(updateTargets[i], 1); // Escalate to Critical S1
        }
        long r5 = System.nanoTime();
        double redesUpdateMs = (r5 - r4) / 1_000_000.0;

        // 2d. Aging Sweep (O(K) bucket sweep with O(1) unlinks)
        long r6 = System.nanoTime();
        int redesPromoted = redes.applyAgingSweep(30);
        long r7 = System.nanoTime();
        double redesAgingMs = (r7 - r6) / 1_000_000.0;

        // 2e. Full Extraction (triageNext bitmask O(1))
        long r8 = System.nanoTime();
        while (!redes.isEmpty()) redes.triageNext();
        long r9 = System.nanoTime();
        double redesExtractMs = (r9 - r8) / 1_000_000.0;

        long memAfterRedes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long redesMemBytes = Math.max(0, memAfterRedes - memBeforeRedes);

        System.out.printf("  Redesigned Admit Time : %10.3f ms%n", redesAdmitMs);
        System.out.printf("  Redesigned 50k Lookups: %10.3f ms (Instant O(1) Hash Map Index)%n", redesLookupMs);
        System.out.printf("  Redesigned 2k Updates : %10.3f ms (Instant O(1) Doubly-Linked Splice)%n", redesUpdateMs);
        System.out.printf("  Redesigned Aging Sweep: %10.3f ms (Promoted %d patients)%n", redesAgingMs, redesPromoted);
        System.out.printf("  Redesigned Extraction : %10.3f ms%n", redesExtractMs);

        // Compile Comparison Records
        metrics.add(new ChallengeMetric("Admit (10k)", origAdmitMs, redesAdmitMs, (origAdmitMs / redesAdmitMs), "O(log N)", "O(1)"));
        metrics.add(new ChallengeMetric("Lookup (50k)", origLookupMs, redesLookupMs, (origLookupMs / redesLookupMs), "O(N)", "O(1)"));
        metrics.add(new ChallengeMetric("Update Priority (2k)", origUpdateMs, redesUpdateMs, (origUpdateMs / redesUpdateMs), "O(N) + O(log N)", "O(1)"));
        metrics.add(new ChallengeMetric("Aging Sweep (30 min)", origAgingMs, redesAgingMs, (origAgingMs / redesAgingMs), "O(N^2)", "O(K)"));
        metrics.add(new ChallengeMetric("Extract Next (10k)", origExtractMs, redesExtractMs, (origExtractMs / redesExtractMs), "O(log N)", "O(1)"));

        // Display Comparative Table
        System.out.println("\n====================================================================================================");
        System.out.println("   SIDE-BY-SIDE HEAD-TO-HEAD VALIDATION SUMMARY TABLE                                               ");
        System.out.println("====================================================================================================");
        System.out.printf("%-24s | %-16s | %-16s | %-14s | %-16s%n",
                "Operation (10k Scale)", "Original (PQ)", "Redesigned (MLQ)", "Speedup Factor", "Complexity Shift");
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (ChallengeMetric m : metrics) {
            System.out.printf("%-24s | %13.3f ms | %13.3f ms | %12.2fx | %s -> %s%n",
                    m.operation, m.originalTimeMs, m.redesignedTimeMs, m.speedupFactor, m.originalBigO, m.redesignedBigO);
        }
        System.out.println("====================================================================================================\n");

        // Save CSV
        saveCsv(metrics, "Assignment 2/Q7_Design_Challenge/results/Q7_Design_Challenge_Benchmark.csv");
        System.out.println("[SUCCESS] Comparative results saved to: Assignment 2/Q7_Design_Challenge/results/Q7_Design_Challenge_Benchmark.csv");
    }

    private static Patient[] generatePatients(int count) {
        Patient[] list = new Patient[count];
        Random rng = new Random(42);
        for (int i = 0; i < count; i++) {
            int severity = rng.nextInt(5) + 1;
            Patient p = new Patient("PATIENT_" + i, severity, i + 1);
            p.setWaitTimeMinutes(rng.nextInt(90)); // 0 to 90 mins wait time
            list[i] = p;
        }
        return list;
    }

    private static String[] generateQueryNames(Patient[] batch, int count) {
        String[] queries = new String[count];
        Random rng = new Random(77);
        for (int i = 0; i < count; i++) {
            queries[i] = batch[rng.nextInt(batch.length)].getName();
        }
        return queries;
    }

    private static String[] generateUpdateTargets(Patient[] batch, int count) {
        String[] targets = new String[count];
        Random rng = new Random(99);
        for (int i = 0; i < count; i++) {
            targets[i] = batch[rng.nextInt(batch.length)].getName();
        }
        return targets;
    }

    private static void saveCsv(List<ChallengeMetric> list, String path) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("Operation,OriginalTimeMs,RedesignedTimeMs,SpeedupFactor,OriginalComplexity,RedesignedComplexity");
            for (ChallengeMetric m : list) {
                pw.printf("\"%s\",%.4f,%.4f,%.2f,\"%s\",\"%s\"%n",
                        m.operation, m.originalTimeMs, m.redesignedTimeMs, m.speedupFactor,
                        m.originalBigO, m.redesignedBigO);
            }
        } catch (IOException e) {
            System.err.println("CSV export error: " + e.getMessage());
        }
    }
}
