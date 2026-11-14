# Task 02: Data Representation

## 1. Overview of Data Representation in Java
Efficient memory and state management in a Campus Smart-Parking System requires selecting appropriate primitive and reference data types. Furthermore, architectural features like multi-level parking with uneven slot capacities demand jagged array structures.

---

## 2. Java Data Types Utilized

| Data Type | Category | Size / Range | Role in Smart Parking System |
| :--- | :--- | :--- | :--- |
| `String` | Reference (Immutable) | Arbitrary string sequence | Vehicle registration numbers (e.g., `"KA05MH1001"`), ticket IDs (`"TICK-1001"`), pass serials. |
| `int` | Primitive (Signed 32-bit) | $-2^{31}$ to $2^{31}-1$ | Floor index ($0 \dots F-1$), slot index ($0 \dots S-1$), total slot counts. |
| `long` | Primitive (Signed 64-bit) | $-2^{63}$ to $2^{63}-1$ | Millisecond timestamps (`System.currentTimeMillis()`), duration in minutes/seconds. |
| `double` | Primitive (IEEE 754 64-bit) | 15–17 decimal digits | Hourly parking rates, calculated gross and discounted parking fees. |
| `BigDecimal` | Reference (Arbitrary-precision) | Arbitrary precision | Financial collection and fee calculations requiring exact decimal arithmetic without binary floating-point roundoff errors. |
| `LocalDateTime` | Reference (`java.time`) | Nanosecond precision calendar | Timestamping vehicle entry and exit times; performing timezone-independent temporal queries (e.g., delivery van 9 AM–5 PM window). |

---

## 3. Jagged Array Representation of Non-Uniform Floors
In modern parking garages, structural pillars, ramps, and specialized zones (such as EV charging corridors) mean that floors do not have identical slot capacities.
A standard 2D rectangular array `String[floors][slots]` wastes memory and misrepresents reality. A **jagged array** (array of arrays with differing inner lengths) directly mirrors this layout:

```java
// Allocating 3 floors with varying slot capacities
String[][] parkingGrid = new String[3][];
parkingGrid[0] = new String[4]; // Ground Floor: 4 slots
parkingGrid[1] = new String[2]; // Level 1 (EV Deck): 2 slots
parkingGrid[2] = new String[5]; // Level 2 (Open Roof): 5 slots
```

### Memory Footprint:
In Java, an array is an object allocated on the heap. A jagged array `parkingGrid` holds an array of references to 3 distinct array objects. This allows `parkingGrid[f].length` to be dynamically queried per floor.

---

## 4. Integer Division and Casting Errors in Fee Calculation

### The Trap:
In Java, when both operands of the division operator `/` are integers (`int` or `long`), the operation performs integer truncation towards zero:
```java
long durationMinutes = 45;
double rate = 40.0;
double fee = (durationMinutes / 60) * rate; 
// 45 / 60 evaluates to 0 (truncated int)
// 0 * 40.0 evaluates to 0.0!
```
A driver who parks for 45 minutes would be billed ₹0.0! Even for 75 minutes, `75 / 60 = 1`, resulting in the 15 minutes of excess parking remaining unbilled.

### Solutions:
1. **Explicit Floating-Point Casting:**
   ```java
   double fee = ((double) durationMinutes / 60.0) * rate;
   ```
2. **Ceiling Slab Policy (Industry Standard):**
   Campus parking policies typically charge for any started hour:
   ```java
   long billableHours = (long) Math.ceil((double) durationMinutes / 60.0);
   double totalFee = billableHours * rate;
   ```

---

## 5. Comparative Evaluation: 1D Array vs. Jagged Array vs. ArrayList

| Dimension | One-Dimensional Array (`T[]`) | Two-Dimensional Jagged Array (`T[][]`) | Dynamic `ArrayList<List<T>>` |
| :--- | :--- | :--- | :--- |
| **Structure** | Flat continuous memory layout | Array of array references | Dynamically resizable object list |
| **Capacity Handling** | Requires manual mathematical flattening: $\text{idx} = f \times S + s$. Fails if floors have unequal slots. | Models unequal floor capacities naturally (`matrix[f].length`). | Can grow floors and slots dynamically at runtime. |
| **Type Flexibility** | Homogeneous primitives or objects | Homogeneous primitives or objects | Objects only (via autoboxing for primitives) |
| **Memory Overhead** | Lowest (single array header) | Moderate (one parent array + $F$ child arrays) | Highest (resizing overhead, boxing, object pointers) |
| **Access Speed** | Very fast $O(1)$ single memory dereference | Fast $O(1)$ dual memory dereference | $O(1)$ method dispatch (`get(f).get(s)`) |
| **Best Used For** | Fixed single-row parking bays | Fixed multi-floor garages with known architecture | Parking systems with dynamic expansions / runtime modifications |

---

## 6. Execution Instructions
Compile and run the demonstration:
```bash
javac DataRepresentationDemo.java
java DataRepresentationDemo
```
