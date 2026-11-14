# Task 02: User-Defined Exceptions and Exception Chaining

## 1. Problem Statement
Implement an enterprise-grade Online Banking and Fund Transfer validation service demonstrating:
- Defining custom checked exceptions extending `Exception`.
- Method `throws` declarations and layered exception propagation (`throw`).
- Rich domain-specific diagnostic state encapsulated within custom exceptions.
- **Exception Chaining**: wrapping low-level system faults (`SocketTimeoutException`, `SQLException`) inside domain-level `TransactionProcessingException` using `Throwable(String, Throwable)` without losing original root cause stack traces (`getCause()`).

---

## 2. Source Code Architecture
- `BankingException.java`: Abstract base checked exception defining `errorCode` and ISO timestamps.
- `InsufficientFundsException.java`: Encapsulates `accountNumber`, `requestedAmount`, `currentBalance`, and `shortfall`.
- `AccountBlockedException.java`: Encapsulates `reason` and `riskScore`.
- `InvalidAccountException.java`: Encapsulates bad account patterns or missing accounts.
- `TransactionProcessingException.java`: Demonstrates exception chaining by preserving low-level root causes.
- `FundTransferService.java`: Business service validating transfers and throwing custom exceptions.
- `UserDefinedExceptionDemo.java`: Client driver demonstrating all 6 test scenarios.

---

## 3. Compilation & Execution Instructions

```powershell
# From repository root (Java/)
$src = (Get-ChildItem -Path "Assignment 3\Task02_User_Defined_Exceptions\src\*.java").FullName
& 'C:\Program Files\Java\jdk-25.0.2\bin\javac.exe' -d bin -cp bin $src
& 'C:\Program Files\Java\jdk-25.0.2\bin\java.exe' -cp bin task02_exceptions.UserDefinedExceptionDemo
```

---

## 4. Test Cases & Verification

| Test Case | Scenario Description | Expected Custom Exception | Key Attributes Inspected |
| :--- | :--- | :--- | :--- |
| **TC 1: Valid Transfer** | Transfer \$500 between active accounts | None (SUCCESS) | Funds debited/credited |
| **TC 2: Invalid Format** | Source account: `BAD-ACCT-9` | `InvalidAccountException` | Format regex validation |
| **TC 3: Non-existent** | Destination: `ACC-9999` | `InvalidAccountException` | Ledger lookup missing |
| **TC 4: Account Frozen** | Transfer from `ACC-3003` | `AccountBlockedException` | AML Risk Score (92/100) |
| **TC 5: Insufficient Funds**| Transfer \$10,000 from \$1,700 bal | `InsufficientFundsException`| Shortfall: \$8,300.00 |
| **TC 6: Chained Exception** | Payment gateway timeout | `TransactionProcessingException`| Root Cause: `SocketTimeoutException` |

---

## 5. Execution Screenshot
![Task02_User_Defined_Exceptions](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%203/Task02_User_Defined_Exceptions/screenshots/Task02_User_Defined_Exceptions.png)

---

## 6. Pair-Work Responsibilities
- **Student A (Devi Vara Prasad - 192365032)**: Designed custom exception hierarchy, implemented base `BankingException`, `InsufficientFundsException`, and `AccountBlockedException`.
- **Student B ([Student B Name] - [Student B Register Number])**: Implemented `TransactionProcessingException` with exception chaining, `FundTransferService` validation, and test harness.
- **Joint Work**: Verified that `e.getCause()` properly surfaces the original `SocketTimeoutException` and preserved the chained stack trace across layers.
