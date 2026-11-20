# Task A2 – Don't Read What You Don't Need

**Course Title:** Programming in Java (CSA09)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**Bloom’s Level:** K4 – Analyse  
**Total Marks:** 5 Marks  

---

## 1. Scenario Context: The 50 MB Attendance Log

A university automated attendance system records student card-tap events into an audit log named `attendance.log`. Over the semester, the file has grown to **50 MB (megabytes)**. A junior developer writes a utility to count the total number of attendance records by reading the file **one byte at a time** using `java.io.FileInputStream.read()` and checking for the newline character (`\n`, ASCII value `10`).

---

## A2.1 – Predict the Cost (2 Marks)

### Quantitative Prediction of Read Calls
In computer storage, 1 Megabyte (binary mebibyte) equals $1024 \times 1024 = 1,048,576$ bytes.
$$\text{Total File Size in Bytes} = 50 \times 1024 \times 1024 = 52,428,800\text{ bytes}$$

When calling `FileInputStream.read()` in a loop:
```java
int b;
while ((b = in.read()) != -1) {
    if (b == '\n') lineCount++;
}
```
- Each iteration consumes exactly **1 byte**.
- The loop calls `in.read()` exactly **$52,428,800$ times** for data bytes, plus **1 additional call** returning `-1` to detect EOF (End Of File).
- **Total Read Invocations:** **$52,428,801$ calls**.

### Why Reading One Byte at a Time is Extremely Slow

```
User Space (JVM)                      Kernel Space (OS)                 Hardware (SSD/HDD)
+-------------------------+          +-------------------------+        +-------------------+
|  in.read() invoked      |          |  sys_read syscall       |        | Block Controller  |
|  (52,428,800 times)     |  ----->  |  Context Switch         |  --->  | Reads 4KB Sector  |
|  Thread blocks in JVM   |  <-----  |  Copies 1 byte to JVM   |  <---  | Discards rest     |
+-------------------------+          +-------------------------+        +-------------------+
  Repeated 52.4 Million Times = Massive CPU Stall & Context-Switch Thrashing!
```

1. **System Call and Context Switching Overhead:**
   Every call to `FileInputStream.read()` dispatches a JNI (Java Native Interface) call to the underlying C library, triggering an operating system system call (`sys_read` on POSIX / `ReadFile` on Windows). This forces the CPU to switch privilege levels from **User Mode** to **Kernel Mode** and back. Executing $52.4$ million context switches saturates the CPU core with mode-switch latency rather than computation.
2. **Disregard for Hardware Block Architecture:**
   Physical storage drives (HDDs and NVMe SSDs) transfer data in fixed sector/block clusters (typically $4\text{ KB}$ or $8\text{ KB}$). When reading 1 byte, the OS must stage the entire $4\text{ KB}$ block into its page cache, copy a single byte across the user-space boundary, and discard the rest of the transfer bandwidth for that call.
3. **JVM Call-Stack Overhead:**
   Pushing and popping $52,428,801$ activation records on the Java thread stack consumes millions of CPU instruction cycles unnecessarily.

---

## A2.2 – Redesign Using Buffering (2 Marks)

### Mechanism of Buffering
Buffering introduces an **in-memory staging array** (default $8\text{ KB} = 8,192\text{ bytes}$) in user-space RAM managed by `BufferedInputStream` or `BufferedReader`.
- **First Call:** When the application requests data, the buffer is empty. The stream issues a single OS system call requesting an entire chunk of $8,192$ bytes.
- **Subsequent 8,191 Calls:** The next $8,191$ byte requests are served directly from fast user-space memory via simple array index increments (`buffer[ptr++]`), requiring **0 kernel context switches**.
- **Refill:** When the buffer is exhausted, another $8\text{ KB}$ chunk is read in one system call.

### Reduction in Operating System System Calls
$$\text{Number of OS System Calls} = \left\lceil \frac{52,428,800}{8,192} \right\rceil = 6,400\text{ calls}$$

$$\text{System Call Reduction Factor} = \frac{52,428,800}{6,400} = 8,192\times \text{ fewer system calls}$$
$$\text{Percentage Reduction} = \left( 1 - \frac{6,400}{52,428,800} \right) \times 100\% = 99.988\%$$

---

### SIMATS Algorithmic Pseudocode

