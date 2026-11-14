# Task 07: Method Overriding

## 1. Overview
Method Overriding allows a subclass to provide a specialized implementation of a method already declared in its superclass. This task explores runtime method resolution, the `@Override` annotation, the `equals()` and `hashCode()` contracts for entity identity, the `final` keyword, and a design comparison between polymorphic overriding and procedural switch statements.

---

## 2. Method Overriding in Action

### 2.1 The `@Override` Annotation
The `@Override` annotation instructs the Java compiler to verify that the method signature exactly matches a method in the superclass. If the parameter types or return type mismatch occurs, the compiler generates an error, preventing silent bugs caused by accidental overloading.

```java
// In TwoWheeler.java
@Override
public double calculateFee(int hours) {
    return hours * 10.0;
}

// In ElectricCar.java
@Override
public double calculateFee(int hours) {
    return super.calculateFee(hours) + (chargingRequired ? hours * 20.0 : hours * 5.0);
}
```

---

## 3. The `equals()` and `hashCode()` Contract
In real-world parking operations, a vehicle is identified uniquely by its **Registration Number**, not by its heap memory address (`this == obj`).

### 3.1 Logical Equality Implementation
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || !(o instanceof Vehicle)) return false;
    Vehicle vehicle = (Vehicle) o;
    return Objects.equals(this.registrationNumber, vehicle.registrationNumber);
}

@Override
public int hashCode() {
    return Objects.hash(registrationNumber);
}
```

### 3.2 The General Contract of `hashCode`:
1. **Consistency:** Multiple invocations on the same object must consistently return the same integer provided state hasn't changed.
2. **Equality Implication:** If `v1.equals(v2)` evaluates to `true`, then `v1.hashCode()` **must equal** `v2.hashCode()`.
3. **Collision Resistance:** Unequal objects should ideally yield distinct integer hash values to maintain $O(1)$ performance in `HashMap` and `HashSet`.

When these methods are overridden properly, a `HashSet<Vehicle>` immediately rejects duplicate registrations upon entry!

---

## 4. The `final` Keyword in Java

| Final Application | Usage in Parking Domain | Architectural Impact |
| :--- | :--- | :--- |
| **`final` Variable** | `protected final String registrationNumber;` | Makes the registration number immutable after vehicle construction. Prevents tampering. |
| **`final` Method** | `public final String getRegistrationNumber()` | Locks the identity accessor so subclasses cannot override or manipulate how vehicle identity is exposed. |
| **`final` Class** | `public final class ElectricCar extends Car` | Closes the class against further extension, ensuring no unintended subclasses break specialized EV battery and charging safety logic. |

---

## 5. Architectural Comparison: Method Overriding vs. Switch-Case

| Evaluation Metric | Polymorphic Method Overriding | Procedural Switch-Case Statement |
| :--- | :--- | :--- |
| **Open-Closed Principle (OCP)** | **Compliant**: Adding a new vehicle type (`EBike`, `DeliveryVan`) requires creating a new subclass without touching existing code. | **Violated**: Adding a new vehicle requires searching and editing every switch block across the entire application codebase. |
| **Encapsulation of Logic** | Rate calculation logic lives inside the respective vehicle domain class. | Rate calculation logic is leaked and scattered inside procedural utility functions. |
| **Code Smells** | Clean, modular, easy unit testing. | Type-checking antipatterns (`switch(type)`, `instanceof` cascades). |
| **Performance** | Resolved in $O(1)$ via JVM Virtual Method Table (vtable) dispatch. | Evaluated via bytecode table-switch or string hash lookups. |

---

## 6. Execution Instructions and Verification
Compile and run the program:
```bash
javac Vehicle.java TwoWheeler.java Car.java ElectricCar.java OverridingDemo.java
java OverridingDemo
```

### Key Output Verified:
- Overridden `calculateFee()` correctly bills ₹40 for TwoWheeler, ₹120 for Car, and ₹200 for ElectricCar.
- `car1.equals(car2)` evaluates to `true` despite being separate heap instances.
- `HashSet` rejected `car2`, verifying that duplicate entry prevention functions correctly.
