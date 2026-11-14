# Task 01: GUI Design Review – Diagnose & Repair a Poor Event-Handling Design

## Student Information
- **Student Name**: Devi Vara Prasad
- **Register Number**: 192365032
- **Course**: Programming in Java (CSA09) | **Course Outcome**: CO4 – Event-Driven GUI Design
- **GitHub Repository**: [https://github.com/vara12126/Java](https://github.com/vara12126/Java)

---

## 1. Problem Overview
A junior developer authored a student fee-payment window where the window itself implements `ActionListener`, routes all button actions through a monolithic `actionPerformed()` method using string comparisons, directly executes raw SQL statements, and blocks the Event Dispatch Thread (EDT) via `Thread.sleep()`.

This task diagnoses the systemic flaws of this naive implementation and presents an enterprise-grade redesign adhering to the **Separation of Concerns (SoC)** principle, asynchronous worker threading (`SwingWorker`), parameterized queries (`PreparedStatement`), dedicated event listeners, and user feedback mechanisms.

---

## 2. Task 1.1 – Diagnose the Design (9 Flaws Identified)

1. **Tight Coupling & Violation of Separation of Concerns**:
   - The UI class (`FeeWindow`) directly contains database connection strings, SQL queries, and transaction business logic.
   - *Impact*: Code cannot be unit tested independently of a live database; any database schema modification breaks UI code.
2. **Identity Comparison of String Action Commands (`==`)**:
   - `e.getActionCommand() == "Pay"` checks reference identity rather than lexical content equality (`.equals()`).
   - *Impact*: If the runtime provides a non-interned string instance, comparison evaluates to `false`, causing the "Pay" button to erroneously fall into the `else` branch and clear the user's form without paying!
3. **Blocking the Event Dispatch Thread (EDT)**:
   - Synchronous network I/O (`DriverManager.getConnection`) and `Thread.sleep(3000)` are executed directly inside `actionPerformed()`.
   - *Impact*: Freezes the single AWT/Swing Event Dispatch Thread. The operating system marks the window as "Not Responding", paints turn white, clicks are queued or dropped, and the user cannot cancel.
4. **Catastrophic SQL Injection Vulnerability**:
   - Concatenating user text into raw SQL: `"INSERT INTO fees VALUES('" + regNo.getText() + "'," + a + ")"`.
   - *Impact*: A user entering `' OR '1'='1` can manipulate database state, corrupt ledger balances, or drop tables.
5. **Absence of Input Validation**:
   - `Double.parseDouble(amount.getText())` without `try-catch` throws an unhandled `NumberFormatException` when fields are empty or non-numeric.
   - *Impact*: The program terminates the event handler abruptly with stack traces dumped only to the console; the user receives zero explanation.
6. **Severe Resource Leakage**:
   - Database `Connection` and `Statement` objects are never closed with `close()` or `try-with-resources`.
   - *Impact*: Leaks socket descriptors and exhausts the database connection pool, causing server starvation.
7. **Complete Lack of Graphical User Feedback**:
   - Status is printed to `System.out.println("Paid")`.
   - *Impact*: In a production GUI desktop app, end-users do not have console access. The user sees no visual indicator of success, failure, or confirmation reference.
8. **Broken "Receipt" Button Handling**:
   - The Receipt button falls into the `else` branch, resetting the text fields instead of viewing transaction records.
9. **Missing Window Closing Handler**:
   - AWT `Frame` does not close when clicking the window 'X' button without an explicit `WindowListener` (`WindowAdapter`).

---

## 3. Task 1.2 – Repair the Design

The redesign decouples presentation from application logic:
- **`FeePaymentDTO`**: Immutable Data Transfer Object encapsulating payment details, transaction reference, timestamp, and status.
- **`FeeService`**: Independent service class providing input validation (`validateInput`), SQL injection prevention via parameterized queries, and asynchronous database persistence.
- **`FeeWindow`**: Clean Swing interface with dedicated button listeners, non-blocking `SwingWorker` for gateway operations, progress indication, modal dialogs (`JOptionPane`), and a dedicated receipt viewer.

---

## 4. Task 1.3 – Design Defence (Under 100 Words)

> "The redesigned architecture strictly decouples presentation from business logic. The `FeeWindow` solely orchestrates user interactions, while `FeeService` encapsulates validation, transactional persistence, and security. By isolating database operations into `FeeService`, business logic can be unit-tested with mock repositories without launching a GUI harness. Offloading long-running network gateway calls to `SwingWorker` guarantees the Event Dispatch Thread remains responsive, preventing window freezes. Dedicated listener objects eliminate fragile string comparisons, and parameterized queries ensure complete immunity against SQL injection." (81 words)

---

## 5. Alternative Concept & Complexity Analysis

### Alternative Concept: Individual Button Listeners vs Monolithic `if-else` Chain
Instead of one monolithic listener checking action commands sequentially, modern Swing assigns dedicated listener objects (or `Action` / lambda expressions) to each button:
```java
btnPay.addActionListener(e -> handlePayment());
btnClear.addActionListener(e -> handleClear());
btnReceipt.addActionListener(e -> handleReceipt());
```

### Complexity Comparison:
- **Event Dispatch Time Complexity**:
  - *Monolithic Listener*: $\mathcal{O}(k)$ worst-case string comparisons for $k$ buttons.
  - *Dedicated Listeners*: $\mathcal{O}(1)$ direct polymorphic invocation by the AWT event multicaster.
- **Space Complexity**:
  - *Monolithic Listener*: $\mathcal{O}(1)$ (single listener instance sharing the frame).
  - *Dedicated Listeners*: $\mathcal{O}(k)$ listener object references (each object requiring ~24–32 bytes in JVM heap). In modern desktop hardware, this negligible memory overhead is overwhelmingly outweighed by $O(1)$ dispatch performance, compile-time type safety, and maintainability.

---

## 6. Execution Evidence & Screenshots

### GUI Window Screenshot
![Task 01 Fee Window](screenshots/Task01_Fee_Window.png)

---

## 7. How to Compile & Run

```bash
# Compile from repository root
javac -d bin -cp bin "Assignment 4/Task01_GUI_Design_Review/src/*.java" "Assignment 4/Report/*.java"

# Run the test harness and window demo
java -cp bin task01_fee.RunTask01Demo
```
