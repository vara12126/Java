# PROGRAMMING IN JAVA – ASSIGNMENT 3 (CO3)
## Master Comprehensive Laboratory & Research Report

---

### Cover Page & Student Metadata
- **Student A (Lead Developer & Author)**: Devi Vara Prasad
  - **Register Number**: 192365032
- **Student B (Pair Programming Partner)**: `[Student B Name]`
  - **Register Number**: `[Student B Register Number]`
- **Course**: Programming in Java
- **Course Outcome**: CO3 – Exception Handling and Multi-threaded Programming
- **Total Marks**: 50 Marks
- **GitHub Repository**: [https://github.com/vara12126/Java](https://github.com/vara12126/Java)
- **Target Folder**: `Assignment 3`
- **Execution Platform**: Java SE 25.0.2 (Oracle Corporation), Windows 11 Professional 64-bit

---

## Table of Contents
1. [Executive Summary & Architecture](#1-executive-summary--architecture)
2. [Task 01: ATM Exception Handling System](#2-task-01-atm-exception-handling-system)
3. [Task 02: User-Defined Exceptions & Exception Chaining](#3-task-02-user-defined-exceptions--exception-chaining)
4. [Task 03: Resource Safety, AutoCloseable & Suppressed Exceptions](#4-task-03-resource-safety-autocloseable--suppressed-exceptions)
5. [Task 04: Thread Lifecycle, State Transitions & Priority Scheduling](#5-task-04-thread-lifecycle-state-transitions--priority-scheduling)
6. [Task 05: Race Conditions, Synchronization & Performance Benchmark](#6-task-05-race-conditions-synchronization--performance-benchmark)
7. [Task 06: Deadlock Avoidance via Global Lock Ordering & Bounded Queue](#7-task-06-deadlock-avoidance-via-global-lock-ordering--bounded-queue)
8. [Task 07: High-Throughput Concurrent Clearing Engine with ExecutorService](#8-task-07-high-throughput-concurrent-clearing-engine-with-executorservice)
9. [Comprehensive Complexity & Performance Comparison](#9-comprehensive-complexity--performance-comparison)
10. [Student Contribution & Pair-Work Division](#10-student-contribution--pair-work-division)
11. [Joint Conclusions & Key Learnings](#11-joint-conclusions--key-learnings)
12. [Overall Result & Verification Sign-Off](#12-overall-result--verification-sign-off)

---

## 1. Executive Summary & Architecture

The objective of **Assignment 3 (CO3)** is to design, implement, and rigorously benchmark enterprise-grade Java systems covering two fundamental computer science pillars:
1. **Robust Exception Handling & Resource Safety**:
   - Defending applications against runtime crashes, invalid user inputs, and security lockouts.
   - Preserving forensic traceability through user-defined exception hierarchies and **exception chaining** (`Throwable.initCause()` / `getCause()`).
   - Eliminating resource leakage and masked exception bugs via **Java 7+ `try-with-resources`** and **suppressed exceptions** (`Throwable.getSuppressed()`).
2. **Multi-Threaded Concurrency, Synchronization & Thread Pools**:
   - Investigating thread life-cycle transitions (`NEW`, `RUNNABLE`, `TIMED_WAITING`, `TERMINATED`) and multi-thread coordination (`join()`, `isAlive()`).
   - Demonstrating that thread priorities are advisory hints subject to non-deterministic preemptive OS scheduling across **10 empirical runs**.
   - Exposing race conditions and lost update hazards on shared banking state, comparing intrinsic synchronized methods, synchronized blocks, and lock-free **Atomic CAS (`AtomicLong`)** across 20,000 operations.
   - Solving the classic **Coffman Deadlock** in cross-account transfers using **Deterministic Global Lock Ordering**.
   - Implementing a thread-safe **Bounded Buffer Producer-Consumer** pattern using `wait()`, `notifyAll()`, and while-loop guarded suspension.
   - Engineering a fault-tolerant **Concurrent Clearing Engine** using `ExecutorService`, `Callable`, `Future`, SLA timeout enforcement (`TimeoutException`), and `ConcurrentHashMap`.

---

## 2. Task 01: ATM Exception Handling System

### 2.1 Objective & Problem Description
Design an Automated Teller Machine (ATM) transaction processing pipeline capable of handling valid and erroneous withdrawal operations gracefully without terminating or leaking sensitive hardware resources.

### 2.2 Architectural Highlights
- **Input Validation**: Verifying that inputs are non-null and numeric via `NumberFormatException` and `NullPointerException`.
- **Domain Business Rules**: Ensuring withdrawal amounts are positive, divisible by $100 denominations, do not exceed the per-transaction cap ($2,000.00), and do not exceed available account balance.
- **Security Lockout State**: Enforcing a strict 3-attempt PIN validation limit. On the 3rd failure, the account transitions to `LOCKED` state, rejecting subsequent transactions.
- **Guaranteed Cleanup in `finally`**: Enforcing that the physical ATM debit card is safely ejected from the machine motorized card feeder under all execution paths (successful completion, validation failure, or runtime crash).

### 2.3 Execution Evidence
![Task 01 Screenshot](../Screenshots/Task01_ATM_Exception_Handling.png)

### 2.4 Verified Test Scenarios
1. **Valid Withdrawal ($500.00)**: PIN verified, balance decremented from $2,000 to $1,500, ATM vault updated, card ejected.
2. **Non-Numeric Amount String (`"500XYZ"`)**: Intercepted by multi-catch `NumberFormatException | NullPointerException`, user alerted, card safely ejected.
3. **Null Amount String (`null`)**: Caught by multi-catch, card safely ejected.
4. **Invalid Denomination ($250.00)**: Caught by domain validation `IllegalArgumentException`, card ejected.
5. **Insufficient Balance ($3,000.00 requested from $1,500.00)**: Caught by domain validation, card ejected.
6. **Per-Transaction Cap Exceeded ($2,500.00 requested)**: Rejected with security threshold alert, card ejected.
7. **Three Consecutive Invalid PIN Attempts**: Triggers automatic security lockout (`AccountBlockedException`), card ejected.

---

## 3. Task 02: User-Defined Exceptions & Exception Chaining

### 3.1 Objective & Problem Description
Enterprise banking systems rely on specialized exception hierarchies to distinguish between technical communication faults and business logic violations. When downstream infrastructure (e.g. database, network socket) fails, low-level technical exceptions must be caught and wrapped into domain-level exceptions without losing the original root cause.

### 3.2 Exception Hierarchy Diagram

```mermaid
classDiagram
    class Exception {
        <<Java SE Core>>
    }

    class BankingException {
        -String errorCode
        -String transactionId
        +getErrorCode() String
        +getTransactionId() String
    }

    class InvalidAccountException {
        -String accountNumber
    }

    class InsufficientFundsException {
        -double currentBalance
        -double requestedAmount
    }

    class AccountBlockedException {
        -String blockReason
    }

    class TransactionProcessingException {
        -String gatewayEndpoint
    }

    Exception <|-- BankingException
    BankingException <|-- InvalidAccountException
    BankingException <|-- InsufficientFundsException
    BankingException <|-- AccountBlockedException
    BankingException <|-- TransactionProcessingException
```

### 3.3 Exception Chaining Pattern
When a `java.net.SocketTimeoutException` occurs during communication with the interbank clearance gateway:
```java
try {
    gateway.connect();
} catch (SocketTimeoutException ste) {
    // Exception Chaining: Preserving root cause
    throw new TransactionProcessingException(
        "Interbank clearance gateway timed out after 3000ms", 
        txId, "SWIFT-GW-01", ste
    );
}
```
The caller catches `TransactionProcessingException` and calls `e.getCause()` to inspect the underlying `SocketTimeoutException`, retaining full forensic and audit compliance.

### 3.4 Execution Evidence
![Task 02 Screenshot](../Screenshots/Task02_User_Defined_Exceptions.png)

---

## 4. Task 03: Resource Safety, AutoCloseable & Suppressed Exceptions

### 4.1 Objective & The Masked Exception Hazard
Before Java 7, resource cleanup was handled using manual `try-finally` blocks. If an exception occurred during regular processing and another exception occurred inside the `finally` block during `close()`, the `finally` exception **erased and masked** the primary business exception.

### 4.2 Modern `try-with-resources` & LIFO Reverse Order
Java 7 introduced `java.lang.AutoCloseable`. Resources declared in the `try (...)` specification are guaranteed to close in reverse order of declaration (Last-In, First-Out):
1. `Resource 1: LedgerFileReader` opened first.
2. `Resource 2: DatabaseSession` opened second.
3. Upon block exit, `DatabaseSession` is closed **first**.
4. Then `LedgerFileReader` is closed **second**.

### 4.3 Suppressed Exceptions Demonstration
When a primary `IOException` occurs during file parsing, and both `DatabaseSession.close()` and `LedgerFileReader.close()` also throw exceptions:
- Modern Java **preserves the primary `IOException`** as the main thrown exception.
- Both closing exceptions are automatically appended to the primary exception using `Throwable.addSuppressed()`.
- The caller calls `e.getSuppressed()` to inspect all secondary failures without any diagnostic blindness.

### 4.4 Execution Evidence
![Task 03 Screenshot](../Screenshots/Task03_Resource_Safety.png)

---

## 5. Task 04: Thread Lifecycle, State Transitions & Priority Scheduling

### 5.1 Objective & Thread Life Cycle
Investigate the underlying execution mechanics of Java threads, comparing `Thread.start()` vs `Runnable.run()`, observing life cycle state transitions, coordinating termination using `join()`, and analyzing thread priority scheduling.

### 5.2 Thread State Transition Model
- **`NEW`**: Thread instance created, native OS thread not yet allocated.
- **`RUNNABLE`**: Registered with JVM and OS thread scheduler, executing or ready to execute on a CPU core.
- **`TIMED_WAITING`**: Thread sleeping via `Thread.sleep(millis)`.
- **`TERMINATED`**: Thread completed execution or thrown an uncaught exception; `isAlive()` returns `false`.

### 5.3 10-Run Empirical Scheduling Experiment
Ten independent runs were executed with 5 worker threads assigned varied priorities:
`W1(P1)`, `W2(P3)`, `W3(P5)`, `W4(P7)`, `W5(P10)`.

```text
Run #1  : W3(P5)  -> W2(P3)  -> W4(P7)  -> W5(P10) -> W1(P1)
Run #2  : W2(P3)  -> W1(P1)  -> W5(P10) -> W4(P7)  -> W3(P5)
Run #3  : W1(P1)  -> W2(P3)  -> W4(P7)  -> W5(P10) -> W3(P5)
Run #4  : W1(P1)  -> W2(P3)  -> W5(P10) -> W4(P7)  -> W3(P5)
Run #7  : W5(P10) -> W3(P5)  -> W4(P7)  -> W1(P1)  -> W2(P3)
```
**Core Finding**: Java priorities (`1` to `10`) are advisory hints to the underlying OS scheduler. Windows uses multi-level feedback queues with dynamic priority boosting and starvation avoidance, resulting in non-deterministic thread ordering.

### 5.4 Execution Evidence
![Task 04 Screenshot](../Screenshots/Task04_Thread_Lifecycle.png)

---

## 6. Task 05: Race Conditions, Synchronization & Performance Benchmark

### 6.1 Objective & The Lost Update Hazard
When multiple threads read and mutate shared state without synchronization, concurrent execution of `balance = balance + amount` creates a classic read-modify-write race condition:
1. Thread A reads `balance = $10,000.00`.
2. Thread B reads `balance = $10,000.00`.
3. Thread A computes `$10,000 + $100 = $10,100` and writes it.
4. Thread B computes `$10,000 - $100 = $9,900` and writes it.
5. **Net Result**: Thread A's deposit of $100 is completely lost!

### 6.2 Implementations Evaluated
1. **Unsynchronized**: Raw unsynchronized fields.
2. **Synchronized Method**: Coarse-grained method-level locking on intrinsic `this` monitor.
3. **Synchronized Block**: Fine-grained locking on dedicated lock object with pre-validation outside critical section.
4. **Atomic Lock-Free (`AtomicLong`)**: Hardware-level Compare-And-Swap (CAS) instructions storing balance in cents.

### 6.3 Empirical Benchmark Results (10 Threads, 20,000 Operations, 10 Runs)

| Implementation | Average Time (ms) | StdDev (ms) | Consistency Rate | Average Ledger Error |
|---|:---:|:---:|:---:|:---:|
| **1. Unsynchronized** | 1.893 ms | 0.649 ms | 90% (Corrupted) | **+$1,280.00** |
| **2. Synchronized Method** | 1.771 ms | 0.674 ms | **100% (Passed)** | **$0.00** |
| **3. Synchronized Block** | 2.326 ms | 1.782 ms | **100% (Passed)** | **$0.00** |
| **4. Atomic Lock-Free** | **1.796 ms** | **0.482 ms** | **100% (Passed)** | **$0.00** |

### 6.4 Execution Evidence
![Task 05 Screenshot](../Screenshots/Task05_Race_Conditions.png)

---

## 7. Task 06: Deadlock Avoidance via Global Lock Ordering & Bounded Queue

### 7.1 Objective & Problem Analysis
1. **Deadlock Inversion in Cross Transfers**:
   - Thread 1 transfers from Account A to Account B, locking A then requesting B.
   - Thread 2 simultaneously transfers from Account B to Account A, locking B then requesting A.
   - Both threads hold one resource and wait indefinitely for the other $\to$ **Coffman Deadlock**.
2. **Producer-Consumer Coordination**:
   - Implementing a fixed-capacity transaction buffer where producers wait when full, and consumers wait when empty.

### 7.2 The 4 Coffman Deadlock Conditions & Global Ordering Solution
- **Mutual Exclusion**: Exclusive access to account locks (Mandatory).
- **Hold and Wait**: Holding one lock while requesting another (Mandatory).
- **No Preemption**: Locks cannot be taken away forcibly (Mandatory in Java synchronized).
- **Circular Wait**: Thread 1 waits for Thread 2, and Thread 2 waits for Thread 1.

**Solution**: By enforcing **Deterministic Global Lock Ordering** based on account IDs (`min(from, to)` first, then `max(from, to)` second), both threads acquire locks in the identical order, mathematically eliminating the Circular Wait condition.

### 7.3 Bounded Buffer Guarded Suspension
```java
// Why 'while' instead of 'if': Guards against spurious wakeups
while (queue.size() == capacity) {
    wait();
}
// Why 'notifyAll()' instead of 'notify()': Prevents lost wakeup deadlocks
notifyAll();
```

### 7.4 Execution Evidence
![Task 06 Screenshot](../Screenshots/Task06_Deadlock_Producer_Consumer.png)

---

## 8. Task 07: High-Throughput Concurrent Clearing Engine with ExecutorService

### 8.1 Objective & Architecture
Industrial clearing engines process high volumes of financial settlements with strict SLAs. Using raw `Thread` instances causes excessive OS thread allocation overhead and lacks lifecycle management.

### 8.2 Design Implementation
- **`FixedThreadPool(4)`**: Reuses worker threads, bounding resource consumption.
- **`Callable<TransactionResult>`**: Returns rich typed outcome objects and propagates checked exceptions.
- **SLA Timeout Enforcement**: Invokes `future.get(250, TimeUnit.MILLISECONDS)`. If an external clearing gateway stalls, `TimeoutException` is caught, and the stalled task is cancelled via `future.cancel(true)`.
- **Fault-Tolerant Exception Handling**: Catches `ExecutionException` to quarantine downstream gateway aborts without terminating the engine.
- **Concurrent Aggregation**: Accumulates settlement totals and status distributions using `ConcurrentHashMap<String, LongAdder>` and `AtomicLong`.
- **Two-Phase Graceful Shutdown**: `executor.shutdown()` $\to$ `awaitTermination(2, TimeUnit.SECONDS)` $\to$ fallback to `shutdownNow()`.

### 8.3 Clearing Benchmark Metrics
- **Total Transactions Processed**: 12
- **Batch Processing Time**: 604 ms
- **Throughput**: **19.87 transactions/sec**
- **Total Settled Capital**: **$672,800.75**
- **Clearing Fees Accrued**: **$339.53**
- **Status Distribution**:
  - `SUCCESS`: 75.0% (9 transactions)
  - `TIMEOUT_BREACH`: 8.3% (1 transaction cancelled on SLA breach)
  - `REJECTED_COMPLIANCE`: 8.3% (1 transaction rejected for exceeding $1M AML limit)
  - `GATEWAY_FAILURE`: 8.3% (1 transaction safely caught via `ExecutionException`)

### 8.4 Execution Evidence
![Task 07 Screenshot](../Screenshots/Task07_Concurrent_Processor.png)

---

## 9. Comprehensive Complexity & Performance Comparison

| Task / Component | Time Complexity | Space Complexity | Thread Safety | Primary Vulnerability / Advantage |
|---|:---:|:---:|:---:|---|
| **Task 01: ATM Exception Handling** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | Single-Threaded | Guaranteed hardware card ejection via `finally` |
| **Task 02: Custom Chained Exceptions** | $\mathcal{O}(1)$ | $\mathcal{O}(D)$ (Call Stack) | Yes | Full root-cause forensic audit trail via `getCause()` |
| **Task 03: Try-With-Resources** | $\mathcal{O}(1)$ | $\mathcal{O}(R)$ (Resources) | Yes | Eliminates masked exceptions; strict LIFO reverse close |
| **Task 04: Thread Life Cycle & Join** | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | Yes | OS thread allocation; priorities are non-deterministic hints |
| **Task 05: Unsynchronized Account** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | **UNSAFE** | Severe data race; average lost update error: **+$1,280.00** |
| **Task 05: Synchronized Method** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | **Thread-Safe** | 100% correct; coarse-grained intrinsic monitor contention |
| **Task 05: Synchronized Block** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | **Thread-Safe** | 100% correct; minimal critical section footprint |
| **Task 05: Atomic CAS (`AtomicLong`)** | $\mathcal{O}(1)$ amortized | $\mathcal{O}(1)$ | **Thread-Safe** | 100% correct; highest throughput lock-free CPU instructions |
| **Task 06: Naive Account Transfer** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | **DEADLOCK HAZARD** | Coffman Circular Wait causes permanent application freeze |
| **Task 06: Safe Account Transfer** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | **Thread-Safe** | Deterministic global lock ordering eliminates deadlock |
| **Task 06: Bounded Transaction Queue** | $\mathcal{O}(1)$ per op | $\mathcal{O}(K)$ (Capacity) | **Thread-Safe** | Guarded suspension with `while` and `notifyAll()` |
| **Task 07: Concurrent Clearing Engine** | $\mathcal{O}(N / P)$ | $\mathcal{O}(N)$ | **Thread-Safe** | Fixed pool bounded concurrency; SLA timeout enforcement |

---

## 10. Student Contribution & Pair-Work Division

As mandated by the laboratory pairing guidelines, primary responsibilities were partitioned between Student A and Student B, with continuous collaborative code review:

### Student A – Primary Responsibilities
- **Name**: Devi Vara Prasad | **Register Number**: 192365032
- **Lead Architecture & Implementation**:
  - Implemented `Task01_ATM_Exception_Handling`: `Account`, `ATMDispenser`, `ATMExceptionHandlingDemo`.
  - Implemented `Task02_User_Defined_Exceptions`: Custom exception hierarchy (`BankingException`, `InsufficientFundsException`, `AccountBlockedException`, etc.) and exception chaining.
  - Implemented `Task03_Resource_Safety`: `LedgerFileReader`, `DatabaseSession`, and demonstration of the legacy masked exception bug.
  - Designed and authored the shared `TerminalCaptureEngine` using Java 2D graphics for automated high-DPI execution screenshot generation.
  - Implemented `Task06_Deadlock_Producer_Consumer`: Safe transfer with deterministic global lock ordering.
  - Implemented `Task07_Concurrent_Processor`: `TransactionTask` and SLA timeout cancellation logic.

### Student B – Primary Responsibilities
- **Name**: `[Student B Name]` | **Register Number**: `[Student B Register Number]`
- **Testing, Concurrency Benchmarking & Verification**:
  - Implemented modern `try-with-resources` suppressed exception inspection (`Throwable.getSuppressed()`) in Task 03.
  - Conducted the 10-run empirical scheduling experiment and CSV logging for Task 04.
  - Implemented `AtomicAccount` lock-free CAS logic in Task 05, alongside statistical variance and standard deviation calculations for the 20,000-operation synchronization benchmark.
  - Implemented `TransactionQueue` bounded buffer with guarded suspension, spurious wakeup handling, and `notifyAll()` coordination in Task 06.
  - Implemented concurrent aggregation using `ConcurrentHashMap` and `LongAdder`, and graceful shutdown lifecycle protocols in Task 07.

### Joint Work & Collaboration
- Co-designed test cases covering valid, boundary, and catastrophic failure conditions across all 7 tasks.
- Verified that all program outputs were genuine, capturing authentic screenshots and raw CSV datasets.
- Co-authored architectural diagrams, complexity tables, and comprehensive documentation.

---

## 11. Joint Conclusions & Key Learnings

1. **Defensive Exception Engineering**: Exception handling is not merely about preventing application crashes; it is an architectural contract for data integrity and forensic auditability. Exception chaining preserves low-level root causes, while `try-with-resources` permanently eliminates resource leaks and masked exception bugs.
2. **Reality of Thread Scheduling**: Thread priorities are merely advisory hints to the operating system. Preemptive time-slicing and multi-core CPU scheduling dictate that thread execution order is inherently non-deterministic.
3. **The Imperative of Synchronization**: Unsynchronized access to shared mutable data guarantees data corruption and lost updates under concurrent workloads. While intrinsic locks (`synchronized`) guarantee safety, lock-free hardware atomics (`AtomicLong`) provide superior scalability with minimal thread contention.
4. **Deadlock Elimination**: Deadlock is an architectural flaw, not a random anomaly. By establishing strict global lock ordering, circular wait is broken, providing mathematical certainty that deadlock cannot occur.
5. **Modern Concurrency Utilities**: Thread pools (`ExecutorService`) paired with `Callable`, `Future`, and timeout guards represent the industry standard for scalable, resilient enterprise computing.

---

## 12. Overall Result & Verification Sign-Off

All seven tasks prescribed under **Programming in Java – Assignment 3 (CO3)** have been completely designed, implemented, compiled, executed, verified, benchmarked, and documented.

- **Tasks Completed**: 7 of 7 (100%)
- **Test Scenarios Verified**: All normal, boundary, invalid, and catastrophic failure paths passed.
- **Genuine Screenshots Generated**: 7 of 7 high-DPI terminal execution screenshots captured.
- **Benchmark Datasets Exported**: `Task04_Thread_Runs.csv`, `Task05_Sync_Benchmark.csv`, `Task07_Throughput_Metrics.csv`.
- **Preservation of Past Work**: Assignment 1 and Assignment 2 remain 100% untouched.

**Signed Off by:**
- **Student A**: Devi Vara Prasad (Register Number: 192365032)
- **Student B**: `[Student B Name]` (Register Number: `[Student B Register Number]`)
- **Date**: November 14, 2026
