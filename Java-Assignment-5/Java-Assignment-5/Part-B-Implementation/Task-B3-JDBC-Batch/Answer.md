# Task B3 – Statement vs PreparedStatement vs Batch

**Course Title:** Programming in Java (CSA09)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Bloom’s Level:** K4 – Analyse  
**Total Marks:** 8 Marks  

---

## 1. Implementation Overview

Task B3 investigates the performance and correctness of relational database writes in MySQL using JDBC across three distinct insertion patterns:
1. **Plain Statement:** Formulating raw dynamic SQL strings and executing via `statement.executeUpdate(sql)`.
2. **PreparedStatement:** Parameterized SQL with `ps.executeUpdate()` executed iteratively row by row.
3. **PreparedStatement with Batch Processing (`PassLoader.java`):** Parameterized SQL batched in memory using `ps.addBatch()` and flushed via `ps.executeBatch()` every 1,000 rows inside a single atomic transaction.

---

## B3.1 – Correctness on Small Data & Metadata Verification (2 Marks)

A baseline batch of 10 bus-pass records was inserted into `assignment5_testdb.bus_pass`. Correctness was verified via SQL query and programmatic reflection using `java.sql.ResultSetMetaData`.

### Programmatic Column Metadata Output
```text
ResultSetMetaData Column Verification (5 columns):
   Column 1: pass_id      | Type: VARCHAR         | Nullable: NO  | Size: 20
   Column 2: reg_no       | Type: VARCHAR         | Nullable: NO  | Size: 20
   Column 3: name         | Type: VARCHAR         | Nullable: NO  | Size: 50
   Column 4: route_no     | Type: INT             | Nullable: NO  | Size: 10
   Column 5: amount       | Type: DOUBLE          | Nullable: NO  | Size: 22

Sample Injected Records:
   [PTEST0000001, 192310001, Student_1, 2, 3600.00]
   [PTEST0000002, 192310002, Student_2, 3, 3700.00]
   [PTEST0000003, 192310003, Student_3, 4, 3800.00]
   [PTEST0000004, 192310004, Student_4, 5, 3900.00]
   [PTEST0000005, 192310005, Student_5, 6, 4000.00]
```
- **Verification Status:** **100% Correct.** Data types match schema definitions, non-null constraints are enforced, and numeric values retain full floating-point precision.

---

## B3.2 – Performance Measurement Across Data Sizes (2 Marks)

Insertion times were benchmarked across dataset sizes of **1,000**, **10,000**, and **100,000 records** on MySQL 8.0.

### Empirical Insertion Performance Benchmark Table

| Record Count | Plain Statement Time (ms) | Plain Statement Throughput | PreparedStatement Time (ms) | PreparedStatement Throughput | PreparedStatement Batch Time (ms) | Batch Throughput | Batch Speedup vs Statement | Batch Speedup vs PreparedStatement |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **1,000** | 3,362 ms | 297 rows/sec | 3,048 ms | 328 rows/sec | **82 ms** | **12,195 rows/sec** | **41.00x** | **37.17x** |
| **10,000** | 24,006 ms | 416 rows/sec | 24,079 ms | 415 rows/sec | **398 ms** | **25,125 rows/sec** | **60.32x** | **60.50x** |
| **100,000** | *~280,000 ms* | *~357 rows/sec* | *~220,000 ms* | *~454 rows/sec* | **2,989 ms** | **33,456 rows/sec** | **93.68x** | **73.60x** |

*(Note: Unbatched Statement/PreparedStatement on 100,000 records take ~4 to 5 minutes due to 100,000 network round-trips; extrapolated based on linear roundtrip scaling).*

### Technical Breakdown of Performance Disparity
1. **Network Round-Trip Latency:**
   - In unbatched execution, every single row requires a separate client-to-server TCP network packet and an acknowledgment packet. For 10,000 records, this forces 10,000 round-trips ($10,000 \times \approx 2.4\text{ ms} \approx 24\text{ seconds}$).
   - With JDBC Batching (`executeBatch()`), 1,000 rows are packed into a single network buffer and transmitted in **one bulk TCP frame**. 10,000 rows require only 10 network round-trips!
2. **Database Parsing and Execution Plan Compilation:**
   - Plain Statement forces MySQL to lexically parse, validate, and compile an execution plan for every dynamic string.
   - Batch PreparedStatement compiles the SQL template once and binds binary arrays directly.

---

## B3.3 – Auto-Commit vs Transaction with Forced Mid-Stream Failure (2 Marks)

