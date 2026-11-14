# Question 3: HashCode and Equals Contract (Student Attendance System)

## 1. AIM
To implement, execute, and evaluate a Student Attendance Registration System using `HashSet`, analyze the failure of duplicate prevention when `hashCode()` is omitted, complete TODO 1 by overriding `hashCode()` in compliance with the `java.lang.Object` contract, and investigate three in-depth failure scenarios (bulk duplicate registration, hash collision degradation, and mutable field mutation after insertion).

---

## 2. PROBLEM STATEMENT
A university attendance system registers incoming students using a `HashSet<Student>`. To enforce that each student is marked present exactly once per lecture, business logic defines student identity solely by their university registration ID (`studentId`). 

If a developer overrides `equals()` to compare `studentId` but fails to override `hashCode()`:
1. Two logically identical `Student` instances will produce different default hash codes (derived from distinct memory addresses via `System.identityHashCode()`).
2. When inserted into a `HashSet`, the two instances map to different hash buckets, bypassing `equals()` entirely, allowing duplicates to penetrate the set.
3. If fields participating in `hashCode()` are mutable and modified after insertion, the object becomes an unfindable "Zombie Object", corrupting the data structure and causing silent memory leaks.

---

## 3. THEORY

### 3.1 HashSet Internal Architecture
`java.util.HashSet` is backed internally by a `java.util.HashMap<E, Object>` where the set elements serve as map keys, and a dummy singleton `Object PRESENT = new Object()` serves as the map value.

When an element `key` is added:
1. The JVM invokes `key.hashCode()`.
2. A supplementary hash function spreads higher bits:
   $$\text{hash} = \text{h} \oplus (\text{h} \gg 16)$$
3. The bucket index in the internal table array is computed using bitwise AND:
   $$\text{bucketIndex} = (\text{table.length} - 1) \mathbin{\&} \text{hash}$$
4. If the bucket is empty, the node is inserted.
5. If the bucket is occupied, Java traverses the bucket chain. For each node $p$:
   - It checks: `if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))`
   - If both the hash match and `equals()` return `true`, the duplicate is recognized and rejected.
   - **Crucial Diagnostic**: If `p.hash != hash`, Java **never invokes `equals()`**!

### 3.2 The hashCode-equals Contract (java.lang.Object Specification)
1. **Reflexive**: `x.equals(x)` must return `true`.
2. **Symmetric**: `x.equals(y) == y.equals(x)`.
3. **Transitive**: If `x.equals(y)` and `y.equals(z)`, then `x.equals(z)`.
4. **Consistent**: Repeated invocations of `equals()` and `hashCode()` must return identical values unless state changes.
5. **The Cardinal Rule**: If two objects are equal according to `equals(Object)`, then calling `hashCode()` on each of the two objects **MUST produce the same integer result**.
6. **Collision Non-Constraint**: If two objects produce the same `hashCode()`, they are **not** required to be equal according to `equals()`.

---

## 4. ALGORITHM

### 4.1 Step 1 (Without hashCode)
1. Define `StudentBroken` overriding `equals()` on `studentId` without overriding `hashCode()`.
2. Instantiate two instances with identical `studentId = "192365032"`.
3. Attempt to insert both into `HashSet<StudentBroken>`.
4. Observe size $= 2$ (Duplicate failure).

### 4.2 Step 2 (With hashCode - TODO 1 Completed)
1. Define `Student` overriding both `equals()` and `hashCode()` using `Objects.hash(studentId)`.
2. Attempt to insert both into `HashSet<Student>`.
3. Observe size $= 1$ (Duplicate rejected).

### 4.3 Diagnostic Scenarios
- **Scenario 1**: 2,000 registrations with 500 unique IDs; verify exactly 500 stored and 1,500 rejected.
- **Scenario 2**: Pathological hash collisions (constant `hashCode = 42`); evaluate Java 8+ treeification.
- **Scenario 3**: Mutate `studentId` post-insertion; demonstrate `contains()` returning `false` and removal failure.

---

## 5. IMPLEMENTATION DETAILS

### 5.1 Structure
```text
Q3_HashCode_Equals/
├── src/
│   ├── StudentBroken.java           # Without hashCode (initial state)
│   ├── Student.java                 # Compliant with TODO 1 complete
│   ├── StudentMutable.java          # Mutable model for Scenario 3
│   ├── AttendanceSystemBefore.java  # Step 1 execution demonstration
│   └── AttendanceSystemAfter.java   # Step 2 & 3 scenarios demonstration
├── results/
│   └── Q3_Scenario_Results.txt      # Text audit logs
├── screenshots/
│   ├── Q3_HashSet_Before.png        # Step 1 bug output evidence
│   └── Q3_HashSet_After.png         # Step 2 corrected contract evidence
└── README.md                        # Documentation
```

---

## 6. PROGRAM CODE
The complete source files are located in the [src/](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/src) directory:
- [StudentBroken.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/src/StudentBroken.java)
- [Student.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/src/Student.java)
- [StudentMutable.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/src/StudentMutable.java)
- [AttendanceSystemBefore.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/src/AttendanceSystemBefore.java)
- [AttendanceSystemAfter.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/src/AttendanceSystemAfter.java)

---

## 7. INPUT
- Two identical student records: `studentId = "192365032"`, Name = "Devi Vara Prasad", Dept = "CSE".
- Scenario 1: 2,000 registration attempts with 500 unique IDs.
- Scenario 2: 5,000 objects with identical hash code (`42`).
- Scenario 3: Modifying key field from `"REG_001"` to `"REG_999"`.

---

## 8. OUTPUT

