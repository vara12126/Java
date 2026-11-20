# Task 10 – Enforce Encapsulation through Packages and Access Modifiers

**Course Outcome:** CO1 – Java fundamentals and object-oriented programming  
**Bloom's Level:** K4 (Analyse)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  

---

## 1. Package Architecture and High-Cohesion Separation

Encapsulation in Java is enforced at two distinct levels:
1. **Class-level:** Using access specifiers (`private`, `protected`, default, `public`).
2. **Architectural-level:** Grouping related classes into logical **packages** with well-defined public boundaries.

```text
       CAMPUS SMART-PARKING SYSTEM PACKAGE ARCHITECTURE
 +------------------------------------------------------------------+
 |                          parking.app                             |
 |  - Main entry point, CLI user interface, test runners            |
 +------------------------------------------------------------------+
                                  |
                                  v
 +------------------------------------------------------------------+
 |                        parking.service                           |
 |  - ParkingFacility (allocation, release, coordination)           |
 |  - BillingService (tariffs, passes, invoicing)                   |
 +------------------------------------------------------------------+
                                  |
                                  v
 +------------------------------------------------------------------+
 |                         parking.model                            |
 |  - Vehicle, TwoWheeler, Car, ElectricCar                         |
 |  - ParkingSlot, ParkingTicket, Pass, User                        |
 +------------------------------------------------------------------+
```

### Package Roles:
1. **`parking.model`:** Contains domain entities representing real-world things. These classes encapsulate their internal state and provide domain invariants.
2. **`parking.service`:** Contains business logic and service coordinators that orchestrate operations on models (allocating slots, calculating complex billing).
3. **`parking.app`:** The presentation and driver layer interacting with the user or automated test suites.

---

## 2. Access Modifier Strategy

Java provides four access levels. We assign them strictly following the **Principle of Least Privilege**:

| Access Modifier | Applied To | Reason & Justification |
| :--- | :--- | :--- |
| **`private`** | All instance fields (`regNo`, `slots`, `ticketId`, `floor`, `entryTime`, `isPaid`). | **Absolute Encapsulation.** External classes cannot bypass business logic or directly mutate internal state into an inconsistent condition. |
| **`protected`** | Subclass hooks such as `ratePerHour()` in `Vehicle`. | Allows derived classes (`Car`, `TwoWheeler`) in any package to supply subclass tariffs, while shielding the method from arbitrary external callers. |
| **`default` (package-private)** | Internal helper methods, factory builders, or package-internal constructors. | Permits close collaboration between related classes in the same package (e.g., `ParkingFacility` interacting directly with `ParkingSlot`) without exposing internal implementation details to the outside world. |
| **`public`** | API entry points (`allocate()`, `release()`, `calculateFee()`, getters). | Explicit contract exposed to clients and calling applications. |

---

## 3. Data Validation via Getters and Setters

Public methods must act as **gatekeepers**, rejecting any invalid data that would violate system invariants:

```java
public class ParkingSlot {
    private int floor;
    private int slotNumber;
    private int durationMinutes;

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative: " + durationMinutes);
        }
        this.durationMinutes = durationMinutes;
    }

    public void setSlotCoordinates(int floor, int slotNumber, int maxFloors, int maxSlotsOnFloor) {
        if (floor < 0 || floor >= maxFloors) {
            throw new IndexOutOfBoundsException("Invalid floor index: " + floor);
        }
        if (slotNumber < 0 || slotNumber >= maxSlotsOnFloor) {
            throw new IndexOutOfBoundsException("Invalid slot index: " + slotNumber);
        }
        this.floor = floor;
        this.slotNumber = slotNumber;
    }
}
```

---

## 4. Wrapper Classes, Autoboxing, and the `==` Equality Pitfall

### Autoboxing and Unboxing
- **Autoboxing:** Automatic conversion of primitive types to their wrapper objects (e.g., `int` $\rightarrow$ `Integer`, `double` $\rightarrow$ `Double`).
- **Unboxing:** Automatic extraction of the primitive value from a wrapper object.

### The Dangers:
1. **`NullPointerException` during Unboxing:**
   ```java
   Integer slotCount = null;
   int primitiveSlots = slotCount; // THROWS NullPointerException at runtime!
   ```
2. **The `==` Reference Equality Trap:**
   The JVM caches `Integer` instances in the range **$-128$ to $+127$** (the Integer Cache). Outside this range, `new Integer` instances are created on the heap:
   ```java
   Integer a = 100;
   Integer b = 100;
   System.out.println(a == b); // Prints TRUE (both point to cached object)

   Integer x = 500;
   Integer y = 500;
   System.out.println(x == y); // Prints FALSE! (two different heap objects!)
   System.out.println(x.equals(y)); // Prints TRUE (compares underlying primitive values)
   ```
   **Rule:** Wrapper objects must **always** be compared using `.equals()`, never `==`.

---

## 5. Alternative Concept and Complexity Analysis

### Encapsulated Private Fields with Validating Setters vs Public Fields with Caller Validation

Suppose all fields were made `public` (e.g., `public int floor; public int duration;`), leaving validation to caller code:

```java
// Alternative Flawed Caller-Side Validation
if (slot.duration >= 0) {
    slot.duration = newDuration;
}
```

### What Goes Wrong?
1. **Fragile Invariants:** Any rogue developer, buggy test script, or external module can bypass validation and assign `slot.duration = -500;` or `slot.isOccupied = false;` while a car is still physically parked inside!
2. **Code Duplication ($O(C)$ Maintenance Cost):**
   If there are $C$ call sites (entry kiosk, exit barrier, mobile app, attendant admin tool), the exact same validation check (`if (d >= 0)`) must be manually duplicated across all $C$ locations.
   If the validation rule changes (e.g., maximum duration capped at 72 hours), developers must locate and rewrite code in $C$ separate files. If a single check is missed, the database corrupts.
3. **With Encapsulation:**
   The validation exists in **exactly 1 place** (the setter / constructor). Time complexity of validation remains $O(1)$ constant time, and maintenance complexity is reduced from $O(C)$ down to $O(1)$.