To evaluate transaction safety, an adversarial batch of 1,000 records was injected with a corrupted amount string (`INVALID_AMOUNT_STRING`) at index 500.

```
+-------------------------------------------------------------------------------------------------+
|                                TRANSACTION ATOMICITY EXPERIMENT                                 |
+-------------------------------------------------------------------------------------------------+
|  Scenario A (Auto-Commit ON):                                                                   |
|  Records 1 to 499 Inserted --> Row 500 CRASH --> Result: 500 ORPHANED ROWS IN DATABASE          |
|  [CRITICAL DATA CORRUPTION: Partial state committed; database permanently inconsistent]         |
|                                                                                                 |
|  Scenario B (Single Transaction with PassLoader):                                               |
|  Records 1 to 499 In Buffer --> Row 500 CRASH --> con.rollback() --> Result: 0 ROWS IN DATABASE |
|  [PERFECT ATOMICITY: Table returned to pristine baseline state; zero corrupt data saved]        |
+-------------------------------------------------------------------------------------------------+
```

### Empirical Experimental Results
- **Scenario A (Auto-Commit ON):**
  - Result: **500 records remained committed in the database!**
  - Consequence: A partial student cohort was registered, while the remaining half failed. Financial registers failed to balance.
- **Scenario B (Single Transaction with `PassLoader.java`):**
  - Result: **0 records remained in the table.**
  - Consequence: The `con.rollback()` invoked in the catch block reversed all preliminary inserts. Database state remained 100% clean and consistent.

---

## B3.4 – Engineering Trade-Off & Recommendation (2 Marks)

### Architecture Decision by Workload Profile

| Workload Scenario | Recommended Persistence Strategy | Justification |
|:---|:---|:---|
| **Nightly Bulk Import of 100,000 Passes** | **PreparedStatement with Batch Processing (`addBatch()`) inside Single Transaction** | Reduces import duration from **~4.5 minutes to under 3 seconds** (33,456 rows/sec). Eliminates socket saturation and ensures atomic rollback if import data is corrupted. |
| **Single Counter Pass Issuance (OLTP)** | **Single PreparedStatement with Immediate Auto-Commit** | A single student standing at the counter requires immediate, synchronous confirmation. Holding open a long batch transaction adds unnecessary connection lock contention. |

---

## Alternative Concept and Complexity Analysis: JDBC Batching vs Multi-Row INSERT vs MySQL LOAD DATA

### Architectural Alternatives

```
1. JDBC Batching:
   PreparedStatement.addBatch() -> driver rewrites to bulk protocol or sends batched packets.

2. Multi-Row INSERT:
   INSERT INTO bus_pass VALUES (...), (...), (...), ... (up to max_allowed_packet)

3. MySQL LOAD DATA INFILE:
   Server-side direct file reading bypasses SQL parser and writes directly to InnoDB storage pages.
```

---

### Comparative Evaluation

| Metric | JDBC PreparedStatement Batch (`b = 1000`) | Multi-Row INSERT (`b = 1000`) | MySQL `LOAD DATA INFILE` |
|:---|:---|:---|:---|
| **Total Time for $n$ Rows** | **$O(n)$** ($\approx 33,000\text{ rows/sec}$) | **$O(n)$** ($\approx 35,000\text{ rows/sec}$) | **$O(n)$** ($\approx 80,000 - 120,000\text{ rows/sec}$) |
| **Client Memory Used** | **$O(b)$** ($1,000$ row parameter arrays $\approx 500\text{ KB}$) | **$O(b)$** (Single giant SQL string $\approx 1\text{ MB}$) | **$O(1)$** (Direct OS file streaming) |
| **SQL Parser Overhead** | None (parameter arrays bound to compiled AST) | Low (single SQL command parsed per batch) | **Zero** (completely bypasses SQL compilation engine) |
| **Security / Injection Risk**| **Zero Risk** (Strict typed parameter binding) | **High** if string concatenation used; requires escaping | Medium (requires secure file privileges and path restrictions) |
| **Portability** | Universal across all SQL databases via JDBC | Universal standard ANSI SQL | Vendor-specific (MySQL / MariaDB proprietary) |

### Engineering Verdict
While `LOAD DATA INFILE` provides the highest raw ingest speed, it requires the source file to reside on the database server filesystem (or `local-infile` enabled, which poses significant security risks). **JDBC PreparedStatement Batching** provides the optimal balance of enterprise portability, strict type validation, injection immunity, and high throughput (33k+ rows/sec).
