# Task A1 – Design a File Format

**Course Title:** Programming in Java (CSA09)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Bloom’s Level:** K4 – Analyse  
**Total Marks:** 7 Marks  

---

## 1. Scenario Context: University Transport Office Bus-Pass Records

The university transport office issues approximately **8,000 bus passes** annually. Each bus pass contains the following attributes:
1. `pass_id`: Unique identifier (e.g., `"P1001"`, string)
2. `reg_no`: Student register number (e.g., `"192311094"`, string/long)
3. `name`: Student full name (e.g., `"Kattiripalli Dhanush"`, string)
4. `route_no`: Bus route number (e.g., `12`, integer)
5. `boarding_stop`: Boarding station/point (e.g., `"Tambaram"`, string)
6. `validity_date`: Pass validity expiry date (e.g., `"2027-05-31"`, ISO-8601 string or epoch long)
7. `amount_paid`: Bus fee collected (e.g., `4500.00`, double)

The system must support three fundamental persistent operations:
1. **Add** a new bus pass.
2. **Find** a bus pass by its unique `pass_id`.
3. **List** all bus passes assigned to a specific `route_no`.

---

## A1.1 – Represent the Record (2 Marks)

### (a) Delimited Text-File Record Layout

In a delimited text file, each record occupies one physical line terminated by a standard newline sequence (`\n` or `\r\n`). Fields are separated by a delimiter (chosen here as comma `,`, following the standard RFC 4180 CSV specification).

#### Record Specification Table
| Field Pos | Field Name | Data Type | Example Value | Description |
|:---:|:---|:---|:---|:---|
| 1 | `pass_id` | String (Alpha-numeric) | `P1001` | Unique primary record identifier |
| 2 | `reg_no` | String (Digits) | `192311094` | Student academic register number |
| 3 | `name` | String | `"Dhanush, K."` | Student full name (may contain comma) |
| 4 | `route_no` | Integer | `12` | Bus route identifier |
| 5 | `boarding_stop` | String | `"Tambaram Main Road"` | Campus pickup location |
| 6 | `validity_date`| String (YYYY-MM-DD)| `2027-05-31` | Pass expiration date |
| 7 | `amount_paid` | Double | `4500.00` | Monetary amount collected |

#### Example Raw Text File Representation
```csv
pass_id,reg_no,name,route_no,boarding_stop,validity_date,amount_paid
P1001,192311001,Arun Kumar,12,Central Station,2027-05-31,4500.00
P1002,192311002,"Dhanush, K.",12,Tambaram,2027-05-31,4500.00
P1003,192311003,Divya S,7,Guindy,2027-05-31,3800.00
```

#### Handling Names Containing Delimiters (RFC 4180 Escaping)
If a student's name contains the delimiter character (e.g., comma in `"Dhanush, K."` or `"Robert, Jr."`):
1. **Quote Enclosure:** Any field containing a comma is enclosed inside double-quote marks (`"..."`).
2. **Quote Escaping:** If the name contains literal double-quotes (e.g., `O'Connor "Bobby"`), the quotation mark is escaped by doubling it (`""`).
3. **Parsing Rule:** A state-machine or token parser only splits on commas that are *outside* double quotation marks.

---

### (b) Binary-File Record Layout (DataOutputStream)

In a binary representation written using `java.io.DataOutputStream`, values are serialized in network byte order (Big-Endian) according to their native primitive machine representations.

#### Binary Stream Layout
```
+---------------+----------------+----------------+------------+----------------+----------------+----------------+
| pass_id (UTF) |  reg_no (UTF)  |   name (UTF)   | route (i4) |  stop (UTF)    | validity (UTF) |  amount (f8)   |
| 2-byte len +  |  2-byte len +  |  2-byte len +  |  4 bytes   |  2-byte len +  |  2-byte len +  |    8 bytes     |
| ASCII bytes   |  ASCII bytes   |  UTF-8 bytes   | Big-Endian |  UTF-8 bytes   |  ASCII bytes   | IEEE 754 float |
+---------------+----------------+----------------+------------+----------------+----------------+----------------+
```

