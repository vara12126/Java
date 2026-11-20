# Task B1 – Implementation and Instrument the File Store

**Course Title:** Programming in Java (CSA09)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Bloom’s Level:** K4 – Analyse  
**Total Marks:** 7 Marks  

---

## 1. Implementation Overview

In Task B1, the bus-pass file store designed in Task A1 was implemented as `PassStore.java`, and the buffered I/O instrumentation suite was implemented as `LineCounter.java` and `TaskB1Benchmark.java`.

### Completed Code: `PassStore.java` (TODO 1)
```java
public String[] find(String passId) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(file)) {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length != 5) {
                // Skip damaged lines (not exactly 5 fields)
                continue;
            }
            if (parts[0].trim().equals(passId)) {
                return parts;
            }
        }
    }
    return null;
}
```

### Verification Against Expected Output
Running `PassStore.main()` produces the exact expected output required by the PDF rubric:
```text
[P1003, 192311003, Divya, 7, 3800.0]
null
```

---

## B1.1 – Validate the Implementation (2 Marks)

### Test Suite Execution Table

| Test ID | Test Condition | Input Data / Query | Expected Output | Actual Observed Output | Status | Engineering Verification |
|:---:|:---|:---|:---|:---|:---:|:---|
| **TC-B1.1** | Normal Insertion & Lookup | Insert `P1001`, `P1003`, `P1004`, `P1005` | Record fields array | `[P1003, 192311003, Divya, 7, 3800.0]` | **PASS** | Validates proper comma-delimited record formatting and sequential scan retrieval. |
| **TC-B1.2** | Adversarial Damaged Line | Injected `"P1002,DAMAGED LINE"` | `null` (Line skipped safely) | `null` | **PASS** | `parts.length == 2 != 5` detected; loop skips line without throwing `ArrayIndexOutOfBoundsException`. |
| **TC-B1.3** | Non-Existent Record | Query `"P9999"` | `null` (Scan reaches EOF) | `null` | **PASS** | Traverses entire file safely and returns `null` at EOF. |
| **TC-B1.4** | Route-Based Listing | List Route 12 | All Route 12 passes | Found 3 passes (`P1001`, `P1004`, `P1005`) | **PASS** | Verifies filtering logic while preserving record data integrity. |

### Why Damaged Line for P1002 Does Not Stop the Search
When `find("P1003")` scans the file, it encounters the damaged line `P1002,DAMAGED LINE`.
1. The line is split by comma into `parts = ["P1002", "DAMAGED LINE"]`.
2. The integrity guard `if (parts.length != 5) continue;` detects that the token count is 2 rather than 5.
3. The method executes `continue`, discarding the corrupted line and immediately proceeding to the next iteration.
4. The file pointer advances to the next line (`P1003`), finds the matching ID, and returns the valid record.
5. Consequently, corrupted lines never halt search execution or cause unhandled exceptions.

---

## B1.2 – Compare Implementation with Part A Pseudocode (1 Mark)

| Aspect | Part A Pseudocode (`FindBusPassById`) | Java Implementation (`PassStore.java`) | Rationale for Adaptation |
|:---|:---|:---|:---|
| **Resource Management** | Explicit `OPEN_BUFFERED_READER()` and `reader.CLOSE()` | Java 7+ `try-with-resources` (`try (BufferedReader reader = ...)`) | Guarantees automatic file handle closure even if runtime exceptions occur. |
| **File I/O Abstraction** | Generic `FILE_EXISTS()` and stream instantiations | Modern Java NIO.2 (`java.nio.file.Path`, `Files.newBufferedReader(file)`) | Better exception handling, cross-platform path resolution, and buffer optimization. |
| **Record Structure** | Modeled as full 7-field academic bus pass | 5-field structure required by the PDF assignment specification | Conforms exactly to the PDF specification signature `[pass_id, reg_no, name, route, amount]`. |

---

## B1.3 – Measure the Effect of Buffering (2 Marks)

Line counting was benchmarked on files of **1 MB**, **10 MB**, and **50 MB** using:
1. **Unbuffered:** `FileInputStream.read()` reading 1 byte per call.
2. **Buffered Stream:** `BufferedInputStream` with an 8 KB user-space buffer.
3. **Buffered Reader:** `BufferedReader.readLine()`.

### Empirical Buffering Measurements Table

