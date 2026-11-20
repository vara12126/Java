# Programming in Java – Assignment 5 (CO5)
## Data Persistence Engineering – Files, Serialization & Databases

**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Course:** Programming in Java (CSA09)  
**Course Outcome:** CO5 – Java I/O Streams, Serialization, and JDBC Persistence Engineering  
**Degree / Department:** B.Tech Computer Science & Engineering  
**Institution:** SIMATS Engineering, Saveetha Institute of Medical and Technical Sciences  
**Total Marks:** 50 / 50 Marks (Part A: 25 Marks, Part B: 25 Marks)  
**GitHub Repository:** [https://github.com/Dhanushssmb/java.git](https://github.com/Dhanushssmb/java.git)  

---

## Table of Contents

1. [Executive Summary & Purpose](#executive-summary--purpose)
2. [Project Folder Hierarchy](#project-folder-hierarchy)
3. [Environment & Software Specifications](#environment--software-specifications)
4. [Database Configuration & Setup Instructions](#database-configuration--setup-instructions)
5. [How to Compile and Run Each Program](#how-to-compile-and-run-each-program)
6. [Task-Wise Implementation & Expected Results](#task-wise-implementation--expected-results)
7. [Empirical Performance Measurements & Visual Charts](#empirical-performance-measurements--visual-charts)
8. [Comprehensive Verification Matrix](#comprehensive-verification-matrix)
9. [Report Documents](#report-documents)

---

## Executive Summary & Purpose

This repository contains the complete implementation, theoretical designs, benchmark instrumentation, and empirical evidence for **Assignment 5 (CO5)** of Programming in Java.

The project investigates the core question:
> *"How should a Java engineer decide where and how data is stored, and how can the program be made correct, safe and efficient as the data grows?"*

We follow the full engineering progression:
$$\text{Model} \longrightarrow \text{Choose a Store} \longrightarrow \text{Design} \longrightarrow \text{Protect} \longrightarrow \text{Implement} \longrightarrow \text{Measure} \longrightarrow \text{Compare} \longrightarrow \text{Decide}$$

---

## Project Folder Hierarchy

```
Java-Assignment-5/
│
├── README.md                                 # Master Assignment 5 Documentation
├── Assignment5_Report.md                     # Full Academic Report in Markdown
├── Assignment5_Report.pdf                    # Complete Academic Report in PDF
├── generate_charts.py                        # Python script generating high-res visual plots
├── build_pdf_report.py                       # Python ReportLab compilation script
│
├── config/
│   ├── db.properties.example                 # Template configuration for credentials
│   └── db.properties                         # Local database configuration (ignored by Git)
│
├── lib/
│   └── mysql-connector-j-26.10.0.jar         # Official MySQL JDBC Driver
│
├── Part-A-Design/
│   ├── Task-A1-File-Format/
│   │   └── Answer.md                         # Delimited/Binary layouts, SIMATS pseudocode, RandomAccessFile
│   ├── Task-A2-Buffered-IO/
│   │   └── Answer.md                         # 50MB unbuffered prediction (52.4M calls), buffering, readAllLines
│   ├── Task-A3-DAO-Pattern/
│   │   └── Answer.md                         # Generic Dao<T,K>, StudentDao, ORM mapping, decoupling analysis
│   ├── Task-A4-Serialization/
│   │   └── Answer.md                         # Transient password/connection, serialVersionUID, JSON comparison
│   └── Task-A5-Transactions-SQL-Injection/
│       └── Answer.md                         # Wallet transfer ACID pseudocode, SQL injection AST breakdown
│
├── Part-B-Implementation/
│   ├── Task-B1-File-Store/
│   │   ├── src/
│   │   │   ├── PassStore.java                # Bus pass store with completed TODO 1
│   │   │   ├── LineCounter.java              # Unbuffered vs Buffered I/O line counter
│   │   │   └── TaskB1Benchmark.java          # Validation, 1-50MB buffering, 1k-100k growth tests
│   │   ├── measurements/
│   │   │   ├── buffering_benchmark.csv       # Measured 1MB, 10MB, 50MB buffering times
│   │   │   └── growth_benchmark.csv          # Measured 1k, 10k, 100k search growth latencies
│   │   └── Answer.md                         # Complete Task B1 report & analysis
│   │
│   ├── Task-B2-Adversarial-Inputs/
│   │   ├── src/
│   │   │   ├── AdversarialLoginTest.java     # SQL injection demonstration (Unsafe vs PreparedStatement)
│   │   │   └── DamagedFileTest.java          # Corrupted line, wrong tokens, non-numeric fare tests
│   │   ├── sql/
│   │   │   └── setup_login_db.sql            # Test user schema and dummy accounts
│   │   ├── screenshots/
│   │   │   ├── login_adversarial_output.txt  # Real console capture of SQL injection tests
│   │   │   └── damaged_file_output.txt       # Real console capture of damaged file parsing
│   │   └── Answer.md                         # Complete Task B2 report & analysis
│   │
│   ├── Task-B3-JDBC-Batch/
│   │   ├── src/
│   │   │   ├── PassLoader.java               # Completed TODO 1, 2, 3 (Batch PreparedStatement)
│   │   │   └── TaskB3Benchmark.java          # Statement vs PreparedStatement vs Batch up to 100k
│   │   ├── sql/
│   │   │   └── setup_bus_pass_table.sql      # Schema for bus_pass table
│   │   ├── measurements/
│   │   │   └── jdbc_insertion_benchmark.csv  # Measured insertion times and throughput
│   │   ├── screenshots/
│   │   │   └── task_b3_benchmark_output.txt  # Real console capture of benchmark & rollback test
│   │   └── Answer.md                         # Complete Task B3 report & analysis
│   │
│   └── Task-B4-Final-Decision/
│       ├── src/
│       │   ├── PassMigration.java            # Defensive ETL batch migration with transaction
│       │   └── DatabaseInspector.java        # DatabaseMetaData runtime engine introspection
│       ├── migration-report/
│       │   ├── migration_summary.txt         # Real migration summary statistics
│       │   └── failed_records.csv            # Detailed failure audit of corrupted lines
│       ├── screenshots/
│       │   ├── migration_execution_output.txt
│       │   └── metadata_inspector_output.txt
│       └── Answer.md                         # Complete Task B4 report & production decision
│
└── evidence/
    ├── test-results/                         # Verifiable console outputs from all test runs
    ├── performance-charts/                   # High-resolution comparison plots (PNG)
    │   ├── chart1_buffering_comparison.png
    │   ├── chart2_search_growth.png
    │   ├── chart3_jdbc_performance.png
    │   └── chart4_transaction_atomicity.png
    └── final-screenshots/
```

---

## Environment & Software Specifications

- **Java Development Kit:** Java SE 27 (`version 27.0.1.0.3`, 64-bit Server VM)
- **Database Engine:** MySQL Server 8.0.43 (InnoDB Storage Engine)
- **JDBC Driver:** MySQL Connector/J (`mysql-connector-j-26.10.0.jar`)
- **Python Runtime:** Python 3.12 (Matplotlib 3.11.2, Pandas, ReportLab)
- **Operating System:** Microsoft Windows 11 Home (x64)

---

## Database Configuration & Setup Instructions

A dedicated local test database `assignment5_testdb` is used for all database tasks. Real credentials are never committed.

1. Ensure MySQL Server 8.0 is running locally (`Get-Service MYSQL80`).
2. Copy `config/db.properties.example` to `config/db.properties` and configure your credentials:
   ```properties
   db.host=localhost
   db.port=3306
   db.name=assignment5_testdb
   db.user=root
   db.password=YOUR_LOCAL_PASSWORD
   ```
3. Initialize the database schema:
   ```powershell
   mysql -u root -p < Java-Assignment-5/Part-B-Implementation/Task-B2-Adversarial-Inputs/sql/setup_login_db.sql
   mysql -u root -p < Java-Assignment-5/Part-B-Implementation/Task-B3-JDBC-Batch/sql/setup_bus_pass_table.sql
   ```

---

## How to Compile and Run Each Program

All commands should be executed from the root repository directory in PowerShell:

```powershell
# Set Classpath Variable
$cp = ".;Java-Assignment-5/lib/mysql-connector-j-26.10.0.jar;Java-Assignment-5/bin"

# Create output class directory
mkdir -p Java-Assignment-5/bin

# -------------------------------------------------------------
# TASK B1: File Store & Buffering Instrumentation
# -------------------------------------------------------------
javac -d Java-Assignment-5/bin Java-Assignment-5/Part-B-Implementation/Task-B1-File-Store/src/*.java
java -cp Java-Assignment-5/bin PassStore
java -cp Java-Assignment-5/bin TaskB1Benchmark

# -------------------------------------------------------------
# TASK B2: Adversarial SQL Injection & Corrupted Files
# -------------------------------------------------------------
javac -cp $cp -d Java-Assignment-5/bin Java-Assignment-5/Part-B-Implementation/Task-B2-Adversarial-Inputs/src/*.java
java -cp $cp AdversarialLoginTest
java -cp $cp DamagedFileTest

# -------------------------------------------------------------
# TASK B3: JDBC Statement vs PreparedStatement vs Batch
# -------------------------------------------------------------
javac -cp $cp -d Java-Assignment-5/bin Java-Assignment-5/Part-B-Implementation/Task-B3-JDBC-Batch/src/*.java
java -cp $cp TaskB3Benchmark

# -------------------------------------------------------------
# TASK B4: Enterprise Migration & Database Inspection
# -------------------------------------------------------------
javac -cp $cp -d Java-Assignment-5/bin Java-Assignment-5/Part-B-Implementation/Task-B4-Final-Decision/src/*.java
java -cp $cp PassMigration
java -cp $cp DatabaseInspector

# -------------------------------------------------------------
# Generate Visual Performance Charts & Compile PDF Report
# -------------------------------------------------------------
python Java-Assignment-5/generate_charts.py
python Java-Assignment-5/build_pdf_report.py
```

---

## Task-Wise Implementation & Expected Results

### Task B1 – PassStore Verification (TODO 1)
- **Completed Code:** Inside `PassStore.java`, `find(String passId)` reads the file line-by-line using `BufferedReader` inside try-with-resources, splits on comma, skips records where `parts.length != 5`, and returns the matching record fields.
- **Observed Terminal Output:**
  ```text
  [P1003, 192311003, Divya, 7, 3800.0]
  null
  ```
- **Explanation:** The damaged record `P1002,DAMAGED LINE` has only 2 tokens. The integrity check skips it cleanly, allowing `find("P1003")` to locate the subsequent valid pass without throwing exceptions.

---

### Task B2 – Adversarial Inputs
- **SQL Injection:**
  - `admin` with `' OR '1'='1`: Bypasses dynamic SQL authentication in the unsafe version; safely rejected as an invalid literal password by `PreparedStatement`.
  - `admin' -- `: Truncates password check in dynamic SQL; rejected by `PreparedStatement`.
- **Damaged File Ingestion:**
  - Truncated lines (3 fields) skipped safely via length guard.
  - Non-numeric amounts skipped without crashing via `Double.parseDouble` exception handling.
  - Empty lines and whitespace discarded without errors.

---

### Task B3 – PassLoader Verification (TODO 1, 2, 3)
- **Completed Code:** In `PassLoader.java`:
  - `TODO 1`: Bound all 5 parameters, called `ps.addBatch()`, and executed batch every 1,000 rows via `ps.executeBatch()`.
  - `TODO 2`: Committed transaction (`con.commit()`) and returned row count.
  - `TODO 3`: Rolled back transaction (`con.rollback()`) upon exception and re-threw.
- **Atomicity Proof:**
  - With Auto-Commit ON, a mid-stream crash at record 500 left **500 orphaned records** in the database.
  - With `PassLoader.loadAll()` (Single Transaction), `con.rollback()` restored the table to **0 records**, guaranteeing 100% ACID consistency.

---

### Task B4 – Migration & Database Inspection
- **Migration:** Ingested 35 mixed records. Valid passes (30) migrated in 703 ms inside an atomic transaction; 5 corrupted lines intercepted and logged to `failed_records.csv`.
- **Database Inspection:** Introspected MySQL 8.0.43, MySQL Connector/J 26.10.0, JDBC 4.2 compliance, and table definitions for `bus_pass` and `users`.
- **Decision:** Recommended a **Centralized Relational Database (MySQL InnoDB)** for 800,000 passes across 12 campuses due to MVCC row-level concurrency, sub-millisecond B+ Tree lookups ($< 0.5\text{ ms}$), and full ACID crash recovery.

---

## Empirical Performance Measurements & Visual Charts

### 1. Buffering Benchmark (Task B1.3)
| File Size | Unbuffered `FileInputStream` | `BufferedInputStream` (8 KB) | `BufferedReader` | Measured Speedup |
|:---:|:---:|:---:|:---:|:---:|
| **1 MB** | 4,507 ms | 22 ms | 34 ms | **204.86x** |
| **10 MB** | 29,946 ms | 132 ms | 60 ms | **226.86x** |
| **50 MB** | 151,731 ms ($2.53\text{ min}$) | 630 ms | 127 ms | **240.84x** |

### 2. Search Growth Latency (Task B1.4)
| Records | File Size | Scan Head | Scan Mid | Scan Tail | Scan Not Found | In-Memory HashMap Lookup |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **1,000** | $43.5\text{ KB}$ | 6.70 ms | 2.16 ms | 2.24 ms | 2.09 ms | 0.0299 ms ($29.9\text{ }\mu\text{s}$) |
| **10,000** | $445\text{ KB}$ | 12.72 ms | 7.63 ms | 13.75 ms | 11.31 ms | 0.0058 ms ($5.8\text{ }\mu\text{s}$) |
| **100,000** | $4.55\text{ MB}$ | 49.70 ms | 22.66 ms | 44.34 ms | 32.44 ms | 0.0087 ms ($8.7\text{ }\mu\text{s}$) |

### 3. JDBC Ingestion Benchmark (Task B3.2)
| Records | Statement Time | PreparedStatement Time | Batch Time (`PassLoader`) | Batch Throughput | Speedup vs Statement |
|:---:|:---:|:---:|:---:|:---:|:---:|
| **1,000** | 3,362 ms | 3,048 ms | **82 ms** | 12,195 rows/sec | **41.00x** |
| **10,000** | 24,006 ms | 24,079 ms | **398 ms** | 25,125 rows/sec | **60.32x** |
| **100,000** | ~280,000 ms | ~220,000 ms | **2,989 ms** | 33,456 rows/sec | **93.68x** |

### High-Resolution Performance Plots:
- **Buffering Comparison:** `evidence/performance-charts/chart1_buffering_comparison.png`
- **Search Growth & HashMap:** `evidence/performance-charts/chart2_search_growth.png`
- **JDBC Ingestion:** `evidence/performance-charts/chart3_jdbc_performance.png`
- **Transaction Atomicity:** `evidence/performance-charts/chart4_transaction_atomicity.png`

---

## Comprehensive Verification Matrix

| Task | Rubric Component | Expected Evidence | Actual Execution Status |
|:---:|:---|:---|:---:|
| **A1** | File Format Design | Delimited and binary layouts, SIMATS pseudocode, RandomAccessFile padding | **COMPLETE (Answer.md)** |
| **A2** | Buffered I/O Analysis | 50MB prediction ($52.4\text{M}$ calls), context switches, readAllLines | **COMPLETE (Answer.md)** |
| **A3** | DAO Design Pattern | Generic `Dao<T,K>`, `StudentDao`, ORM mapping, decoupling analysis | **COMPLETE (Answer.md)** |
| **A4** | Serialization Design | Transient fields, object graph DFS, `serialVersionUID`, JSON | **COMPLETE (Answer.md)** |
| **A5** | Safety & Transactions | ACID commit/rollback pseudocode, SQL injection AST breakdown | **COMPLETE (Answer.md)** |
| **B1** | Instrumented File Store | `PassStore.java` TODO 1, buffering benchmark, growth measurements | **VERIFIED (100% Pass)** |
| **B2** | Adversarial Inputs | SQL injection unsafe vs safe, damaged CSV parsing tests | **VERIFIED (100% Pass)** |
| **B3** | JDBC Batching | `PassLoader.java` TODO 1-3, Statement vs PS vs Batch, rollback test | **VERIFIED (100% Pass)** |
| **B4** | Enterprise Decision | `PassMigration.java`, `DatabaseInspector.java`, 12-campus evaluation | **VERIFIED (100% Pass)** |

---

## Report Documents

- **Markdown Report:** [Assignment5_Report.md](./Assignment5_Report.md)
- **PDF Report:** [Assignment5_Report.pdf](./Assignment5_Report.pdf)
