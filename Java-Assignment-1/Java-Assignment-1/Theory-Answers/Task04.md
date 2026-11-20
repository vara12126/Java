# Task 04 – Design Classes, Objects and Constructors

**Course Outcome:** CO1 – Java fundamentals and object-oriented programming  
**Bloom's Level:** K4 (Analyse)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  

---

## 1. Class Design: Fields and Methods

Based on the object identification in Task 1, we design the core domain classes:

### Class 1: `ParkingTicket`
- **Fields:**
  - `private final int ticketId`: Unique identifier for the transaction.
  - `private final String vehicleRegNo`: License plate of the parked vehicle.
  - `private final int floor`: Allocated floor index.
  - `private final int slot`: Allocated slot index on that floor.
  - `private final long entryTime`: Timestamp (in minutes or milliseconds).
  - `private boolean isPaid`: Status flag for fee clearance.
- **Methods:**
  - Getters for all fields.
  - `markPaid()`: Updates ticket payment state.
  - `toString()`: Formatted printable receipt representation.

### Class 2: `ParkingSlot`
- **Fields:**
  - `private final int floor`: Floor index.
  - `private final int slotNumber`: Slot identifier.
  - `private boolean occupied`: Current occupancy status.
  - `private String vehicleRegNo`: Registration number of vehicle currently occupying the slot (or `null`).
- **Methods:**
  - `occupy(String vehicleRegNo)`: Validates and assigns a vehicle.
  - `vacate()`: Clears the slot and returns the vacated vehicle number.
  - `isOccupied()`, `getFloor()`, `getSlotNumber()`.

### Class 3: `ParkingFacility`
- **Fields:**
  - `private final String[][] slots`: Multi-level slot matrix.
  - `private final int totalFloors`: Count of floors.
- **Methods:**
  - `allocate(String vehicleNo)`: Finds and occupies the first vacant slot.
  - `release(int floor, int slot)`: Clears a slot after departure.
  - `isFull()`: Checks if all slots across all floors are occupied.

---

## 2. Constructors: Default, Parameterized, and Overloaded

In Java, constructors initialize newly allocated heap memory into a well-defined, consistent state:

1. **Default (No-Arg) Constructor:**
   - Automatically provided by the compiler *only* if no explicit constructor is defined.
   - Initializes fields to JVM defaults (`null`, `0`, `false`).
   - In our domain, a default constructor for `ParkingTicket` is **dangerous** because a ticket without a `ticketId`, `vehicleRegNo`, or `entryTime` is fundamentally invalid.
2. **Parameterized Constructor:**
   - Guarantees that every object is born in a complete, valid state.
   - Example: `public ParkingTicket(int ticketId, String vehicleRegNo, int floor, int slot, long entryTime)` validates inputs and sets all final fields.
3. **Overloaded Constructors:**
   - Multiple constructors in the same class sharing the same name but differing in parameter count or parameter types.
   - Allows clients to instantiate objects conveniently when default values are appropriate (e.g., automatically capturing the current system timestamp if no custom entry time is supplied).

---

## 3. Constructor Chaining and the `this` Keyword

Constructor chaining refers to calling one constructor from another within the same class using `this(...)`.
- **Constraint:** In Java, `this(...)` must be the **very first statement** in the constructor body.
- **Benefit:** Eliminates boilerplate, prevents code duplication, and routes all object initialization through a single master validation path.

### Code Example:
```java
public class ParkingTicket {
    private final int ticketId;
    private final String vehicleRegNo;
    private final int floor;
    private final int slot;
    private final long entryTime;
    private boolean isDuplicate;

    // Master Constructor (Performs validation and assignment)
    public ParkingTicket(int ticketId, String vehicleRegNo, int floor, int slot, long entryTime) {
        if (ticketId <= 0) throw new IllegalArgumentException("Invalid ticket ID");
        if (vehicleRegNo == null || vehicleRegNo.trim().isEmpty()) 
            throw new IllegalArgumentException("Vehicle RegNo cannot be blank");
        this.ticketId = ticketId;
        this.vehicleRegNo = vehicleRegNo;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
        this.isDuplicate = false;
    }

    // Overloaded Constructor: Chains to master constructor with current system time
    public ParkingTicket(int ticketId, String vehicleRegNo, int floor, int slot) {
        this(ticketId, vehicleRegNo, floor, slot, System.currentTimeMillis()); // Constructor Chaining
    }
}
```