```text
ALGORITHM BufferedLineCounter(filePath)
    INPUT:  filePath: STRING (Path to 50 MB log file)
    OUTPUT: totalLines: INTEGER

    BEGIN
        DECLARE fileStream: FileInputStream
        DECLARE buffer: ARRAY OF BYTE[8192]
        DECLARE bytesRead: INTEGER
        DECLARE lineCount: INTEGER ← 0
        DECLARE i: INTEGER

        IF NOT FILE_EXISTS(filePath) THEN
            PRINT("Error: Target log file does not exist.")
            RETURN -1
        ENDIF

        TRY
            fileStream ← OPEN_FILE_INPUT_STREAM(filePath)
            
            // Read 8KB chunks in one OS operation
            WHILE (bytesRead ← fileStream.READ(buffer, 0, 8192)) ≠ -1 DO
                FOR i ← 0 TO bytesRead - 1 DO
                    // Check for newline byte (ASCII 10)
                    IF buffer[i] = 10 THEN
                        lineCount ← lineCount + 1
                    ENDIF
                ENDFOR
            ENDWHILE

            fileStream.CLOSE()
            RETURN lineCount
        CATCH IOException e THEN
            PRINT("Error during buffered read: " + e.MESSAGE)
            RETURN -1
        ENDTRY
    END
```

---

## A2.3 – When Buffering Does Not Help (1 Mark)

Buffering provides little or no benefit in the following practical scenario:

### Scenario: Random Non-Sequential Access with Sparse Lookups
When an application uses random access (such as reading 4-byte record pointers in an un-clustered B-Tree index or hash index across a multi-gigabyte file):
- The file pointer constantly jumps to disparate byte locations (e.g., offset 100, then offset 40,000,000, then offset 8,500).
- If an 8 KB buffer is loaded upon each seek, the application consumes only 4 bytes from that buffer and immediately issues another `seek()`.
- The remaining 8,188 prefetched bytes are discarded without being read.
- **Result:** The system incurs the overhead of allocating and filling the buffer with data that is never used ("cache thrashing"), yielding worse latency than targeted unbuffered unaligned reads or memory-mapped I/O (`FileChannel.map`).

---

## Alternative Concept and Complexity Analysis: Buffered Streams vs Files.readAllLines()

### Alternative Concept Explanation
Java NIO (`java.nio.file.Files`) provides `Files.readAllLines(Path path)`, which opens the file, decodes all lines into memory at once, and returns a `List<String>`.

```java
// Alternative Implementation:
List<String> lines = Files.readAllLines(Paths.get("attendance.log"));
int totalLines = lines.size();
```

---

### Comprehensive Comparative Analysis (50 MB File)

| Metric | Buffered Stream / Array (`BufferedReader` / `byte[8192]`) | In-Memory Slurp (`Files.readAllLines()`) |
|:---|:---|:---|
| **Time Complexity** | $O(N)$ where $N$ is file size (single sequential pass) | $O(N)$ (must read and parse all characters) |
| **Space Complexity** | **$O(B) \approx O(1)$** where $B = 8\text{ KB}$ buffer size | **$O(N)$** proportional to total file size on heap |
| **Heap Memory Consumed** | **$\approx 8\text{ KB}$** (fixed, independent of file size) | **$\approx 150\text{ MB} - 250\text{ MB}$** |
| **Garbage Collection Pressure**| Near zero (reuses single byte array) | Extremely high (creates millions of `String` instances) |
| **Risk of `OutOfMemoryError`** | **Zero** (can process 100 GB files safely) | **Extremely High** if file size approaches heap limit |
| **Maximum Scalable File Size**| Terabytes (limited only by disk size) | Constrained by JVM `-Xmx` heap limit (typically < 1 GB) |

### Memory Amplification in `Files.readAllLines()`
A raw 50 MB ASCII text file containing 1,000,000 lines requires vastly more than 50 MB of Java heap space due to object headers and 64-bit reference overheads:
1. `java.util.ArrayList` internal array: 1,000,000 element references $\times 8\text{ bytes} = 8\text{ MB}$.
2. 1,000,000 `java.lang.String` objects:
   - Object header: 16 bytes.
   - Hash code + coder + flags: 8 bytes.
   - `byte[] value` array reference: 8 bytes.
   - `byte[]` instance overhead: 16 bytes header + 4 bytes length.
   - String character bytes: average 50 bytes.
   - Total per line: $\approx 102\text{ bytes}$.
3. **Total Heap Allocated:** $1,000,000 \times 102\text{ bytes} + 8\text{ MB} \approx 110\text{ MB} - 180\text{ MB}$.

### Engineering Recommendation
- **Use Buffered Streams (`BufferedReader` / `InputStream`):** For all stream-processing, batch-counting, parsing, or file transformation tasks where data is processed sequentially. It provides constant memory safety ($O(1)$ RAM).
- **Use `Files.readAllLines()`:** Exclusively for small configuration files, metadata descriptors, or dictionary lookups ($< 2\text{ MB}$) where random index access across lines in memory is strictly necessary and memory footprint is negligible.
