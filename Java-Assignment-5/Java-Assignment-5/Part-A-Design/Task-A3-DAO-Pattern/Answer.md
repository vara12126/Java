# Task A3 – Discover the Data-Access Design Pattern

**Course Title:** Programming in Java (CSA09)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Bloom’s Level:** K4 – Analyse  
**Total Marks:** 5 Marks  

---

## 1. Scenario Context: The Redundant Database Classes

A student project manages academic records using three separate classes: `StudentDB`, `CourseDB`, and `MarksDB`. Inspection of these classes reveals significant code duplication: each class independently opens a database connection, prepares SQL queries, binds parameters, iterates over a `ResultSet`, unpacks columns into model objects, catches `SQLException`, and closes resources. 

This violates the **DRY (Don't Repeat Yourself)** software engineering principle and couples SQL persistence logic directly with business workflows.

---

## A3.1 – Identify the Common Pattern (2 Marks)

### Structural Decomposition: Repeated vs Differing Responsibilities

```
+---------------------------------------------------------------------------------------------+
|                            COMMON REPEATED PATTERN (Boilerplate)                            |
+---------------------------------------------------------------------------------------------+
|  1. Acquire Connection from DataSource / DriverManager                                      |
|  2. Create PreparedStatement from SQL query string                                         |
|  3. Execute Query (executeQuery) or DML Statement (executeUpdate)                           |
|  4. Check execution status or iterate over ResultSet (while rs.next())                     |
|  5. Handle SQLExceptions, transaction rollbacks, and log errors                            |
|  6. Release / close PreparedStatement, ResultSet, and Connection (try-with-resources)       |
+---------------------------------------------------------------------------------------------+
                                              │
                      ┌───────────────────────┴───────────────────────┐
                      ▼                                               ▼
+-------------------------------------------+   +-------------------------------------------+
|          STUDENT-SPECIFIC LOGIC           |   |           COURSE-SPECIFIC LOGIC           |
+-------------------------------------------+   +-------------------------------------------+
| • Model Entity: Student                   |   | • Model Entity: Course                    |
| • Primary Key: reg_no (String)            |   | • Primary Key: course_code (String)       |
| • SQL: INSERT INTO student VALUES(...)    |   | • SQL: INSERT INTO course VALUES(...)     |
| • Param Bind: ps.setString(1, s.getReg()) |   | • Param Bind: ps.setString(1, c.getCode())|
| • Mapping: extract name, email, dept      |   | • Mapping: extract title, credits         |
+-------------------------------------------+   +-------------------------------------------+
```

### Tabular Comparison of Responsibilities

| Processing Step | Repeated Infrastructure (Common) | Entity-Specific Logic (Variable) |
|:---|:---|:---|
| **Connection Handling** | `DriverManager.getConnection()` / Connection Pool | None |
| **Query Formulation** | Template structure (`SELECT * FROM table WHERE id = ?`) | Specific table name & column names |
| **Parameter Binding** | Index-based binding calls (`ps.setX(index, val)`) | Specific domain attributes and data types |
| **Execution** | `ps.executeQuery()` / `ps.executeUpdate()` | None |
| **Row Extraction (ORM)** | `while (rs.next()) { ... }` traversal | Unpacking columns into domain model constructor |
| **Resource Cleanup** | Safe closure in `try-with-resources` | None |

---

## A3.2 – Design a Generic Data-Access Interface (2 Marks)

The **Data Access Object (DAO)** pattern isolates the application/business layer from the persistence layer. By leveraging Java Generics, we define a unified contract `Dao<T, K>` where:
- `T`: The domain model entity type (e.g., `Student`, `Course`).
- `K`: The primary key identifier type (e.g., `String`, `Long`).

### Generic Interface Definition (`Dao<T, K>`)

```java
import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {
    boolean insert(T entity);
    Optional<T> findById(K id);
    List<T> findAll();
    boolean update(T entity);
    boolean delete(K id);
}
```

---

### Concrete Implementation: `StudentDao` Implementing `Dao<Student, String>`

#### SIMATS Algorithmic Pseudocode

```text
ALGORITHM StudentDao_FindById(connectionPool, targetRegNo)
    INPUT:  connectionPool: DataSource
            targetRegNo: STRING
    OUTPUT: studentOpt: OPTIONAL OF Student

    BEGIN
        DECLARE conn: Connection
        DECLARE stmt: PreparedStatement
        DECLARE rs: ResultSet
        DECLARE sqlQuery: STRING
        DECLARE foundStudent: Student ← NULL

        sqlQuery ← "SELECT reg_no, full_name, email, department, gpa FROM students WHERE reg_no = ?"

        TRY
            conn ← connectionPool.GET_CONNECTION()
            stmt ← conn.PREPARE_STATEMENT(sqlQuery)
            stmt.SET_STRING(1, targetRegNo)
            rs ← stmt.EXECUTE_QUERY()

            IF rs.NEXT() THEN
                foundStudent ← NEW Student(
                    rs.GET_STRING("reg_no"),
                    rs.GET_STRING("full_name"),
                    rs.GET_STRING("email"),
                    rs.GET_STRING("department"),
                    rs.GET_DOUBLE("gpa")
                )
            ENDIF

            rs.CLOSE()
            stmt.CLOSE()
            conn.CLOSE()

            RETURN OPTIONAL_OF(foundStudent)
        CATCH SQLException ex THEN
            LOG_ERROR("Database query failure for student " + targetRegNo, ex)
            RETURN OPTIONAL_EMPTY()
        ENDTRY
    END
```

```text
ALGORITHM StudentDao_Insert(connectionPool, student)
    INPUT:  connectionPool: DataSource
            student: Student
    OUTPUT: rowsInserted: INTEGER

    BEGIN
        DECLARE conn: Connection
        DECLARE stmt: PreparedStatement
        DECLARE sqlInsert: STRING
        DECLARE affectedRows: INTEGER ← 0

        sqlInsert ← "INSERT INTO students (reg_no, full_name, email, department, gpa) VALUES (?, ?, ?, ?, ?)"

        TRY
            conn ← connectionPool.GET_CONNECTION()
            stmt ← conn.PREPARE_STATEMENT(sqlInsert)
            stmt.SET_STRING(1, student.getRegNo())
            stmt.SET_STRING(2, student.getFullName())
            stmt.SET_STRING(3, student.getEmail())
            stmt.SET_STRING(4, student.getDepartment())
            stmt.SET_DOUBLE(5, student.getGpa())

            affectedRows ← stmt.EXECUTE_UPDATE()

            stmt.CLOSE()
            conn.CLOSE()
            RETURN affectedRows > 0
        CATCH SQLException ex THEN
            LOG_ERROR("Failed to insert student record: " + student.getRegNo(), ex)
            RETURN FALSE
        ENDTRY
    END
```

---

## A3.3 – Map Objects to Tables (1 Mark)

### Entity-to-Relational Mapping Architecture

```
        Java Domain Class (Student.java)                 MySQL Relational Table (students)
     +------------------------------------+             +------------------------------------+
     | - regNo: String                    |  <=======>  | reg_no      VARCHAR(20) [PK]       |
     | - fullName: String                 |  <=======>  | full_name   VARCHAR(100) NOT NULL  |
     | - email: String                    |  <=======>  | email       VARCHAR(100) UNIQUE    |
     | - department: String               |  <=======>  | department  VARCHAR(50)            |
     | - gpa: double                      |  <=======>  | gpa         DECIMAL(3,2)           |
     +------------------------------------+             +------------------------------------+
```

### Relational Schema Definition
```sql
CREATE TABLE students (
    reg_no VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    gpa DECIMAL(3, 2) NOT NULL,
    CONSTRAINT pk_students PRIMARY KEY (reg_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Mapping Matrix
| Java Attribute | Java Data Type | MySQL Column Name | SQL Data Type | Key / Constraint |
|:---|:---|:---|:---|:---|
| `regNo` | `java.lang.String` | `reg_no` | `VARCHAR(20)` | **PRIMARY KEY**, NOT NULL |
| `fullName` | `java.lang.String` | `full_name` | `VARCHAR(100)`| NOT NULL |
| `email` | `java.lang.String` | `email` | `VARCHAR(100)`| UNIQUE, NOT NULL |
| `department` | `java.lang.String` | `department` | `VARCHAR(50)` | NOT NULL |
| `gpa` | `double` | `gpa` | `DECIMAL(3, 2)` | CHECK (gpa >= 0.0 AND gpa <= 10.0) |

---

## Alternative Concept and Complexity Analysis: Direct SQL in GUI/Service vs Generic DAO

### The Alternative Architecture: Embedded SQL inside GUI & Service Classes
In early rapid application prototyping, developers often write raw SQL strings directly inside Swing button click listeners (`ActionListener`) or HTTP Controller handlers:

```java
// Anti-pattern: SQL inside GUI Action Listener
btnSave.addActionListener(e -> {
    Connection c = DriverManager.getConnection(...);
    PreparedStatement ps = c.prepareStatement("INSERT INTO students VALUES ('" + txtReg.getText() + "', ...)");
    ps.executeUpdate();
});
```

---

### Comparative Evaluation

| Evaluation Metric | Direct SQL in GUI / Service (Embedded) | Generic DAO Pattern (`Dao<T, K>`) |
|:---|:---|:---|
| **Coupling** | **Tight:** Presentation directly depends on database vendor SQL | **Loose:** Presentation depends only on abstract interface |
| **Number of Files Changed if Column Renamed** | **Dozens of scattered locations** across UI forms, report dialogs, controllers | **Exactly 1 File:** Only `StudentDao.java` changes |
| **Testability** | Impossible to unit test GUI without live database server | Easy: Mock DAO interface with fake in-memory map |
| **Separation of Concerns** | Violated (UI mixes layout, events, SQL, validation) | Maintained (UI handles view, DAO handles storage) |
| **Runtime CPU Time Cost** | Zero method delegation overhead | Negligible: 1 virtual method call invocation ($\approx 1\text{ ns}$) |
| **Heap Memory Overhead** | Zero additional classes instantiated | Tiny: single stateless DAO instance ($\approx 24\text{ bytes}$) |

### Maintenance Impact of a Column Rename
Suppose the database column `full_name` is renamed to `student_name`:
1. **With Direct SQL in GUI:** The developer must execute a global regex search across the entire project repository. A single missed occurrence in an obscure report screen will cause a runtime `SQLException: Unknown column 'full_name'` during production use.
2. **With Generic DAO:** The SQL query strings and column extractions are strictly encapsulated within `StudentDao`. Only the private SQL constant in `StudentDao.java` is modified. The service layer, business rules, and UI remain 100% untouched and compile cleanly.

### Complexity Verdict
The time complexity ($O(1)$) and space complexity ($O(1)$) of the database interaction itself are identical. However, the software maintenance complexity drops from $O(M \times N)$ (where $M$ queries are spread over $N$ UI components) to $O(1)$ centralized maintenance in the DAO implementation.
