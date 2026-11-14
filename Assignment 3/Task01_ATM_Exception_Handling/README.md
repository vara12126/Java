# Task 01: ATM Exception Handling System

## 1. Problem Statement
Design and implement an ATM Cash Withdrawal and Transaction Processing System in Java demonstrating robust exception handling principles:
- Java Exception hierarchy (`Throwable`, `Exception`, `RuntimeException`).
- Multi-catch syntax: `catch (NumberFormatException | NullPointerException e)`.
- Proper ordering of catch blocks: specific exceptions before general `Exception`.
- Mandatory `finally` block: guaranteed resource cleanup, card ejection, network session termination, and audit trail finalization regardless of whether an exception occurs.
- Security thresholds: account lockout after 3 consecutive failed PIN attempts.

---

## 2. Source Code Architecture
- `Account.java`: Encapsulates account details, PIN verification, failed attempt counters, balance debits, and lockout logic.
- `ATMDispenser.java`: Models hardware cash dispenser, enforcing denomination checks (multiples of $100), per-transaction limits ($5,000), and vault liquidity.
- `ATMExceptionHandlingDemo.java`: Interactive driver demonstrating multi-catch, specific catches, and finally cleanup.

---

## 3. Compilation & Execution Instructions

```powershell
# From repository root (Java/)
$src = (Get-ChildItem -Path "Assignment 3\Task01_ATM_Exception_Handling\src\*.java").FullName
& 'C:\Program Files\Java\jdk-25.0.2\bin\javac.exe' -d bin -cp bin $src
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin task01_atm.ATMExceptionHandlingDemo
```

---

## 4. Test Cases & Verification

| Test Case | Input (PIN, Amount) | Expected Exception / Outcome | Catch Mechanism | Finally Executed? |
| :--- | :--- | :--- | :--- | :--- |
| **TC 1: Valid Withdrawal** | `PIN: 4321, Amount: $500` | Cash dispensed; Balance \$1,500 | Normal flow | YES (Card ejected) |
| **TC 2: Non-numeric Amount**| `PIN: 4321, Amount: 500XYZ` | `NumberFormatException` | Multi-catch | YES (Card ejected) |
| **TC 3: Null Amount** | `PIN: 4321, Amount: null` | `NullPointerException` | Multi-catch | YES (Card ejected) |
| **TC 4: Invalid Denomination**| `PIN: 4321, Amount: $250` | `IllegalArgumentException` | Specific catch | YES (Card ejected) |
| **TC 5: Insufficient Balance**| `PIN: 4321, Amount: $3,000` | `ArithmeticException` | Specific catch | YES (Card ejected) |
| **TC 6: Exceeds Limit** | `PIN: 4321, Amount: $6,000` | `IllegalArgumentException` | Specific catch | YES (Card ejected) |
| **TC 7: 3 Failed PINs** | `PINs: 0000, 2222, 3333` | `SecurityException` -> Account Locked | Security catch | YES (Card ejected) |

---

## 5. Execution Screenshot
![Task01_ATM_Exception_Handling](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%203/Task01_ATM_Exception_Handling/screenshots/Task01_ATM_Exception_Handling.png)

---

## 6. Pair-Work Responsibilities
- **Student A (Devi Vara Prasad - 192365032)**: Designed `Account` entity, implemented multi-catch block and `finally` session cleanup logic.
- **Student B ([Student B Name] - [Student B Register Number])**: Implemented `ATMDispenser` hardware checks, denomination validation, and test suite execution.
- **Joint Work**: Validated exception catch block ordering and verified that card ejection is guaranteed in all 7 test cases.