#### DataOutputStream Writing Order
1. `out.writeUTF(passId)`: 2-byte unsigned length prefix $L_1$ + $L_1$ bytes of modified UTF-8 string.
2. `out.writeUTF(regNo)`: 2-byte unsigned length prefix $L_2$ + $L_2$ bytes.
3. `out.writeUTF(name)`: 2-byte unsigned length prefix $L_3$ + $L_3$ bytes.
4. `out.writeInt(routeNo)`: Exactly 4 bytes (32-bit signed integer).
5. `out.writeUTF(boardingStop)`: 2-byte unsigned length prefix $L_4$ + $L_4$ bytes.
6. `out.writeUTF(validityDate)`: 2-byte unsigned length prefix $L_5$ + $L_5$ bytes (or `writeLong(epochMillis)` for 8 bytes).
7. `out.writeDouble(amountPaid)`: Exactly 8 bytes (64-bit IEEE 754 double precision floating point).

#### Why Delimiter Ambiguity Disappears in Binary Format
In binary streams, fields are **not delimited by characters**; instead, they are bounded by **explicit length prefixes** and **fixed byte widths**. When reading with `DataInputStream.readUTF()`, the stream reads the 2-byte length $L$, and then reads exactly $L$ bytes into the string. Comma, quotes, newlines, and null bytes are read verbatim as data without terminating the field.

---

## A1.2 – Design the Operations (3 Marks)

### Stream Class Selection and Justification
- **Text File Appending (`BufferedWriter` + `FileWriter`):**
  - Character-based stream handles character encoding (UTF-8).
  - `BufferedWriter` provides an in-memory 8 KB buffer, aggregating multiple small field string writes into single OS block operations.
  - Appending mode (`new FileWriter(filePath, true)`) moves the file pointer to EOF without rewriting existing records.
- **Text File Reading (`BufferedReader` + `FileReader`):**
  - Provides the `readLine()` method, reading line-delimited records sequentially.
  - Avoids reading the entire 8,000 records into heap memory simultaneously, maintaining $O(1)$ auxiliary space complexity.

---

### SIMATS Algorithmic Pseudocode

#### Operation 1: Add a New Bus Pass
```text
ALGORITHM AddBusPass(filePath, passRecord)
    INPUT:  filePath: STRING (Path to persistent CSV file)
            passRecord: PassRecord (Contains passId, regNo, name, routeNo, stop, validity, amount)
    OUTPUT: success: BOOLEAN

    BEGIN
        DECLARE writer: BufferedWriter
        DECLARE formattedLine: STRING
        DECLARE escapedName: STRING

        // Handle delimiter escaping for name
        IF CONTAINS(passRecord.name, ",") THEN
            escapedName ← CONCAT('"', REPLACE(passRecord.name, '"', '""'), '"')
        ELSE
            escapedName ← passRecord.name
        ENDIF

        formattedLine ← CONCAT_WS(",", passRecord.passId, passRecord.regNo, escapedName, 
                                  STRING(passRecord.routeNo), passRecord.boardingStop, 
                                  passRecord.validityDate, STRING(passRecord.amountPaid))

        TRY
            writer ← OPEN_BUFFERED_WRITER(filePath, APPEND_MODE = TRUE)
            writer.WRITE(formattedLine)
            writer.NEW_LINE()
            writer.FLUSH()
            writer.CLOSE()
            RETURN TRUE
        CATCH IOException e THEN
            PRINT("Error writing to bus pass store: " + e.MESSAGE)
            RETURN FALSE
        ENDTRY
    END
```

---

