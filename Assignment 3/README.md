# Programming in Java – Assignment 3 (CO3)

## Student Information
- **Student A**: Devi Vara Prasad | **Register Number**: 192365032
- **Student B**: [Student B Name] | **Register Number**: [Student B Register Number]
- **Course**: Programming in Java
- **Course Outcome**: CO3 – Exception Handling and Multi-threaded Programming
- **Total Marks**: 50
- **GitHub Repository**: [https://github.com/vara12126/Java](https://github.com/vara12126/Java)
- **Target Folder**: `Assignment 3`
- **Java Platform**: Java SE 25.0.2 (Oracle Corporation), Windows 11 64-bit

---

## Repository Structure

```text
Assignment 3/
├── Task01_ATM_Exception_Handling/
│   ├── src/
│   ├── screenshots/
│   ├── test-results/
│   └── README.md
│
├── Task02_User_Defined_Exceptions/
│   ├── src/
│   ├── screenshots/
│   ├── test-results/
│   └── README.md
│
├── Task03_Resource_Safety/
│   ├── src/
│   ├── screenshots/
│   ├── test-results/
│   └── README.md
│
├── Task04_Thread_Lifecycle/
│   ├── src/
│   ├── screenshots/
│   ├── test-results/
│   └── README.md
│
├── Task05_Race_Conditions/
│   ├── src/
│   ├── screenshots/
│   ├── results/
│   └── README.md
│
├── Task06_Deadlock_Producer_Consumer/
│   ├── src/
│   ├── screenshots/
│   ├── test-results/
│   └── README.md
│
├── Task07_Concurrent_Processor/
│   ├── src/
│   ├── screenshots/
│   ├── results/
│   └── README.md
│
├── Screenshots/
│   ├── Task01_ATM_Exception_Handling.png
│   ├── Task02_User_Defined_Exceptions.png
│   ├── Task03_Resource_Safety.png
│   ├── Task04_Thread_Lifecycle.png
│   ├── Task05_Race_Conditions.png
│   ├── Task06_Deadlock_Producer_Consumer.png
│   └── Task07_Concurrent_Processor.png
│
├── Results/
│   ├── Task04_Thread_Runs.csv
│   ├── Task05_Sync_Benchmark.csv
│   └── Task07_Throughput_Metrics.csv
│
├── Report/
│   ├── Assignment3_Report.md
│   └── TerminalCaptureEngine.java
│
└── README.md
```

---

## Task Summary Table

| Task Folder | Core Concepts | Key Classes | Status |
|---|---|---|:---:|
| **Task 01: ATM Exception Handling** | Multi-catch, specific-to-general catch, validation, `finally` card ejection | `ATMExceptionHandlingDemo`, `Account`, `ATMDispenser` | **100% Passed** |
| **Task 02: User-Defined Exceptions** | Custom exception hierarchy, `throw`, `throws`, exception chaining (`getCause()`) | `BankingException`, `FundTransferService`, `UserDefinedExceptionDemo` | **100% Passed** |
| **Task 03: Resource Safety** | AutoCloseable, try-with-resources, LIFO closing order, suppressed exceptions | `LedgerFileReader`, `DatabaseSession`, `ResourceSafetyDemo` | **100% Passed** |
| **Task 04: Thread Lifecycle** | Thread states (`NEW`..`TERMINATED`), `start()` vs `run()`, `join()`, `isAlive()`, 10 priority runs | `StatementWorker`, `ThreadStateObserver`, `ThreadLifecycleDemo` | **100% Passed** |
| **Task 05: Race Conditions** | Lost update hazard, synchronized methods vs blocks vs lock-free Atomic CAS | `UnsynchronizedAccount`, `SynchronizedMethodAccount`, `AtomicAccount`, `SynchronizationBenchmark` | **100% Passed** |
| **Task 06: Deadlock & Queue** | Coffman conditions, global lock ordering, bounded buffer with `wait()` and `notifyAll()` | `Account`, `SafeAccountTransfer`, `TransactionQueue`, `DeadlockProducerConsumerDemo` | **100% Passed** |
| **Task 07: Concurrent Processor** | `ExecutorService`, `Callable`, `Future`, SLA timeouts, `ConcurrentHashMap`, graceful shutdown | `TransactionTask`, `TransactionResult`, `ConcurrentClearingEngine` | **100% Passed** |

---

## How to Compile and Execute

### 1. Build All Tasks
From the root of the repository:
```bash
# In Windows PowerShell:
$src = (Get-ChildItem -Path "Assignment 3\*\src\*.java", "Assignment 3\Report\*.java").FullName
javac -d bin -cp bin $src
```

### 2. Run Demonstrations
```bash
# Task 01: ATM Exception Handling
java -cp bin task01_atm.ATMExceptionHandlingDemo

# Task 02: User-Defined Exceptions & Chaining
java -cp bin task02_exceptions.UserDefinedExceptionDemo

# Task 03: Resource Safety & Suppressed Exceptions
java -cp bin task03_resources.ResourceSafetyDemo

# Task 04: Thread Lifecycle & 10 Priority Runs
java -cp bin task04_lifecycle.ThreadLifecycleDemo

# Task 05: Race Conditions & Synchronization Benchmark (20,000 Ops)
java -cp bin task05_concurrency.SynchronizationBenchmark

# Task 06: Deadlock Prevention & Producer-Consumer Queue
java -cp bin task06_deadlock_queue.DeadlockProducerConsumerDemo

# Task 07: High-Throughput Concurrent Clearing Engine
java -cp bin task07_executor.ConcurrentClearingEngine
```

---

## Pair Work Distribution
- **Student A (Devi Vara Prasad, Reg: 192365032)**: Lead developer for Tasks 1, 2, 3, 6, and 7; designed the high-DPI execution capture engine.
- **Student B ([Student B Name], Reg: [Student B Register Number])**: Implemented benchmark data collection, suppressed exception parsing, 10-run priority scheduling, and lock-free CAS validation.
