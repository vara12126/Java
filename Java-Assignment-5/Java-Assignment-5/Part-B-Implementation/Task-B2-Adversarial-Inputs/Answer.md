# Task B2 – Engineer an Adversarial Input

**Course Title:** Programming in Java (CSA09)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Bloom’s Level:** K4 – Analyse  
**Total Marks:** 5 Marks  

---

## 1. Objective and Setup

Task B2 assesses defensive programming under adversarial input conditions across two distinct domains:
1. **Relational Database Authentication:** Comparing dynamic SQL string concatenation with parameterized `PreparedStatement`.
2. **File Storage Ingestion:** Evaluating flat-file parsing resilience against corrupted, malformed, and adversarial inputs.

A dedicated local MySQL database `assignment5_testdb` was created and populated with non-sensitive dummy test accounts:
- `('admin', 'Secret@123', 'Campus Administrator', 'ADMIN')`
- `('192311094', 'Dhanush@2026', 'Kattiripalli Dhanush', 'STUDENT')`
- `('faculty01', 'Prof@Pass99', 'Dr. S. Raman', 'FACULTY')`

---

## B2.1 – Adversarial SQL Injection Demonstration (Measured Evidence)

### Comparative Execution Test Results Table

| Test Case | ID Input | Password Input | Unsafe Concatenation Behavior | PreparedStatement Behavior | Security Evaluation |
|:---:|:---|:---|:---|:---|:---:|
| **TC-1: Legitimate Login** | `admin` | `Secret@123` | **SUCCESS** (Logged in as Admin) | **SUCCESS** (Logged in as Admin) | Baseline verification; valid credentials pass in both. |
| **TC-2: Invalid Password** | `admin` | `WrongPassword` | **REJECTED** | **REJECTED** | Normal rejection when invalid credentials supplied. |
| **TC-3: Boolean Tautology** | `admin` | `' OR '1'='1` | **VULNERABLE: SUCCESS** (Logged in as Student) | **SECURE: REJECTED** | Unsafe evaluates `'1'='1'` as true; PreparedStatement treats as literal password string. |
| **TC-4: Comment Truncation** | `admin' -- ` | `arbitrary_garbage` | **VULNERABLE: SUCCESS** (Logged in as Admin) | **SECURE: REJECTED** | Unsafe treats `-- ` as SQL comment, removing password check; PreparedStatement looks for user literally named `admin' -- `. |
| **TC-5: Universal Dump** | `' OR 1=1 -- ` | `dummy` | **VULNERABLE: SUCCESS** (Logged in as first row) | **SECURE: REJECTED** | Bypasses both user ID and password constraints entirely in unsafe query. |

### Technical Analysis of Why Unsafe Fails and Safe Succeeds
1. **Unsafe String Concatenation:**
   ```sql
   SELECT * FROM users WHERE id='admin' AND pwd='' OR '1'='1';
   ```
   The SQL parser compiles the query **after** user text is injected. Because `AND` binds tighter than `OR`, the query evaluates as `(id='admin' AND pwd='') OR ('1'='1')`. Since `'1'='1'` is unconditionally true, every record in the table matches.
2. **PreparedStatement Immunity:**
   ```sql
   SELECT * FROM users WHERE id = ? AND pwd = ?
   ```
   The database parses and compiles the query structure into an Abstract Syntax Tree (AST) **before** parameters are received. User inputs are transmitted via binary protocol packets as raw literal values. Quotes, comments, and SQL keywords are treated literally without affecting query grammar.

---

## B2.2 – Adversarial Flat-File Ingestion Tests

Using `DamagedFileTest.java`, the `PassStore` implementation was subjected to corrupted and malformed inputs:

| Adversarial Input | Injected Line Content | Observed Program Response | Defense Mechanism |
|:---:|:---|:---|:---|
| **Truncated Record** | `P1002,192311002,TRUNCATED_RECORD` (3 fields) | Safely skipped; returns `null`. | Length guard (`parts.length != 5`) prevents `ArrayIndexOutOfBoundsException`. |
| **Non-Numeric Fare** | `P1004,192311004,Karthik,12,FREE_SCHOLARSHIP` | Safely skipped during conversion. | Exception handling around `Double.parseDouble()` prevents process termination. |
| **Empty / Blank Lines** | `   \n\n\t\n` | Skipped automatically. | `line.trim().isEmpty()` check discards whitespace traps. |
| **Extra Fields** | `P1005,192311005,Manoj,14,5000.0,EXTRA1,EXTRA2` | Safely skipped. | Strict token boundary validation ensures data structure conformity. |

### What Each Adversarial Input Reveals About Flat-File Design
- **Lack of Schema Enforcement:** Unlike relational databases, raw text files have no inherent type system or schema constraints. A text editor or buggy script can insert corrupted rows undetected.
- **Vulnerability to Unhandled Exceptions:** A naive parser without defensive guards crashes immediately on malformed data, causing denial-of-service for all subsequent operations.

---

## Alternative Concept and Complexity Analysis: PreparedStatement vs Input Escaping / Regex

### The Sanitization Alternative
Instead of `PreparedStatement`, some developers attempt to sanitize inputs:
```java
String safeId = id.replace("'", "''").replaceAll("--", "");
String safePwd = pwd.replace("'", "''");
String sql = "SELECT * FROM users WHERE id='" + safeId + "' AND pwd='" + safePwd + "';";
```

### Why Escaping and Regex Validation are Fundamentally Weaker
1. **Encoding and Character Set Bypasses:** In multibyte character encodings (e.g., GBK, Big5, SJIS), attackers inject byte sequences like `0xbf27`. When the escaping function inserts a backslash (`0x5c`), the sequence `0xbf5c` is interpreted as a valid multibyte Chinese character, leaving the single quote (`0x27`) unescaped and active!
2. **SQL Dialect Differences:** Different database engines support alternate comment characters (`#`, `/* */`), alternative string literal delimiters, and backslash escape rules. A regex written for one database fails on another.
3. **Regex Denial of Service (ReDoS):** Complex nested regular expressions can be exploited using backtracking payloads, causing CPU utilization to spike to 100%.

### Performance and Security Comparison

| Metric | PreparedStatement (Parameterized) | Regex Sanitization + Dynamic SQL |
|:---|:---|:---|
| **Security Guarantee** | **100% Mathematically Immune** to SQL Injection | **Fragile / Partial:** Prone to bypasses and zero-days |
| **Query Parsing Cost** | $O(1)$ amortized (AST compiled once and cached) | $O(M)$ on every request (database re-parses raw SQL every time) |
| **Client Validation Cost** | Zero client regex overhead | $O(L \times K)$ client CPU regex state machine overhead |
| **Network Wire Protocol** | Compact typed binary protocol | Verbose ASCII SQL strings |
