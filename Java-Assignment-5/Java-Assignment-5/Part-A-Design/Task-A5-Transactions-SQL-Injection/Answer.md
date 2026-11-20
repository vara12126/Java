# Task A5 – Design for Correctness and Safety

**Course Title:** Programming in Java (CSA09)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Bloom’s Level:** K4 – Analyse  
**Total Marks:** 4 Marks  

---

## 1. Scenario Context: Financial Integrity & Security

Database persistence in campus enterprise systems must guarantee two non-negotiable properties:
1. **Financial Correctness (ACID Transactions):** A transfer of ₹500 from a student's hostel wallet to a campus canteen wallet must never result in money vanishing into thin air or being illegally duplicated if the server crashes mid-flight.
2. **Defensive Security (SQL Injection Immunity):** User authentication queries must treat user input strictly as literal values, never permitting malicious input to rewrite the structure of the executable SQL command.

---

## A5.1 – Transaction Design (2 Marks)

### The Money Transfer Problem
A wallet transfer consists of two distinct database write operations:
- **Operation 1 (Debit):** Deduct ₹500 from the Hostel Wallet.
- **Operation 2 (Credit):** Add ₹500 to the Canteen Wallet.

If auto-commit is enabled (default in JDBC), each statement commits immediately. If a network disruption, power failure, or runtime exception occurs after Operation 1 but before Operation 2, the ₹500 is debited from the student, but never credited to the canteen. **The money is permanently lost.**

```
+-------------------------------------------------------------------------------------------------+
|                                 ATOMIC TRANSACTION LIFECYCLE                                    |
+-------------------------------------------------------------------------------------------------+
|                                                                                                 |
|   Connection.setAutoCommit(false)                                                               |
|        │                                                                                        |
|        ├───> Step 1: UPDATE wallets SET balance = balance - 500 WHERE id = 'hostel'            |
|        │             (Balance decremented in transaction buffer; NOT yet finalized)             |
|        │                                                                                        |
|        ├───> Step 2: UPDATE wallets SET balance = balance + 500 WHERE id = 'canteen'           |
|        │             (Balance incremented in transaction buffer; NOT yet finalized)             |
|        │                                                                                        |
|   [ SUCCESS? ] ─── YES ───> Connection.commit()   (Both updates finalized atomically)           |
|        │                                                                                        |
|       NO (Exception) ─────> Connection.rollback() (Both updates discarded; 0 change)            |
|                                                                                                 |
+-------------------------------------------------------------------------------------------------+
```

---

### SIMATS Algorithmic Pseudocode: Atomic Wallet Transfer

```text
ALGORITHM TransferWalletFunds(connectionPool, studentId, transferAmount)
    INPUT:  connectionPool: DataSource
            studentId: STRING
            transferAmount: DOUBLE (500.00)
    OUTPUT: isSuccess: BOOLEAN

    BEGIN
        DECLARE conn: Connection
        DECLARE stmtDebit: PreparedStatement
        DECLARE stmtCredit: PreparedStatement
        DECLARE oldAutoCommit: BOOLEAN
        DECLARE rowsDebited: INTEGER
        DECLARE rowsCredited: INTEGER

        TRY
            conn ← connectionPool.GET_CONNECTION()
            
            // 1. Record original auto-commit state and disable it
            oldAutoCommit ← conn.GET_AUTO_COMMIT()
            conn.SET_AUTO_COMMIT(FALSE)

            // 2. Prepare Debit statement with balance validation check
            stmtDebit ← conn.PREPARE_STATEMENT(
                "UPDATE hostel_wallet SET balance = balance - ? WHERE student_id = ? AND balance >= ?"
            )
            stmtDebit.SET_DOUBLE(1, transferAmount)
            stmtDebit.SET_STRING(2, studentId)
            stmtDebit.SET_DOUBLE(3, transferAmount)
            rowsDebited ← stmtDebit.EXECUTE_UPDATE()

            IF rowsDebited = 0 THEN
                // Insufficient funds or invalid student account
                conn.ROLLBACK()
                PRINT("Transfer failed: Insufficient wallet balance or invalid ID.")
                RETURN FALSE
            ENDIF

            // 3. Prepare Credit statement
            stmtCredit ← conn.PREPARE_STATEMENT(
                "UPDATE canteen_wallet SET balance = balance + ? WHERE student_id = ?"
            )
            stmtCredit.SET_DOUBLE(1, transferAmount)
            stmtCredit.SET_STRING(2, studentId)
            rowsCredited ← stmtCredit.EXECUTE_UPDATE()

            IF rowsCredited = 0 THEN
                conn.ROLLBACK()
                PRINT("Transfer failed: Canteen account not reachable.")
                RETURN FALSE
            ENDIF

            // 4. Both steps succeeded: Commit atomically
            conn.COMMIT()
            PRINT("Successfully transferred ₹" + STRING(transferAmount))
            RETURN TRUE

        CATCH Exception ex THEN
            // 5. In case of ANY hardware or software fault, undo all changes
            IF conn ≠ NULL THEN
                TRY
                    conn.ROLLBACK()
                    PRINT("System fault detected. Transaction rolled back completely.")
                CATCH SQLException rbEx THEN
                    PRINT("Rollback error: " + rbEx.MESSAGE)
                ENDTRY
            ENDIF
            RETURN FALSE

        FINALLY
            // 6. Guarantee restoration of original connection configuration
            IF conn ≠ NULL THEN
                conn.SET_AUTO_COMMIT(oldAutoCommit)
                conn.CLOSE()
            ENDIF
        ENDTRY
    END
```