---

## 4. Copy Constructors in Smart-Parking

### Is a Copy Constructor Needed?
**Yes.** In real-world campus operations, a driver may misplace their printed parking slip or request a duplicate receipt at the payment kiosk.
A **copy constructor** creates a new object that duplicates the state of an existing object without sharing mutable references:
```java
// Copy Constructor for Re-issuing Duplicate Ticket
public ParkingTicket(ParkingTicket original) {
    this.ticketId = original.ticketId;
    this.vehicleRegNo = original.vehicleRegNo;
    this.floor = original.floor;
    this.slot = original.slot;
    this.entryTime = original.entryTime;
    this.isDuplicate = true; // Flags this slip as a duplicate re-issue for security audit
}
```
**Why not simple assignment (`t2 = t1`)?**
Simple assignment merely copies the 64-bit reference pointer on the stack; both variables point to the exact same object on the heap. Mutating `t2` would alter `t1`. A copy constructor creates an independent object on the heap.

---

## 5. Java Memory Model: Stack vs Heap Storage

```text
       STACK MEMORY                         HEAP MEMORY
 +-----------------------+             +--------------------------------------+
 | main() Frame          |             | ParkingTicket Instance               |
 |                       |             | [Object Header: 12-16 bytes]         |
 | ticketRef ----------->|------------>| ticketId: 101                        |
 |                       |             | vehicleRegNo: ref to "TN01AB1111"    |
 | int floor = 0         |             | floor: 0, slot: 1                    |
 | int slot = 1          |             | entryTime: 1716300000000             |
 +-----------------------+             +--------------------------------------+
```

1. **Stack Memory:**
   - Stores primitive local variables (e.g., `int f = 0`, `int s = 1`) and reference variables (e.g., `ticketRef`).
   - Allocation and deallocation are instantaneous as stack frames push and pop with method calls.
2. **Heap Memory:**
   - Stores all actual object instances (e.g., `new ParkingTicket(...)`) and arrays (`new String[2][]`).
   - Objects persist in the heap until no live references point to them, at which point the Garbage Collector reclaims their memory.

---

## 6. Alternative Concept and Complexity Analysis

### Comparison of Object Creation Paradigms

1. **Constructor Chaining (Our Primary Choice):**
   - Invariants checked at birth; fields can be `final` (immutable).
2. **Default Constructor + Setters (JavaBeans pattern):**
   - `ParkingTicket t = new ParkingTicket(); t.setTicketId(101); t.setVehicleRegNo("TN01"); ...`
   - **Severe Flaw:** An object exists in an incomplete, partially-initialized state between setter calls. Thread-unsafe and prone to bugs where a setter is accidentally omitted.
3. **Static Factory Methods:**
   - `ParkingTicket t = ParkingTicket.issueFor(regNo, floor, slot);`
   - Expressive naming, encapsulates creation logic, guarantees valid object creation.

### Complexity & Resource Comparison for Creating $n$ Tickets

| Dimension | Constructor Chaining | Default Constructor + Setters | Static Factory Method |
| :--- | :--- | :--- | :--- |
| **Object Invariant Guarantee** | **Guaranteed.** Fully initialized before reference is returned. | **None.** Object is temporarily inconsistent between calls. | **Guaranteed.** Static method validates before calling private constructor. |
| **Immutability Support** | Allows `final` fields. | Cannot use `final` fields (requires setters). | Allows `final` fields. |
| **Time to Create $n$ Objects** | $O(n)$ — 1 constructor invocation per object. | $O(n)$ — 1 constructor + 5 setter method invocations per object ($6\times$ method frame overhead). | $O(n)$ — 1 static call + 1 constructor call per object (easily inlined by JIT). |
| **Heap Space for $n$ Objects** | $O(n)$ — Exactly $n \times \text{instanceSize}$ bytes. | $O(n)$ — Same instance size, but mutable fields prevent JVM escape analysis optimizations. | $O(n)$ — Same instance size; enables instance caching if needed. |
