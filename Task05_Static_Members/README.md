# Task 05: Static Members and Object Life Cycle

## 1. Overview
The `static` keyword in Java binds variables, methods, and initialization blocks to the class itself rather than to individual object instances. This task examines class-level state management, unique sequential ID generation, static blocks, and the JVM object lifecycle from allocation to garbage collection.

---

## 2. Static Members vs. Instance Members

| Aspect | Static Variable (`ticketCounter`) | Instance Variable (`instanceTicketCounter`) |
| :--- | :--- | :--- |
| **Storage Location** | Method Area / Metaspace | JVM Heap (inside individual object payload) |
| **Copies in Memory** | Exactly one shared copy per JVM classloader | One copy per instantiated object |
| **Lifecycle** | Persists throughout class loading duration | Persists only while the specific object is referenced |
| **Invocation** | Accessed via class name: `ParkingTicket.getTotal()` | Accessed via object reference: `t1.getTicketId()` |
| **Suitability for IDs** | **Perfect**: Guarantees monotonically increasing sequence | **Broken**: Resets to initial value for every new object |

---

## 3. Static Initialization Block
A static block runs strictly once when the JVM's `ClassLoader` first loads and links the class byte-code into Metaspace, prior to any constructor invocation or instance creation:

```java
static {
    ticketCounter = 1000;
    PREFIX = "TICK-";
    System.out.println("[ParkingTicket] Static block executed.");
}
```
This is essential for initializing complex static state, reading configuration parameters, or registering drivers.

---

## 4. Object Life Cycle and Garbage Collection (GC)

### 4.1 Stages of an Object's Life:
1. **Class Loading:** The `.class` byte-code is loaded, and static blocks execute.
2. **Instantiation & Allocation:** Memory is carved out on the Heap (`new ParkingTicket(...)`), zero-initialized, and constructors execute.
3. **In-Use (Referenced):** Strong references exist on thread stacks or other live objects pointing to the heap memory address.
4. **Unreachable (Eligible for GC):** All stack pointers pointing to the object go out of scope (e.g., method return) or are explicitly set to `null`.
5. **Garbage Collection:** The JVM Garbage Collector (e.g., G1, ZGC) marks unreachable objects, executes cleanup actions (via `java.lang.ref.Cleaner`), and reclaims heap memory for future allocations.

---

## 5. Execution Instructions and Output Verification
Compile and run the program:
```bash
javac ParkingTicket.java StaticDemo.java
java StaticDemo
```

### Key Verified Results:
- Static counter increments reliably across tickets (`TICK-1001`, `TICK-1002`, `TICK-1003`).
- Instance counter failed to produce unique sequence (remained stuck at 1001 for each object).
- Static methods operated independently of object instances.
- Out-of-scope session objects were collected by `System.gc()`.