| File Size | File Size (Bytes) | Total Read Calls | Unbuffered Time (ms) | Buffered Stream Time (ms) | BufferedReader Time (ms) | Measured Speedup Factor |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **1 MB** | $1,048,645$ | $1,048,646$ | **4,507 ms** | **22 ms** | 34 ms | **204.86x** |
| **10 MB**| $10,485,793$ | $10,485,794$ | **29,946 ms** | **132 ms** | 60 ms | **226.86x** |
| **50 MB**| $52,428,819$ | $52,428,820$ | **151,731 ms** ($\approx 2.5\text{ min}$) | **630 ms** | **127 ms** | **240.84x** |

### Comparison with A2.1 Theoretical Prediction
- **Theoretical Prediction in A2.1:** A 50 MB file requires $52,428,800$ read operations. Reading byte-by-byte forces 52.4 million JNI native calls and operating system privilege mode switches, leading to extreme execution times.
- **Empirical Reality:** The unbuffered 50 MB execution took **151.73 seconds (151,731 ms)**, whereas `BufferedInputStream` completed the identical task in **630 ms**, and `BufferedReader` in **127 ms**.
- **Conclusion:** Buffering achieved a **240.84x performance acceleration**, verifying our prediction that eliminating 99.988% of kernel context switches transforms an unusable system into a high-throughput pipeline.

---

## B1.4 – Measure Search Time Growth (2 Marks)

The search performance of sequential linear file scanning was benchmarked on generated pass stores containing **1,000**, **10,000**, and **100,000 records**.

### Empirical Growth Measurements Table

| Record Count | File Size (Bytes) | Scan Head (ms) | Scan Mid (ms) | Scan Tail (ms) | Scan Not Found (ms) | In-Memory HashMap Load (ms) | HashMap Lookup (ns) |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **1,000** | $43,533$ ($\approx 43.5\text{ KB}$) | 6.699 ms | 2.162 ms | 2.236 ms | 2.086 ms | 3.22 ms | 29,900 ns ($0.03\text{ ms}$) |
| **10,000** | $445,294$ ($\approx 445\text{ KB}$) | 12.719 ms | 7.632 ms | 13.753 ms | 11.310 ms | 16.02 ms | 5,800 ns ($0.0058\text{ ms}$) |
| **100,000** | $4,552,895$ ($\approx 4.55\text{ MB}$) | 49.696 ms | 22.655 ms | 44.342 ms | 32.435 ms | 59.95 ms | 8,700 ns ($0.0087\text{ ms}$) |

### Trend Explanation
1. **Linear Degradation ($O(n)$):** As record count increases from 1,000 to 100,000 ($100\times$ increase), worst-case linear scanning latency scales directly from **2.08 ms** to **32.43 ms** (and up to 44.34 ms for tail searches).
2. **I/O Bound Bottleneck:** Every linear search forces the disk controller and JVM to parse megabytes of text and allocate hundreds of thousands of string tokens, even when looking for a single record.

---

## Alternative Concept and Complexity Analysis: In-Memory HashMap Indexing

### Concept Explanation
Instead of scanning the disk on every query, the file is parsed **once at application startup** into an in-memory hash table:
```java
Map<String, String[]> passIndex = new HashMap<>();
// Populate once during bootstrap
```

### Complexity Comparison

| Dimension | Repeated Sequential File Scan | In-Memory `HashMap<String, String[]>` Index |
|:---|:---|:---|
| **Query Complexity for $q$ Searches** | **$O(q \times n)$** | **$O(n + q)$** ($O(n)$ one-time load + $O(1)$ per search) |
| **Search Latency (100k records)** | $32.4\text{ ms} - 49.7\text{ ms}$ | **$8,700\text{ ns} \approx 0.0087\text{ ms}$ ($3,700\times$ faster)** |
| **Auxiliary RAM Footprint** | **$O(1)$** ($8\text{ KB}$ buffer) | **$O(n)$** ($\approx 25\text{ MB} - 40\text{ MB}$ for 100,000 records) |
| **Data Freshness / Consistency** | Immediate (reads disk state directly) | Requires manual index invalidation/sync on new `add()` |

### Engineering Decision
When query frequency $q$ is large ($q \gg 1$), loading into `HashMap` is overwhelmingly superior, dropping search latency from 32 ms to sub-microsecond levels. However, if memory is constrained or data must be shared across processes, an external indexed database is required.