### 8.1 Step 1 (Broken Contract)
```text
Testing Equivalence directly:
  s1.equals(s2)             : true [Logically EQUAL]
  s1.hashCode() (System)    : 2060468723
  s2.hashCode() (System)    : 622488023
  s1.hashCode() == s2.hashCode() : false [VIOLATION OF CONTRACT!]

Registering students into HashSet Attendance Register...
  Added s1 successfully?    : true
  Added s2 (duplicate)?     : true <-- [BUG: Duplicate accepted by HashSet!]
  Attendance Register Size  : 2 (Expected: 1, Actual: 2)
```

### 8.2 Step 2 (TODO 1 Completed)
```text
Testing Equivalence directly:
  s1.equals(s2)             : true [Logically EQUAL]
  s1.hashCode()             : 827113158
  s2.hashCode()             : 827113158
  s1.hashCode() == s2.hashCode() : true [CONTRACT PERFECTLY SATISFIED!]

Registering students into HashSet Attendance Register...
  Added s1 successfully?    : true
  Added s2 (duplicate)?     : false <-- [CORRECT: Duplicate cleanly REJECTED by HashSet]
  Attendance Register Size  : 1 (Expected: 1, Actual: 1)
```

---

## 9. EXECUTION SCREENSHOTS

### 9.1 Step 1: Execution BEFORE Overriding hashCode() (Duplicate Bug)
![Q3_HashSet_Before](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/screenshots/Q3_HashSet_Before.png)

### 9.2 Step 2: Execution AFTER Completing TODO 1 (Contract Enforced)
![Q3_HashSet_After](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q3_HashCode_Equals/screenshots/Q3_HashSet_After.png)

---

## 10. EXPERIMENTAL RESULTS

### 10.1 Scenario 1: Bulk Duplicate Registration
- **Attempts**: 2,000
- **Unique IDs**: 500
- **Result**: Exactly 500 elements retained in `HashSet`; 1,500 duplicate attempts rejected with 100% precision.

### 10.2 Scenario 2: Deliberate Hash Collision Stress Test
- **Elements**: 5,000 elements mapping to `hashCode = 42`.
- **Insert Time**: 179.412 ms
- **Lookup Time**: 200.280 ms
- **Analysis**: In legacy Java 7, 5,000 collisions in a linked list would cause catastrophic $O(N^2)$ slowdown ($> 5,000 \text{ ms}$). In modern Java 8+, the bucket automatically converts into a balanced Red-Black tree (`TreeNode`) once the bucket length reaches 8 (`TREEIFY_THRESHOLD`), bounding worst-case lookup time to $O(\log N)$.

### 10.3 Scenario 3: Key Mutation After Insertion ("Zombie Object")
- When `studentId` changed from `"REG_001"` to `"REG_999"`:
  - `register.contains(s)` returned `false`.
  - `register.remove(s)` returned `false`.
  - `register.size()` remained `1`.
- The element became permanently trapped in the bucket determined by its original hash code, leaking memory.

---

## 11. TIME COMPLEXITY

| Data Structure / Operation | Average Case | Worst Case (Pathological Collisions) | Worst Case (Java 8+ Treeified) |
| :--- | :--- | :--- | :--- |
| **HashSet.add()** | $O(1)$ | $O(N)$ (Java 7 linked list) | $O(\log N)$ (Red-Black Tree) |
| **HashSet.contains()** | $O(1)$ | $O(N)$ (Java 7 linked list) | $O(\log N)$ (Red-Black Tree) |
| **HashSet.remove()** | $O(1)$ | $O(N)$ (Java 7 linked list) | $O(\log N)$ (Red-Black Tree) |

---

## 12. SPACE COMPLEXITY

- `HashSet` space complexity is $O(N)$ where $N$ is the number of stored elements.
- Internal table array allocates capacity in powers of two ($16, 32, 64, \dots$).
- Default load factor is $0.75$, meaning the table resizes when $75\%$ of buckets are occupied.

---

## 13. ALTERNATIVE CONCEPT: HashSet vs TreeSet vs HashMap

| Property | HashSet | TreeSet | HashMap |
| :--- | :--- | :--- | :--- |
| **Underlying Structure** | `HashMap<E, Object>` | Red-Black Tree (`TreeMap`) | Array of Buckets + TreeNodes |
| **Time Complexity** | $O(1)$ average | $O(\log N)$ guaranteed | $O(1)$ average |
| **Ordering** | Unordered / Unpredictable | Sorted (Natural or Comparator) | Unordered / Unpredictable |
| **Null Key Support** | Allows 1 null element | Prohibits null (throws NPE) | Allows 1 null key |
| **Required Contract** | `equals()` and `hashCode()` | `Comparable` or `Comparator` | `equals()` and `hashCode()` |

---

## 14. ANALYSIS & ARCHITECTURAL DEFENSE
1. **Why Incorrect Implementation Produces Unpredictable Duplicates**:
   It is vital to understand that omitting `hashCode()` does not deterministically mean duplicates will appear in every tiny manual test. The behavior depends on whether the JVM's memory allocator happens to place two objects at addresses that map to the same bucket index modulo table length. However, because identity hash codes are distributed broadly across the 32-bit integer range, duplicates will almost always land in distinct buckets.
2. **Defensive Coding Rule**:
   All fields participating in `equals()` and `hashCode()` **MUST be declared `final`** to guarantee object immutability while residing in hash-based collections.

---

## 15. RESULT
TODO 1 was successfully completed, the contract violations were diagnosed and proven empirically, three robust test scenarios were executed, and full architectural documentation and genuine terminal evidence were produced.
