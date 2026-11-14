# Programming in Java – Assignment 2 (CO2)
## Complete Laboratory Implementation & Evaluation

- **Student Name:** Devi Vara Prasad
- **Register Number:** 192365032
- **Subject:** Programming in Java
- **Assignment:** Assignment 2 – Course Outcome 2 (CO2)
- **Total Marks:** 50
- **Java Version:** Java SE 25.0.2 LTS (Oracle Corporation, Windows 11 64-bit)
- **Repository URL:** [https://github.com/vara12126/Java](https://github.com/vara12126/Java)

---

## 1. Project Directory Structure

```text
Assignment 2/
├── Q1_String_Benchmark/
│   ├── src/                          # ETicket, StringConcat, StringBuilder, StringBuffer
│   ├── screenshots/                  # Q1_String_Output.png, Q1_Benchmark.png
│   ├── results/                      # Q1_Benchmark_Results.csv
│   └── README.md
│
├── Q2_List_Comparison/
│   ├── src/                          # Track, UpNextQueue, ManualArray, ListBenchmark
│   ├── screenshots/                  # Q2_List_Comparison.png
│   ├── results/                      # Q2_List_Benchmark.csv
│   └── README.md
│
├── Q3_HashCode_Equals/
│   ├── src/                          # Student, AttendanceBefore, AttendanceAfter
│   ├── screenshots/                  # Q3_HashSet_Before.png, Q3_HashSet_After.png
│   ├── results/                      # Q3_Scenario_Results.txt
│   └── README.md
│
├── Q4_Map_Comparison/
│   ├── src/                          # Medicine, SortedArray, PharmacyBenchmarkRunner
│   ├── screenshots/                  # Q4_Map_Output.png
│   ├── results/                      # Q4_Map_Benchmark.csv
│   └── README.md
│
├── Q5_Generics_Leaderboard/
│   ├── src/                          # Team, Leaderboard, LeaderboardApp
│   ├── screenshots/                  # Q5_Leaderboard_Output.png
│   ├── results/
│   └── README.md
│
├── Q6_Priority_Queue/
│   ├── src/                          # Patient, TriageSystem, MultiLevelQueue, Benchmark
│   ├── screenshots/                  # Q6_Triage_Output.png
│   ├── results/                      # Q6_Triage_Benchmark.csv
│   └── README.md
│
├── Q7_Design_Challenge/
│   ├── original/                     # OriginalTriageQueue.java
│   ├── redesigned/                   # PatientNode.java, RedesignedTriageQueue.java
│   ├── screenshots/                  # Q7_Original_Result.png, Q7_Redesigned_Result.png
│   ├── results/                      # Q7_Design_Challenge_Benchmark.csv
│   └── README.md
│
├── report/
│   ├── Assignment2_Report.md         # Master Technical Documentation (All 15 sections)
│   ├── TerminalCaptureEngine.java    # Terminal Screenshot Rendering Utility
│   └── diagrams/
│
└── README.md                         # This Master Summary File
```

---

## 2. Compilation Instructions

From the repository root (`Java/`):

```powershell
# Create build directory
New-Item -ItemType Directory -Path "bin" -Force

# Compile all source files across Questions 1 through 7
$sources = (Get-ChildItem -Path "Assignment 2\*\src\*.java", "Assignment 2\Q7_Design_Challenge\*\*.java" -Recurse).FullName
& 'C:\Program Files\Java\jdk-25.0.2\bin\javac.exe' -d bin -cp bin $sources
```

---

## 3. Execution Instructions

```powershell
# Question 1: String Benchmarking (10k Tickets, 50 Fields)
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q1_string.RailwayTicketBenchmark

# Question 2: Music Playlist Collections Benchmark
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q2_list.ListBenchmarkSuite

# Question 3: HashCode-Equals Attendance System (Before & After)
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q3_hashcode.AttendanceSystemBefore
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q3_hashcode.AttendanceSystemAfter

# Question 4: Hospital Pharmacy Map Architectures
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q4_map.PharmacyBenchmarkRunner

# Question 5: Generic Sports Leaderboard Application
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q5_leaderboard.LeaderboardApp

# Question 6: Emergency Triage System
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q6_triage.TriageSystem
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q6_triage.TriageBenchmarkRunner

# Question 7: Day-4 Design Challenge Head-to-Head Validation
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin q7_design.DesignChallengeValidation
```

---

## 4. Question-wise Summary of Results

### Q1: String, StringBuilder and StringBuffer (Railway E-Ticket Generation)
- **Problem**: 50 metadata fields per ticket across 100, 1,000, and 10,000 workloads.
- **Empirical Measurement (10,000 tickets)**:
  - String `+`: **329.131 ms**, 1,500,000 intermediate objects.
  - `StringBuilder`: **65.320 ms** (**5.04x faster**), 10,000 objects.
  - `StringBuffer`: **72.297 ms** (**4.55x faster**; lock overhead measured).
- **Concurrency**: `StringBuilder` lost 73% of characters under 8 concurrent threads; `StringBuffer` preserved 100% integrity.
- **API Comparison**: `String.join()` (187k ops/sec) vs `String.format()` (12k ops/sec; 14x slower).

### Q2: ArrayList, LinkedList and ArrayDeque (Music Playlist)
- **Problem**: Adding, inserting, deleting, random accessing, and iterating over 50,000 tracks.
- **Random Access `get(k)` (10,000 queries)**:
  - `ArrayList`: **0.514 ms** ($O(1)$)
  - `LinkedList`: **610.374 ms** ($O(N)$ pointer traversal; **1,187x slower**).
- **Insert Middle**: `ArrayList` is **25.1x faster** than `LinkedList` due to vectorized `System.arraycopy()`.
- **Up Next Queue**: Verified circular `ArrayDeque` with $O(1)$ priority jumping and polling.

### Q3: HashCode and Equals Contract (Student Attendance System)
- **Initial State (Without hashCode)**: `attendanceSet.size()` was 2; duplicate student admitted.
- **Corrected State (TODO 1 Complete)**: `hashCode()` overridden with `Objects.hash(studentId)`; duplicate cleanly rejected (`size = 1`).
- **Scenarios**:
  - Scenario 1: 2,000 registrations with 500 unique IDs retained exactly 500 students.
  - Scenario 2: 5,000 collisions in bucket handled via Java 8+ Red-Black treeification (`TreeNode`).
  - Scenario 3: Mutating `studentId` post-insertion trapped the student as an unfindable "zombie object".

### Q4: Hospital Pharmacy Inventory (Map Comparison)
- **Lookups (100,000 queries on 50k items)**:
  - `HashMap`: **7.229 ms** ($O(1)$)
  - `LinkedHashMap`: **4.524 ms** ($O(1)$)
  - `TreeMap`: **60.305 ms** ($O(\log N)$)
- **Sorted Report**: `TreeMap` finished in **0.340 ms** (in-order walk) vs `HashMap` at **38.773 ms** (requires key sort).
- **Alternative**: `SortedArray` + `binarySearch` achieves fast 55 ms lookups, but incurs a 27 ms insertion penalty ($O(N)$ shifting).

### Q5: Generics and Sports Leaderboard
- **TODO 1 & 2 Completed**: Multi-tiered comparator implemented using `Comparator.comparingInt(Team::getPoints).reversed().thenComparing(...).thenComparing(Team::getName)`.
- **Verified Output Produced**:
  ```text
  [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
  ```
- **Why Bisons > Panthers**: Both tied on points (9) and goal difference (+4); Rule 3 (alphabetical order) awarded Bisons precedence because 'B' precedes 'P' in Unicode.

### Q6: PriorityQueue and Emergency Triage System
- **TODO Completed**: PriorityQueue ordered by ascending severity number, then ascending arrival counter.
- **Verified Treatment Order**:
  ```text
  Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
  ```
- **Direct Queue Print**: `[Meena(S1), Lata(S2), Kumar(S1), Asha(S5), John(S3), Ravi(S3)]` explains binary min-heap level-order array layout.
- **Feasibility for 5,000 Patients/Day**: Verified operating at $< 0.001\%$ CPU utilization and $< 1\text{ MB}$ RAM.

### Q7: Day-4 Extended Design Challenge (13 Marks)
- **Bottleneck Diagnosed**: $O(N)$ linear scans for lookup and $O(N^2)$ re-heapification during aging sweeps in `PriorityQueue`.
- **Redesigned Solution**: `IndexedMultiLevelQueue` combining secondary hash index with 5 doubly-linked discrete buckets and a 5-bit register.
- **Measured Validation Results (10,000 Patients)**:
  - **50,000 Lookups**: Reduced from 3,074.315 ms to 16.126 ms (**190.64x faster**).
  - **2,000 Priority Updates**: Reduced from 194.881 ms to 3.128 ms (**62.30x faster**).
  - **Aging Sweep (4,407 promoted)**: Reduced from 113.273 ms to 2.711 ms (**41.79x faster**).

---

## 5. Verification Checklist
- [x] All 7 Questions compiled and executed independently.
- [x] Exact required console outputs matched 100%.
- [x] All TODO sections completed.
- [x] Genuine execution screenshots recorded in corresponding `screenshots/` folders.
- [x] Actual experimental measurements exported to `results/*.csv`.
- [x] Existing Assignment 1 files 100% preserved and untouched.
- [x] Progressive Git commit history maintained.
