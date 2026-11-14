# Task 04: Classes, Objects and Constructors

## 1. Overview
Object-Oriented Programming models real-world business domains using classes as blueprints and objects as runtime instances. This task examines object initialization, constructor overloading, constructor chaining via `this(...)`, copy constructors, static factory methods, and the relationship between JVM Stack frames and the JVM Heap.

---

## 2. Constructor Types Implemented

### 2.1 Default (No-Argument) Constructor
Initializes an object with sensible system defaults:
```java
public Vehicle() {
    this("UNKNOWN", "Standard", "Guest"); // Delegated chaining
}
```

### 2.2 Parameterized and Overloaded Constructors
Allows flexible instantiations depending on the caller's known data:
```java
public Vehicle(String regNumber, String vehicleType) {
    this(regNumber, vehicleType, "Guest");
}

public Vehicle(String regNumber, String vehicleType, String ownerName) {
    this.registrationNumber = regNumber;
    this.vehicleType = vehicleType;
    this.ownerName = ownerName;
}
```

### 2.3 Constructor Chaining with `this(...)`
Constructor chaining eliminates duplicated initialization code. The overloaded constructors delegate their execution to the canonical master constructor using `this(...)` as the very first statement.

### 2.4 Copy Constructor
In Java, object assignment `v2 = v1` copies the reference pointer, **not** the object contents. A copy constructor explicitly instantiates a separate, independent object in heap memory:
```java
public Vehicle(Vehicle other) {
    this.registrationNumber = other.registrationNumber;
    this.vehicleType = other.vehicleType;
    this.ownerName = other.ownerName;
}
```

---

## 3. JVM Memory Model: Stack vs. Heap

```text
       STACK MEMORY                               HEAP MEMORY
  (Thread Execution Frames)                 (Dynamically Allocated Objects)
+---------------------------+             +-------------------------------+
| Frame: main()             |             | Vehicle Object #1             |
|   vFull ------------------------------> |   registration: "TN02BB2222"  |
|                           |             |   vehicleType:  "TwoWheeler"  |
|   vRefAlias --------------------------^ |   ownerName:    "Dr. Sharma"  |
|   (Identical Heap Pointer)|             +-------------------------------+
|                           |
|   vCloned ----------------------------> +-------------------------------+
|   (New Heap Pointer)      |             | Vehicle Object #2 (Cloned)    |
|                           |             |   registration: "TN02BB2222"  |
|                           |             |   vehicleType:  "TwoWheeler"  |
|                           |             |   ownerName:    "Dr. Sharma"  |
+---------------------------+             +-------------------------------+
```

- **Stack:** Stores method call frames, local variables, and object reference pointers (`vFull`, `vRefAlias`, `vCloned`). Stack memory is automatically reclaimed when the method scope exits.
- **Heap:** Stores actual instantiated object payloads (all class fields). Objects persist on the heap until the Garbage Collector detects they are unreachable.
- **Reference Copy vs. Deep Clone:** Modifying `vRefAlias.setOwnerName(...)` alters the shared object at that heap address, directly affecting `vFull`. Modifying `vCloned` does not affect `vFull` because it points to an isolated heap block.

---

## 4. Architectural Comparison: Constructors vs. Setters vs. Static Factory Methods

| Criteria | Constructor (`new Class(...)`) | Setters (`obj.setX(...)`) | Static Factory Method (`Class.create(...)`) |
| :--- | :--- | :--- | :--- |
| **Object Mutability** | Can enforce immutability (`final` fields) | Requires mutable object state | Can enforce immutability and return cached instances |
| **Readability** | Parameter intent can be ambiguous if types match | Verbose; requires multiple method calls | Highly descriptive naming (e.g., `createElectricCar(...)`) |
| **Atomic State** | Ensures object is fully valid upon return | Object can be in a partially initialized, invalid state | Can perform pre-validation and return subtypes |
| **Subtyping** | Can only return the exact declared class type | Operates only on existing instance | Can return any subclass or interface implementation |

---

## 5. Execution and Verification
Compile and execute `ConstructorDemo.java`:
```bash
javac Vehicle.java ParkingTicket.java ParkingFacility.java ConstructorDemo.java
java ConstructorDemo
```
All constructor patterns, memory reference comparisons, and static factory instantiations execute without warnings or errors.