### How Transactions Guarantee Financial Conservation
Transactions enforce the **ACID** properties:
- **Atomicity ("All or Nothing"):** Either both the debit and credit succeed together, or neither takes effect.
- **Consistency:** The total circulating balance before the transfer ($\text{Hostel} + \text{Canteen}$) strictly equals the total balance after the transfer. Money cannot be created or destroyed.

---

## A5.2 – Injection Analysis & Safe Redesign (2 Marks)

### Analysis of the Vulnerable Query
The application executes the following query string constructed via string concatenation:
```java
String sql = "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + pwd + "';";
```

### Mechanism of the Exploit
In dynamic SQL concatenation, user-supplied text is concatenated directly into the command string before the SQL compiler parses it. This allows an attacker to inject SQL syntax keywords, altering the query logic.

#### Attack Scenario 1: Authentication Bypass via Boolean Tautology
- **Attacker Input:**
  - `id` = `admin`
  - `pwd` = `' OR '1'='1`
- **Resulting Executed Query:**
  ```sql
  SELECT * FROM users WHERE id='admin' AND pwd='' OR '1'='1';
  ```
- **Operator Precedence & Evaluation:**
  1. In SQL, `AND` takes precedence over `OR`.
  2. Sub-clause 1: `(id='admin' AND pwd='')` evaluates to `FALSE`.
  3. Sub-clause 2: `'1'='1'` evaluates to `TRUE`.
  4. Final condition: `FALSE OR TRUE` $\rightarrow$ **`TRUE`**!
  5. The database returns the `admin` user row without requiring the valid password. The attacker is immediately logged in as the system administrator!

#### Attack Scenario 2: Comment Truncation
- **Attacker Input:**
  - `id` = `admin' -- `
  - `pwd` = `anything`
- **Resulting Executed Query:**
  ```sql
  SELECT * FROM users WHERE id='admin' -- ' AND pwd='anything';
  ```
- **Evaluation:** The SQL engine interprets `-- ` as a single-line comment, completely ignoring the password check (`AND pwd='...'`).

---

### Secure Redesign Using `PreparedStatement`

```java
public boolean safeLogin(Connection con, String id, String pwd) throws SQLException {
    // 1. Define parameterized template with '?' placeholders
    String sql = "SELECT user_id, full_name, role FROM users WHERE user_id = ? AND password_hash = ?";
    
    // 2. Database compiles the SQL command structure FIRST
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        // 3. Inputs are bound as pure literal values, NEVER as executable SQL code
        ps.setString(1, id);
        ps.setString(2, pwd);
        
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next(); // True if credentials match exactly
        }
    }
}
```

#### Why `PreparedStatement` Completely Neutralizes Injection
When using `PreparedStatement`:
1. The database receives the SQL string with `?` markers and compiles the **Abstract Syntax Tree (AST)** and execution plan *before* any data values are received.
2. The parameter values (`id`, `pwd`) are transmitted across the JDBC protocol as distinct typed binary data packets.
3. If an attacker inputs `' OR '1'='1`, the database looks for a user whose literal password string in the column literally equals the characters `' OR '1'='1`. It is impossible for user data to alter the command grammar.

---

## Alternative Concept and Complexity Analysis: Auto-Commit vs Transaction & PreparedStatement vs Input Escaping

### Part 1: Two Independent Auto-Commit Updates vs Single Transaction

| Evaluation Metric | Two Independent Auto-Commit Updates | Single Atomic Transaction (`commit`/`rollback`) |
|:---|:---|:---|
| **Data Correctness Risk** | **Severe:** If crash occurs between updates, funds disappear permanently | **Zero Risk:** Atomicity guarantees state rollback on failure |
| **Disk I/O Cost (WAL Fsync)**| **2 Separate Fsyncs:** Each auto-commit statement forces a synchronous flush to the redo log | **1 Single Fsync:** All changes are flushed to the redo log once at `commit()` |
| **Network Round-Trip Time**| 2 round-trips to database server | 2 round-trips for updates + 1 lightweight commit token |
| **Total Execution Latency** | Higher due to dual physical disk writes ($\approx 15 - 25\text{ ms}$) | Lower or equal due to batched redo log flushing ($\approx 8 - 12\text{ ms}$) |

---

### Part 2: PreparedStatement vs String Escaping / Regex Filtering

An alternative often attempted by inexperienced developers is passing input through a regular expression or replacing single quotes:
```java
String sanitizedId = id.replaceAll("'", "''").replaceAll("--", "");
```

#### Why Sanitization & Regex Filtering is Weaker
1. **Bypass Vulnerabilities:** Attackers easily bypass naive sanitizers using double-encoding, Unicode normalization tricks (e.g., full-width apostrophes), multibyte character truncation (e.g., GBK / Big5 character sets where `0xbf27` transforms into a valid character plus a single quote), or alternative SQL syntax (`/* ... */`, `CHAR(39)`).
2. **Maintenance Fragility:** Every database dialect (MySQL, PostgreSQL, Oracle, SQLite) has different string escape conventions, comment markers, and whitespace interpretations.
3. **Execution Overhead:** Running complex regex pattern matchers on every request consumes significant CPU time ($O(M \times K)$ where $M$ is string length and $K$ is regex state machine depth).
4. **PreparedStatement Advantage:** In addition to 100% mathematical immunity to injection, `PreparedStatement` enables **statement caching** in the MySQL server and client driver. Once compiled, repeated queries execute in $O(1)$ preparation time without reparsing.