#### Operation 2: Find a Bus Pass by Pass ID
```text
ALGORITHM FindBusPassById(filePath, targetPassId)
    INPUT:  filePath: STRING
            targetPassId: STRING
    OUTPUT: matchingPass: PassRecord OR NULL

    BEGIN
        DECLARE reader: BufferedReader
        DECLARE currentLine: STRING
        DECLARE tokens: ARRAY OF STRING

        IF NOT FILE_EXISTS(filePath) THEN
            PRINT("Error: Bus pass store file not found at " + filePath)
            RETURN NULL
        ENDIF

        TRY
            reader ← OPEN_BUFFERED_READER(filePath)
            WHILE (currentLine ← reader.READ_LINE()) ≠ NULL DO
                currentLine ← TRIM(currentLine)
                IF currentLine = "" THEN
                    CONTINUE  // Skip blank lines
                ENDIF

                tokens ← PARSE_CSV_LINE(currentLine)

                // Verify integrity: record must have exactly 7 fields
                IF LENGTH(tokens) ≠ 7 THEN
                    PRINT("Warning: Skipping malformed line: " + currentLine)
                    CONTINUE
                ENDIF

                // Check primary key match
                IF tokens[0] = targetPassId THEN
                    DECLARE pass: PassRecord
                    pass.passId ← tokens[0]
                    pass.regNo ← tokens[1]
                    pass.name ← tokens[2]
                    pass.routeNo ← TO_INTEGER(tokens[3])
                    pass.boardingStop ← tokens[4]
                    pass.validityDate ← tokens[5]
                    pass.amountPaid ← TO_DOUBLE(tokens[6])
                    reader.CLOSE()
                    RETURN pass
                ENDIF
            ENDWHILE
            reader.CLOSE()
            RETURN NULL  // Not found
        CATCH IOException e THEN
            PRINT("I/O failure while scanning records: " + e.MESSAGE)
            RETURN NULL
        ENDTRY
    END
```

---

#### Operation 3: List All Passes on a Given Route
```text
ALGORITHM ListPassesByRoute(filePath, targetRouteNo)
    INPUT:  filePath: STRING
            targetRouteNo: INTEGER
    OUTPUT: routePasses: LIST OF PassRecord

    BEGIN
        DECLARE reader: BufferedReader
        DECLARE currentLine: STRING
        DECLARE tokens: ARRAY OF STRING
        DECLARE matchingList: LIST OF PassRecord ← EMPTY_LIST()

        IF NOT FILE_EXISTS(filePath) THEN
            RETURN matchingList
        ENDIF

        TRY
            reader ← OPEN_BUFFERED_READER(filePath)
            WHILE (currentLine ← reader.READ_LINE()) ≠ NULL DO
                IF TRIM(currentLine) = "" THEN
                    CONTINUE
                ENDIF

                tokens ← PARSE_CSV_LINE(currentLine)
                IF LENGTH(tokens) ≠ 7 THEN
                    CONTINUE  // Skip damaged records
                ENDIF

                IF TO_INTEGER(tokens[3]) = targetRouteNo THEN
                    DECLARE pass: PassRecord
                    pass.passId ← tokens[0]
                    pass.regNo ← tokens[1]
                    pass.name ← tokens[2]
                    pass.routeNo ← TO_INTEGER(tokens[3])
                    pass.boardingStop ← tokens[4]
                    pass.validityDate ← tokens[5]
                    pass.amountPaid ← TO_DOUBLE(tokens[6])
                    APPEND(matchingList, pass)
                ENDIF
            ENDWHILE
            reader.CLOSE()
            RETURN matchingList
        CATCH IOException e THEN
            PRINT("I/O error reading route passes: " + e.MESSAGE)
            RETURN matchingList
        ENDTRY
    END
```

---

## A1.3 – Design for Damaged Data (2 Marks)

In production file stores, corrupted data must never cause catastrophic program termination or silent data corruption.

```
+---------------------------------------------------------------------------------------------+
|                                    FAILURE DEFENSE MODEL                                    |
+---------------------------------------------------------------------------------------------+
|  Damaged Record  --> Token Count != 7 / NumberFormatException --> Log Warning, Continue     |
|  Missing File    --> FileNotFoundException / Files.notExists()   --> Initialize New File    |
|  Power Failure   --> Partial Write / Half-written Line           --> Atomic Temp-File Swap  |
+---------------------------------------------------------------------------------------------+
```

### 1. Damaged Lines
- **Detection:** When `readLine()` extracts a record, the program splits the line using CSV parsing logic. If `tokens.length != 7`, or if `Integer.parseInt(tokens[3])` or `Double.parseDouble(tokens[6])` throws a `NumberFormatException`, the line is flagged as damaged.
- **Handling:** The program logs an audit warning indicating the corrupted line number and record content, skips the corrupted line, and continues processing the remainder of the file. A damaged record never aborts an ongoing search for unaffected records.

