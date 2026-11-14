# Task 03: Control Logic

## 1. Overview
This task implements the core control logic for managing parking spaces and calculating fees in the Smart-Parking System. It demonstrates standard control flow constructs including nested loops, conditional branching (`if-else`), multi-way branching (`switch-case`), control transfer statements (`break`, `continue`, `return`), and boundary condition defenses.

---

## 2. Pseudocode

### 2.1 Parking Slot Allocation Algorithm (First-Fit)
```text
ALGORITHM AllocateSlot(slots[][])
INPUT: 2D boolean array representing floor and slot occupancy
OUTPUT: Array [floor, slot] of first available slot, or null if full

BEGIN
    FOR floor FROM 0 TO LENGTH(slots) - 1 DO
        FOR slot FROM 0 TO LENGTH(slots[floor]) - 1 DO
            IF NOT slots[floor][slot] THEN
                slots[floor][slot] ← TRUE     // Mark as occupied
                RETURN [floor, slot]          // Early return on finding first match
            END IF
        END FOR
    END FOR
    RETURN null                               // Facility is completely full
END
```

### 2.2 Parking Slot Release Algorithm
```text
ALGORITHM ReleaseSlot(slots[][], floor, slot)
INPUT: slots matrix, target floor index, target slot index
OUTPUT: TRUE if released successfully, FALSE otherwise

BEGIN
    IF floor < 0 OR floor >= LENGTH(slots) THEN
        RETURN FALSE                          // Out of floor bounds
    END IF
    IF slot < 0 OR slot >= LENGTH(slots[floor]) THEN
        RETURN FALSE                          // Out of slot bounds
    END IF
    IF slots[floor][slot] == TRUE THEN
        slots[floor][slot] ← FALSE            // Mark as vacant
        RETURN TRUE
    ELSE
        RETURN FALSE                          // Slot was already vacant
    END IF
END
```

### 2.3 Parking Fee Calculation Algorithm
```text
ALGORITHM CalculateFee(vehicleType, hours, hasPass)
INPUT: vehicleType (String), hours (Integer), hasPass (Boolean)
OUTPUT: totalFee (Double)

BEGIN
    IF hours <= 0 THEN
        RETURN 0.0
    END IF

    SWITCH LOWERCASE(vehicleType) DO
        CASE "twowheeler":
            ratePerHour ← 10.0
            BREAK
        CASE "car":
            ratePerHour ← 30.0
            BREAK
        CASE "electriccar":
            ratePerHour ← 50.0
            BREAK
        DEFAULT:
            ratePerHour ← 20.0
    END SWITCH

    totalFee ← hours * ratePerHour
    IF hasPass == TRUE THEN
        totalFee ← totalFee * 0.5             // Apply 50% concession
    END IF

    RETURN totalFee
END
```

---

## 3. Control Flow Constructs Used

| Construct | Implementation Role in Smart-Parking System |
| :--- | :--- |
| `for` loop | Iterating through outer floor indices and inner slot indices during the search for available parking. |
| `if-else` | Validating boundary ranges (preventing `ArrayIndexOutOfBoundsException`), inspecting boolean slot flags, applying pass discounts. |
| `switch-case` | Selecting the base hourly parking rate mapped directly from the vehicle type string without multiple chained comparisons. |
| `break` | Terminating execution within a switch branch once a rate is matched, preventing fall-through bugs. |
| `return` | Immediate exit from `allocateSlot()` as soon as the first free space is identified, bypassing unnecessary iterations ($O(1)$ best-case exit). |

---

## 4. Expected vs. Actual Output Verification

The program `ParkingFacility.java` was configured with Floor 0 having 2 slots and Floor 1 having 1 slot (total 3 slots).

### Execution Steps:
1. `allocateSlot()` $\rightarrow$ Allocates Floor 0, Slot 0 $\rightarrow$ Output: `[0, 0]`
2. `allocateSlot()` $\rightarrow$ Allocates Floor 0, Slot 1 $\rightarrow$ Output: `[0, 1]`
3. `allocateSlot()` $\rightarrow$ Allocates Floor 1, Slot 0 $\rightarrow$ Output: `[1, 0]`
4. `allocateSlot()` $\rightarrow$ No slots left $\rightarrow$ Output: `null`
5. Release sequence:
   - `releaseSlot(0, 1)` $\rightarrow$ Returns `true`
   - `releaseSlot(0, 1)` $\rightarrow$ Returns `false` (already freed)
   - `releaseSlot(3, 0)` $\rightarrow$ Returns `false` (invalid floor out-of-bounds)
   - Output: `true false false`
6. `allocateSlot()` $\rightarrow$ Re-scans grid, finds `[0, 1]` newly vacant $\rightarrow$ Output: `[0, 1]`

### Actual Terminal Output:
```
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```
The output matches the assignment specification exactly.
