# Task 07 – Override and Specialise Behaviour

**Course Outcome:** CO1 – Java fundamentals and object-oriented programming  
**Bloom's Level:** K4 (Analyse)  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  

---

## 1. Overriding and Behaviour Specialisation

Method overriding occurs when a subclass provides a specific implementation for an instance method already declared in its superclass. In our smart-parking system, overriding enables each vehicle type to specialize its pricing logic while allowing the parking facility to treat all vehicles uniformly.

### Methods Overridden in the Design:
1. **`ratePerHour()`:**
   - Declared as `protected abstract double ratePerHour();` in `Vehicle`.
   - Overridden in `TwoWheeler` to return Rs 10.0/hr.
   - Overridden in `Car` to return Rs 30.0/hr.
   - Inherited by `ElectricCar` from `Car` (setting the base parking rate at Rs 30.0/hr).
   - **Why needed:** Each vehicle class encapsulates its own pricing tariff. The base `calculateFee(int minutes)` method in `Vehicle` invokes `ratePerHour()` using dynamic dispatch without needing to know which vehicle it is dealing with.
2. **`toString()`:**
   - Overridden from `java.lang.Object` to produce readable diagnostic information (`"TwoWheeler TN01A1"`) instead of memory hash codes (`Vehicle@7d4991adf`).
3. **`equals(Object o)` and `hashCode()`:**
   - Overridden to enforce business identity: two vehicle objects with the same registration number represent the exact same vehicle.

---

## 2. Rules of Method Overriding and the `@Override` Annotation

For a valid override in Java, the compiler strictly enforces these rules:
1. **Exact Signature Match:** The method name, parameter count, and parameter types must be identical.
2. **Return Type (Covariant Returns):** The return type must be either identical or a subtype (covariant) of the superclass method's return type.
3. **Access Privilege:** The subclass method cannot assign weaker access privileges (e.g., if superclass is `protected`, subclass can be `protected` or `public`, but **cannot** be `private` or default/package-private).
4. **Checked Exceptions:** The overriding method cannot throw new or broader checked exceptions than those declared in the superclass.
5. **Private and Static Methods Cannot be Overridden:** `private` methods are not visible to subclasses, and `static` methods undergo method hiding, not overriding.

### Purpose of `@Override`:
- It is a compile-time safety check. If the developer mistypes a parameter type or method name (e.g., `rateperHour()` instead of `ratePerHour()`), the compiler issues an immediate error: *"method does not override or implement a method from a supertype"*.

---

## 3. Justified Usage of the `final` Keyword

The `final` keyword enforces immutability and prevents unintended modification:
1. **`final` Variables:**
   - Applied to `private final String regNo` in `Vehicle`. Once a vehicle object is constructed, its license plate can never be altered.
   - Applied to slot coordinates `final int floor`, `final int slot` in `ParkingSlot`.
2. **`final` Methods:**
   - Applied to `public final String getRegNo()`: Subclasses should never alter the fundamental identity retrieval logic.
   - Applied to template methods (e.g., `calculateFee(int minutes)`) if the core ceiling algorithm must be globally preserved and unmodifiable by subclasses.
3. **`final` Classes:**
   - Applied to utility classes or immutable leaf classes that should never be subclassed (preventing unauthorized extension or security exploits).

---

## 4. Overriding `toString()`, `equals()` and `hashCode()`

In Java, default `equals()` inherited from `Object` uses reference identity (`this == o`). Under that rule, two distinct objects with the exact same license plate `"TN02B2"` would be treated as unequal:
```java
new Car("TN02B2").equals(new Car("TN02B2")) // would evaluate to false!
```
To fix this, we override `equals()` and `hashCode()` to obey the **Business Key Identity**:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true; // Performance optimization for identical references
    return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
}

@Override
public int hashCode() {
    return regNo.hashCode(); // Consistent with equals contract
}
```

### The `equals()` / `hashCode()` Contract:
- If `v1.equals(v2)` is `true`, then `v1.hashCode() == v2.hashCode()` **must** be true.
- Overriding `hashCode()` using `regNo.hashCode()` ensures that when `Vehicle` objects are placed in hash-based data structures (`HashSet`, `HashMap`), lookup and deduplication work seamlessly in $O(1)$ time.

---

## 5. Alternative Concept and Complexity Analysis

### Method Overriding vs Central `switch` Statement

Suppose method overriding were discarded, and fee calculation were implemented inside a single `calculateFee()` method using a `switch` on a vehicle type string/enum:

```java
// Alternative Procedural Approach with switch
public double calculateFee(String type, int minutes) {
    int hours = (minutes + 59) / 60;
    double rate;
    switch (type) {
        case "TwoWheeler": rate = 10.0; break;
        case "Car":        rate = 30.0; break;
        case "ElectricCar":rate = 30.0; break;
        case "EBike":      rate = 15.0; break;
        case "DeliveryVan":rate = 60.0; break;
        default: throw new IllegalArgumentException("Unknown type");
    }
    return hours * rate;
}
```

### Comparison Matrix

| Evaluation Metric | Method Overriding (OOP) | Central `switch` Statement (Procedural) |
| :--- | :--- | :--- |
| **Execution Time for $k$ Types** | **$O(1)$ constant time.** JVM uses a virtual method table (`vtable`) lookup via a direct index jump. | $O(1)$ to $O(k)$ branching or table lookup (`tableswitch` / `lookupswitch`). |
| **Open-Closed Principle (OCP)** | **Strictly obeyed.** Adding a new vehicle type requires only adding a new subclass. Zero existing classes are modified. | **Violated.** Adding a $(k+1)$-th vehicle type forces developers to locate and modify every single `switch` statement across the entire codebase. |
| **Classes to Change on New Type** | **0 existing classes** (1 new subclass created). | **1 to many existing service classes** must be edited, recompiled, and retested. |
| **Risk of Regression Bugs** | Minimal (new code isolated in new file). | High (editing shared core methods risks introducing bugs into existing vehicle calculations). |