### 2. Missing File
- **Detection:** `Files.exists(path)` check or catching `java.io.FileNotFoundException` when opening the reader.
- **Handling:**
  - For reading (`find`, `list`): Return an empty collection or `null`, warning the user gracefully.
  - For writing (`add`): Automatically create parent directories and initialize the file with a valid standard header row (`pass_id,reg_no,name,route_no,boarding_stop,validity_date,amount_paid\n`).

### 3. Incomplete Writes Caused by Power Failure
- **Detection:** If a power outage occurs midway through a write, the final line in the file may be truncated without a terminating newline `\n` or may contain fewer than 7 tokens.
- **Handling (Atomic File Operations):**
  1. For batch updates or rewrites, writes are directed to a temporary file (`passes.tmp`).
  2. The buffer is flushed explicitly (`writer.flush()`) and synced to physical storage via `FileDescriptor.sync()`.
  3. Once safely closed, `Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)` replaces the target file atomically at the OS filesystem inode level.

---

## Alternative Concept and Complexity Analysis: Fixed-Length Records with RandomAccessFile

### Concept Explanation
Instead of a variable-length delimited text file, records can be structured with **fixed field byte widths** and stored using `java.io.RandomAccessFile`:

```
+---------------+---------------+---------------+------------+---------------+---------------+------------+
| pass_id (10B) |  reg_no (15B) |   name (40B)  | route (4B) |   stop (30B)  | validity (10B)| amount (8B)|
+---------------+---------------+---------------+------------+---------------+---------------+------------+
Total Record Size = 10 + 15 + 40 + 4 + 30 + 10 + 8 = 117 bytes.
```

- Each record $i$ (0-indexed) starts at exact byte offset:
  $$\text{Byte Offset} = i \times \text{RecordSize} = i \times 117$$
- To read record $i$, the program seeks directly:
  ```java
  raf.seek(i * 117L);
  ```

### Complexity Comparison: Delimited Text File vs RandomAccessFile

| Dimension | Delimited Text File (Variable Length) | RandomAccessFile (Fixed Length) |
|:---|:---|:---|
| **Find by Record Index ($i$)** | $O(n)$ linear scan (must count newlines) | **$O(1)$** direct seek (`raf.seek(i * 117)`) |
| **Find by ID (Unsorted)** | $O(n)$ linear scan | $O(n)$ linear scan (or $O(\log n)$ if sorted by ID via binary search) |
| **Append New Record** | $O(1)$ amortized (write at EOF) | $O(1)$ (`raf.seek(raf.length())`) |
| **Update Existing Record** | $O(n)$ rewrite of subsequent file bytes | **$O(1)$ in-place update** |
| **Storage Efficiency** | **High** (compact, stores exact string lengths) | **Low** (wastes space due to blank padding) |

### Space Wasted by Padding (Empirical Calculation for 8,000 Records)
In a fixed-length layout, shorter strings must be padded with spaces or zeros to fill the fixed allocation:
- Average student name: 18 characters (allocated: 40 bytes $\rightarrow$ **22 bytes wasted**).
- Average boarding stop: 14 characters (allocated: 30 bytes $\rightarrow$ **16 bytes wasted**).
- Average reg_no: 10 characters (allocated: 15 bytes $\rightarrow$ **5 bytes wasted**).
- Average pass_id: 6 characters (allocated: 10 bytes $\rightarrow$ **4 bytes wasted**).

$$\text{Average Wasted Bytes Per Record} = 22 + 16 + 5 + 4 = 47\text{ bytes}$$
$$\text{Total Storage Wasted across 8,000 Records} = 8,000 \times 47 = 376,000\text{ bytes } (\approx 367.2\text{ KB})$$
$$\text{Percentage Overhead} = \frac{47}{117} \times 100 \approx 40.17\% \text{ storage wasted}$$

### Engineering Conclusion
For 8,000 annual records (~936 KB file size), variable-length delimited text with buffering offers superior storage density and human readability. However, if frequent in-place updates or $O(\log n)$ binary search by ID were required without loading data into memory, `RandomAccessFile` justifies the 40% padding penalty.
