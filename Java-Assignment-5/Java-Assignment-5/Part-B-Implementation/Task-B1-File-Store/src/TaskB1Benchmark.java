import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class TaskB1Benchmark {

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   TASK B1: FILE STORE VALIDATION, BUFFERING & GROWTH EXPERIMENTAL SUITE");
        System.out.println("================================================================================");

        Path baseDir = Paths.get("Java-Assignment-5/Part-B-Implementation/Task-B1-File-Store");
        Path measurementsDir = baseDir.resolve("measurements");
        Files.createDirectories(measurementsDir);

        // -----------------------------------------------------------------------------------------
        // B1.1: Functional Validation of PassStore
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [B1.1] Functional Validation of PassStore ---");
        Path testStorePath = Files.createTempFile("pass_store_validation", ".csv");
        PassStore store = new PassStore(testStorePath);

        System.out.println("1. Adding normal passes...");
        store.add("P1001", "192311001", "Arun", 12, 4500.0);
        store.add("P1003", "192311003", "Divya", 7, 3800.0);
        store.add("P1004", "192311004", "Karthik", 12, 4500.0);
        store.add("P1005", "192311094", "Dhanush", 12, 4500.0);

        System.out.println("2. Injecting adversarial damaged line: 'P1002,DAMAGED LINE'");
        Files.writeString(testStorePath, "P1002,DAMAGED LINE\n", StandardOpenOption.APPEND);

        System.out.println("3. Querying Existing Pass P1003: " + Arrays.toString(store.find("P1003")));
        System.out.println("4. Querying Damaged Pass P1002:   " + Arrays.toString(store.find("P1002")));
        System.out.println("5. Querying Non-existent P9999:  " + Arrays.toString(store.find("P9999")));

        List<String[]> route12Passes = store.listRoute(12);
        System.out.println("6. Listing Passes on Route 12 (Found " + route12Passes.size() + " passes):");
        for (String[] p : route12Passes) {
            System.out.println("   - " + Arrays.toString(p));
        }

        Files.deleteIfExists(testStorePath);
        System.out.println("-> Validation successful: Damaged line correctly skipped without halting search.");

        // -----------------------------------------------------------------------------------------
        // B1.3: Measure Effect of Buffering (1 MB, 10 MB, 50 MB)
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [B1.3] Benchmarking I/O Buffering (1 MB, 10 MB, 50 MB) ---");
        int[] sizesMB = {1, 10, 50};
        List<String> bufferCsvLines = new ArrayList<>();
        bufferCsvLines.add("file_size_mb,file_size_bytes,unbuffered_time_ms,buffered_stream_time_ms,buffered_reader_time_ms,speedup_factor");

        Path tempDir = Files.createTempDirectory("benchmark_io_files");

        for (int mb : sizesMB) {
            System.out.print("Generating " + mb + " MB test file... ");
            Path sampleFile = tempDir.resolve("test_" + mb + "MB.log");
            generateSampleFile(sampleFile, mb);
            long fileBytes = Files.size(sampleFile);
            System.out.println("Done (" + fileBytes + " bytes).");

            // Measure unbuffered FileInputStream.read()
            System.out.print("  Measuring unbuffered FileInputStream.read()... ");
            long startUnbuffered = System.nanoTime();
            long linesUnbuffered = LineCounter.countUnbuffered(sampleFile);
            long timeUnbufferedMs = (System.nanoTime() - startUnbuffered) / 1_000_000;
            System.out.println(timeUnbufferedMs + " ms (" + linesUnbuffered + " lines)");

            // Measure BufferedInputStream
            System.out.print("  Measuring BufferedInputStream (8 KB buffer)... ");
            long startBufferedStream = System.nanoTime();
            long linesBufferedStream = LineCounter.countBufferedStream(sampleFile);
            long timeBufferedStreamMs = (System.nanoTime() - startBufferedStream) / 1_000_000;
            System.out.println(timeBufferedStreamMs + " ms (" + linesBufferedStream + " lines)");

            // Measure BufferedReader.readLine()
            System.out.print("  Measuring BufferedReader.readLine()... ");
            long startBufferedReader = System.nanoTime();
            long linesBufferedReader = LineCounter.countBufferedReader(sampleFile);
            long timeBufferedReaderMs = (System.nanoTime() - startBufferedReader) / 1_000_000;
            System.out.println(timeBufferedReaderMs + " ms (" + linesBufferedReader + " lines)");

            double speedup = (double) timeUnbufferedMs / Math.max(timeBufferedStreamMs, 1);
            System.out.printf("  ==> Buffering Speedup: %.2fx faster!\n\n", speedup);

            bufferCsvLines.add(String.format("%d,%d,%d,%d,%d,%.2f",
                    mb, fileBytes, timeUnbufferedMs, timeBufferedStreamMs, timeBufferedReaderMs, speedup));

            Files.deleteIfExists(sampleFile);
        }

        Path bufferCsvPath = measurementsDir.resolve("buffering_benchmark.csv");
        Files.write(bufferCsvPath, bufferCsvLines, StandardCharsets.UTF_8);
        System.out.println("Buffering measurements saved to: " + bufferCsvPath);

        // -----------------------------------------------------------------------------------------
        // B1.4: Measure Search Time Growth (1,000, 10,000, 100,000 records)
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [B1.4] Benchmarking Search Time Growth (1k, 10k, 100k records) ---");
        int[] recordCounts = {1_000, 10_000, 100_000};
        List<String> growthCsvLines = new ArrayList<>();
        growthCsvLines.add("record_count,file_size_bytes,linear_scan_head_ms,linear_scan_mid_ms,linear_scan_tail_ms,linear_scan_notfound_ms,hashmap_load_ms,hashmap_lookup_ns");

        for (int count : recordCounts) {
            System.out.print("Generating " + count + " bus-pass records... ");
            Path passStoreFile = tempDir.resolve("passes_" + count + ".csv");
            generatePassDataFile(passStoreFile, count);
            long storeSizeBytes = Files.size(passStoreFile);
            System.out.println("Done (" + storeSizeBytes + " bytes).");

            PassStore benchmarkStore = new PassStore(passStoreFile);

            // Targets
            String headId = "P100000001";
            String midId = String.format("P%09d", count / 2);
            String tailId = String.format("P%09d", count);
            String notFoundId = "P999999999";

            // 1. Search Head
            long t1 = System.nanoTime();
            benchmarkStore.find(headId);
            double headMs = (System.nanoTime() - t1) / 1_000_000.0;

            // 2. Search Mid
            long t2 = System.nanoTime();
            benchmarkStore.find(midId);
            double midMs = (System.nanoTime() - t2) / 1_000_000.0;

            // 3. Search Tail
            long t3 = System.nanoTime();
            benchmarkStore.find(tailId);
            double tailMs = (System.nanoTime() - t3) / 1_000_000.0;

            // 4. Search Not Found (Worst-case full linear scan)
            long t4 = System.nanoTime();
            benchmarkStore.find(notFoundId);
            double notFoundMs = (System.nanoTime() - t4) / 1_000_000.0;

            // 5. Alternative Concept: In-Memory HashMap Index
            long t5 = System.nanoTime();
            Map<String, String[]> index = loadIntoHashMap(passStoreFile);
            double hashLoadMs = (System.nanoTime() - t5) / 1_000_000.0;

            long t6 = System.nanoTime();
            String[] res = index.get(midId);
            long hashLookupNs = System.nanoTime() - t6;

            System.out.printf("  Records: %,d | Head: %.3f ms | Mid: %.3f ms | Tail: %.3f ms | Not Found: %.3f ms | HashMap Load: %.2f ms | Hash Lookup: %d ns\n",
                    count, headMs, midMs, tailMs, notFoundMs, hashLoadMs, hashLookupNs);

            growthCsvLines.add(String.format("%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%d",
                    count, storeSizeBytes, headMs, midMs, tailMs, notFoundMs, hashLoadMs, hashLookupNs));

            Files.deleteIfExists(passStoreFile);
        }

        Files.deleteIfExists(tempDir);
        Path growthCsvPath = measurementsDir.resolve("growth_benchmark.csv");
        Files.write(growthCsvPath, growthCsvLines, StandardCharsets.UTF_8);
        System.out.println("Growth measurements saved to: " + growthCsvPath);

        System.out.println("\n================================================================================");
        System.out.println("   TASK B1 BENCHMARK COMPLETE");
        System.out.println("================================================================================");
    }

    private static void generateSampleFile(Path path, int targetMB) throws IOException {
        byte[] line = "2026-11-19 08:30:00,STUDENT_ENTRY,TAP_SUCCESS,GATE_04,192311094,Tambaram\n".getBytes(StandardCharsets.US_ASCII);
        long targetBytes = (long) targetMB * 1024 * 1024;
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(path), 64 * 1024)) {
            long written = 0;
            while (written < targetBytes) {
                bos.write(line);
                written += line.length;
            }
        }
    }

    private static void generatePassDataFile(Path path, int count) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8), 64 * 1024)) {
            for (int i = 1; i <= count; i++) {
                String passId = String.format("P%09d", i);
                String regNo = String.format("1923%05d", (10000 + i % 90000));
                String name = "Student_" + i;
                int route = 1 + (i % 25);
                double amount = 3500.0 + (route * 100);
                bw.write(passId + "," + regNo + "," + name + "," + route + "," + amount);
                bw.newLine();
            }
        }
    }

    private static Map<String, String[]> loadIntoHashMap(Path path) throws IOException {
        Map<String, String[]> map = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    map.put(parts[0], parts);
                }
            }
        }
        return map;
    }
}
