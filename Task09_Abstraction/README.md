# Task 09: Abstraction

## 1. Overview
Abstraction is the process of hiding internal implementation complexity and exposing only essential functional characteristics to the outside world. In Java, abstraction is achieved via **Abstract Classes** (partial abstraction) and **Interfaces** (pure behavioral contracts).

---

## 2. Abstract Class vs. Interface: Architectural Comparison

| Dimension | Abstract Class (`abstract class Vehicle`) | Interface (`interface Chargeable`, `Trackable`) |
| :--- | :--- | :--- |
| **Primary Intent** | Models an identity archetype ("what an entity is") | Models a behavioral capability ("what an entity can do") |
| **State / Fields** | Can maintain instance variables, protected state, mutable fields | Only `public static final` constants |
| **Constructors** | Can define constructors called by subclasses via `super(...)` | Cannot have constructors (cannot be instantiated) |
| **Inheritance Model** | **Single inheritance** (`class Car extends Vehicle`) | **Multiple inheritance** (`implements Chargeable, Trackable`) |
| **Method Implementation** | Mix of fully concrete and `abstract` methods | Abstract methods, `default` methods, and `static` methods |
| **Access Modifiers** | `public`, `protected`, package-private, `private` | Methods are implicitly `public` |

---

## 3. Why Both Are Appropriate in Smart-Parking System

In the Campus Smart-Parking System:
1. **`abstract class Vehicle`** represents core entity taxonomy. Every vehicle has a permanent state (`registrationNumber`, `vehicleType`, `brand`) and common initialization logic that must execute via constructors.
2. **`interface Chargeable`** represents a purely financial contract. Any billable entity (e.g., vehicles, lost tickets, locker rentals) can implement `Chargeable` regardless of its position in the vehicle class hierarchy.
3. **`interface Trackable`** represents an IoT telematics contract for RFID gate sensors. A vehicle implements `Trackable` without complicating its financial billing logic.

This adheres strictly to the **Interface Segregation Principle (ISP)** and **Single Responsibility Principle (SRP)**.

---

## 4. Modern Interface Features Demonstrated (Java 8+)

### 4.1 Default Methods
Default methods allow interfaces to evolve and provide fallback business logic without breaking existing implementing classes:
```java
default double calculateDiscountedFee(int hours, double discountPercent) {
    double baseFee = calculateFee(hours);
    return baseFee - (baseFee * (discountPercent / 100.0));
}
```

### 4.2 Static Interface Methods
Static methods provide utility functions bound directly to the interface namespace:
```java
static String getCurrencySymbol() {
    return "Rs";
}
```

---

## 5. Execution Instructions and Verification
Compile and run the abstraction demonstration:
```bash
javac Chargeable.java Trackable.java Vehicle.java TwoWheeler.java Car.java ElectricCar.java AbstractionDemo.java
java AbstractionDemo
```
All static utilities, default methods, polymorphic zone lookups, and multiple-interface capabilities execute with 0 errors.
