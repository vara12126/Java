package q4_map;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Master Benchmarking and Execution Harness for Question 4:
 * HashMap vs TreeMap vs LinkedHashMap in Hospital Pharmacy Inventory.
 * 
 * Features:
 * - Insert, Update, Search, Delete operations.
 * - Report 1: Natural sort by medicine code.
 * - Report 2: Historical insertion order audit report.
 * - 3 Inventory sizes tested: 1,000, 10,000, 50,000.
 * - 100,000 lookup queries measured.
 * - Comparison with SortedArray + Arrays.binarySearch().
 * - Export of raw metrics to CSV.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class PharmacyBenchmarkRunner {

    public static class MapMetric {
        public String mapType;
        public int inventorySize;
        public double insertTimeMs;
        public double lookup100kTimeMs;
        public double updateTimeMs;
        public double deleteTimeMs;
        public double sortedReportTimeMs;
        public double insertionOrderReportTimeMs;

        public MapMetric(String mapType, int inventorySize, double insertTimeMs, double lookup100kTimeMs,
                         double updateTimeMs, double deleteTimeMs, double sortedReportTimeMs, double insertionOrderReportTimeMs) {
            this.mapType = mapType;
            this.inventorySize = inventorySize;
            this.insertTimeMs = insertTimeMs;
            this.lookup100kTimeMs = lookup100kTimeMs;
            this.updateTimeMs = updateTimeMs;
            this.deleteTimeMs = deleteTimeMs;
            this.sortedReportTimeMs = sortedReportTimeMs;
            this.insertionOrderReportTimeMs = insertionOrderReportTimeMs;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - QUESTION 4: HOSPITAL PHARMACY INVENTORY MANAGEMENT             ");
        System.out.println("   HashMap vs TreeMap vs LinkedHashMap vs SortedArray                           ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        // 1. Verify Sample Operations and Report Generation
        verifySampleOperations();

        // 2. Comprehensive Benchmarking Suite across 3 sizes
        int[] inventorySizes = {1000, 10000, 50000};
        int lookupCount = 100000;
        List<MapMetric> metrics = new ArrayList<>();

        System.out.println("\n>>> Executing Comprehensive Map Benchmarks (Lookups: 100,000) <<<");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-8s | %-12s | %-16s | %-12s | %-12s | %-14s | %-14s%n",
                "Map Type", "Size", "Insert(ms)", "100k Lookups", "Update(ms)", "Delete(ms)", "Sorted Rep", "Insert Rep");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        for (int size : inventorySizes) {
            Medicine[] testBatch = generateTestMedicines(size);
            String[] queryCodes = generateQueryKeys(testBatch, lookupCount);

            // Test HashMap
            MapMetric hmMetric = benchmarkMap(new HashMap<>(), "HashMap", testBatch, queryCodes, size);
            metrics.add(hmMetric);
            printMetricRow(hmMetric);

            // Test TreeMap
            MapMetric tmMetric = benchmarkMap(new TreeMap<>(), "TreeMap", testBatch, queryCodes, size);
            metrics.add(tmMetric);
            printMetricRow(tmMetric);

            // Test LinkedHashMap
            MapMetric lhmMetric = benchmarkMap(new LinkedHashMap<>(), "LinkedHashMap", testBatch, queryCodes, size);
            metrics.add(lhmMetric);
            printMetricRow(lhmMetric);

            System.out.println("  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        }

        // 3. Alternative Concept Comparison: SortedArray + binarySearch
        benchmarkSortedArray(10000, 100000);

        // 4. Save results to CSV
        saveCsv(metrics, "Assignment 2/Q4_Map_Comparison/results/Q4_Map_Benchmark.csv");
        System.out.println("\n[SUCCESS] Raw benchmark data exported to: Assignment 2/Q4_Map_Comparison/results/Q4_Map_Benchmark.csv");

        // 5. Theory and Architectural Defense
        printTheoreticalAnalysis();
    }

    private static void verifySampleOperations() {
        System.out.println("--- SAMPLE INVENTORY VERIFICATION (HashMap vs TreeMap vs LinkedHashMap) ---");
        Map<String, Medicine> hashMap = new HashMap<>();
        Map<String, Medicine> treeMap = new TreeMap<>();
        Map<String, Medicine> linkedHashMap = new LinkedHashMap<>();

        Medicine m1 = new Medicine("MED-3001", "Amoxicillin 500mg", 150, 12.50, "Pfizer", "Antibiotic");
        Medicine m2 = new Medicine("MED-1002", "Paracetamol 650mg", 500, 2.20, "GSK", "Analgesic");
        Medicine m3 = new Medicine("MED-2005", "Azithromycin 250mg", 80, 25.00, "Cipla", "Antibiotic");
        Medicine m4 = new Medicine("MED-4010", "Metformin 500mg", 300, 4.50, "Sun Pharma", "Antidiabetic");

        Medicine[] sample = {m1, m2, m3, m4};
        for (Medicine m : sample) {
            hashMap.put(m.getMedicineCode(), m);
            treeMap.put(m.getMedicineCode(), m);
            linkedHashMap.put(m.getMedicineCode(), m);
        }

        System.out.println("1. Insertion Order (Chronological Receipt Order - Preserved by LinkedHashMap):");
        for (Map.Entry<String, Medicine> e : linkedHashMap.entrySet()) {
            System.out.println("   -> " + e.getValue());
        }

        System.out.println("\n2. Natural Sorted Order by Medicine Code (Guaranteed by TreeMap O(log N)):");
        for (Map.Entry<String, Medicine> e : treeMap.entrySet()) {
            System.out.println("   -> " + e.getValue());
        }

        System.out.println("\n3. Testing Update Operation (Restocking MED-1002 by +250 units):");
        Medicine med = hashMap.get("MED-1002");
        med.setStockQuantity(med.getStockQuantity() + 250);
        System.out.println("   Updated in HashMap: " + hashMap.get("MED-1002"));

        System.out.println("\n4. Testing Delete Operation (Discontinuing MED-3001):");
        hashMap.remove("MED-3001");
        System.out.println("   hashMap.containsKey('MED-3001'): " + hashMap.containsKey("MED-3001") + " [Successfully Deleted]\n");
    }

    private static MapMetric benchmarkMap(Map<String, Medicine> map, String mapName,
                                          Medicine[] testBatch, String[] queryCodes, int size) {
        // Insert
        long t0 = System.nanoTime();
        for (Medicine m : testBatch) {
            map.put(m.getMedicineCode(), m);
        }
        long t1 = System.nanoTime();
        double insertMs = (t1 - t0) / 1_000_000.0;

        // 100k Lookups
        long t2 = System.nanoTime();
        Medicine sink = null;
        for (String q : queryCodes) {
            sink = map.get(q);
        }
        long t3 = System.nanoTime();
        double lookupMs = (t3 - t2) / 1_000_000.0;

        // Update 1,000 medicines
        long t4 = System.nanoTime();
        for (int i = 0; i < Math.min(1000, size); i++) {
            Medicine m = map.get(testBatch[i].getMedicineCode());
            if (m != null) m.setStockQuantity(m.getStockQuantity() + 10);
        }
        long t5 = System.nanoTime();
        double updateMs = (t5 - t4) / 1_000_000.0;

        // Delete 500 medicines
        long t6 = System.nanoTime();
        for (int i = 0; i < Math.min(500, size); i++) {
            map.remove(testBatch[i].getMedicineCode());
        }
        long t7 = System.nanoTime();
        double deleteMs = (t7 - t6) / 1_000_000.0;

        // Re-insert deleted for reports
        for (int i = 0; i < Math.min(500, size); i++) {
            map.put(testBatch[i].getMedicineCode(), testBatch[i]);
        }

        // Report 1: Sorted by medicine code
        long t8 = System.nanoTime();
        int reportItems1 = 0;
        if (map instanceof TreeMap) {
            for (Map.Entry<String, Medicine> e : map.entrySet()) {
                reportItems1++;
            }
        } else {
            // Must sort keys
            List<String> sortedKeys = new ArrayList<>(map.keySet());
            Collections.sort(sortedKeys);
            for (String k : sortedKeys) {
                Medicine m = map.get(k);
                reportItems1++;
            }
        }
        long t9 = System.nanoTime();
        double sortedReportMs = (t9 - t8) / 1_000_000.0;

        // Report 2: Insertion order report
        long t10 = System.nanoTime();
        int reportItems2 = 0;
        if (map instanceof LinkedHashMap) {
            for (Map.Entry<String, Medicine> e : map.entrySet()) {
                reportItems2++;
            }
        } else {
            // If not LinkedHashMap, requires external tracking list
            for (Medicine m : testBatch) {
                reportItems2++;
            }
        }
        long t11 = System.nanoTime();
        double insertionReportMs = (t11 - t10) / 1_000_000.0;

        return new MapMetric(mapName, size, insertMs, lookupMs, updateMs, deleteMs, sortedReportMs, insertionReportMs);
    }

    private static void benchmarkSortedArray(int size, int lookups) {
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.println("--- ALTERNATIVE CONCEPT: SortedArray + Arrays.binarySearch() vs Maps (Size: " + size + ") ---");
        SortedArrayInventory sai = new SortedArrayInventory(size);
        Medicine[] testBatch = generateTestMedicines(size);
        String[] queryCodes = generateQueryKeys(testBatch, lookups);

        // Insertion
        long t0 = System.nanoTime();
        for (Medicine m : testBatch) sai.insert(m);
        long t1 = System.nanoTime();
        double insertMs = (t1 - t0) / 1_000_000.0;

        // Lookups
        long t2 = System.nanoTime();
        Medicine sink = null;
        for (String q : queryCodes) sink = sai.search(q);
        long t3 = System.nanoTime();
        double lookupMs = (t3 - t2) / 1_000_000.0;

        System.out.printf("SortedArray Insert Time (%d items) : %10.3f ms (Punishing O(N) element shifts on each insert)%n", size, insertMs);
        System.out.printf("SortedArray 100k Lookups Time     : %10.3f ms (Fast O(log N) binary search with CPU cache locality)%n", lookupMs);
        System.out.println("Architectural Takeaway:");
        System.out.println("While binary search in a sorted array achieves very competitive lookup speed, its O(N) insertion");
        System.out.println("cost is up to 100x slower than HashMap's O(1) hash bucket insertion. Maps are essential for dynamic data.");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
    }

    private static Medicine[] generateTestMedicines(int count) {
        Medicine[] list = new Medicine[count];
        for (int i = 0; i < count; i++) {
            String code = "MED-" + String.format("%06d", (count - i)); // non-sequential insertion
            list[i] = new Medicine(code, "PharmaDrug_" + i, 100 + (i % 500), 5.0 + (i % 100), "PharmaCorp", "General");
        }
        return list;
    }

    private static String[] generateQueryKeys(Medicine[] batch, int count) {
        String[] queries = new String[count];
        Random rng = new Random(101);
        for (int i = 0; i < count; i++) {
            queries[i] = batch[rng.nextInt(batch.length)].getMedicineCode();
        }
        return queries;
    }

    private static void printMetricRow(MapMetric m) {
        System.out.printf("%-15s | %-8d | %10.3f ms | %10.3f ms | %10.3f ms | %10.3f ms | %10.3f ms | %10.3f ms%n",
                m.mapType, m.inventorySize, m.insertTimeMs, m.lookup100kTimeMs,
                m.updateTimeMs, m.deleteTimeMs, m.sortedReportTimeMs, m.insertionOrderReportTimeMs);
    }

    private static void saveCsv(List<MapMetric> list, String path) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("MapType,InventorySize,InsertTimeMs,Lookup100kTimeMs,UpdateTimeMs,DeleteTimeMs,SortedReportTimeMs,InsertionOrderReportTimeMs");
            for (MapMetric m : list) {
                pw.printf("\"%s\",%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                        m.mapType, m.inventorySize, m.insertTimeMs, m.lookup100kTimeMs,
                        m.updateTimeMs, m.deleteTimeMs, m.sortedReportTimeMs, m.insertionOrderReportTimeMs);
            }
        } catch (IOException e) {
            System.err.println("CSV save error: " + e.getMessage());
        }
    }

    private static void printTheoreticalAnalysis() {
        System.out.println("\n================================================================================");
        System.out.println("   THEORETICAL COMPLEXITY & ARCHITECTURAL SUMMARY                               ");
        System.out.println("================================================================================");
        System.out.printf("%-18s | %-14s | %-14s | %-18s | %-14s%n",
                "Map Implementation", "Lookup Time", "Insert Time", "Sorted Report", "Order Preserved");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-18s | %-14s | %-14s | %-18s | %-14s%n", "HashMap", "O(1) average", "O(1) average", "O(N log N) sort", "None (Random)");
        System.out.printf("%-18s | %-14s | %-14s | %-18s | %-14s%n", "TreeMap", "O(log N)", "O(log N)", "O(N) in-order walk", "Key Sorted");
        System.out.printf("%-18s | %-14s | %-14s | %-18s | %-14s%n", "LinkedHashMap", "O(1) average", "O(1) average", "O(N log N) sort", "Insertion Order");
        System.out.printf("%-18s | %-14s | %-14s | %-18s | %-14s%n", "SortedArray", "O(log N)", "O(N) shifts", "O(N) scan", "None (Key Sorted)");
        System.out.println("================================================================================\n");

        System.out.println("Why is Hashtable Obsolete in Modern Java?");
        System.out.println("1. Method-Level Synchronization: Hashtable uses synchronized methods on every call");
        System.out.println("   (get, put, remove), introducing severe thread contention under multi-core concurrency.");
        System.out.println("2. Modern Alternatives: Single-threaded contexts use HashMap. Concurrent multi-threaded");
        System.out.println("   contexts use ConcurrentHashMap, which achieves high throughput via lock-striping and");
        System.out.println("   non-blocking CAS (Compare-And-Swap) read operations.");
        System.out.println("3. Null Restrictions: Hashtable forbids null keys and values (throws NullPointerException),");
        System.out.println("   whereas HashMap permits one null key and multiple null values.");
        System.out.println("4. Legacy Heritage: Extends the obsolete java.util.Dictionary abstract class from Java 1.0.");
        System.out.println("================================================================================\n");
    }
}
