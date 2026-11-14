# Task 10: Packages and Encapsulation

## 1. Overview
Encapsulation is the fundamental OOP mechanism of wrapping data (state) and methods (behavior) within a single unit, shielding an object's internal representation from arbitrary external tampering. In Java, encapsulation is governed by **Packages** and **Access Modifiers**.

---

## 2. Package Architecture

```text
Task10_Encapsulation_Packages/
└── parking/
    ├── model/
    │   ├── VehicleType.java       (Enum: vehicle categories)
    │   ├── Vehicle.java           (Abstract model with private/protected/package-private members)
    │   ├── TwoWheeler.java        (Specialized model)
    │   ├── Car.java               (Specialized model)
    │   ├── ElectricCar.java       (Specialized model)
    │   ├── ParkingSlot.java       (Encapsulated physical slot)
    │   └── ParkingTicket.java     (Active session entity)
    ├── service/
    │   ├── ValidationService.java (Input sanitization, autoboxing wrappers)
    │   └── ParkingService.java    (Coordinates allocation, exit, defensive copying)
    └── app/
        └── EncapsulationDemo.java (Application entry point)
```

---

## 3. Java Access Modifiers in Practice

| Modifier | Keyword | Accessibility Scope | Practical Usage in Smart Parking |
| :--- | :--- | :--- | :--- |
| **Private** | `private` | Class scope only | Fields like `registrationNumber`, `slots[][]`, `activeTickets`. Restricts direct mutation. |
| **Package-Private** | *(no modifier)* | Same package only | `internalAuditCode` in `Vehicle`: accessible to model entities for telemetry, hidden from services. |
| **Protected** | `protected` | Same package & any subclass | `securityTag` in `Vehicle`: accessible to `TwoWheeler` and `Car` subclasses even if in other packages. |
| **Public** | `public` | Universal (any package) | Public API methods: `parkVehicle()`, `exitVehicle()`, `getRegistrationNumber()`. |

---

## 4. Input Validation and Invariant Preservation

Setters and constructors enforce strict business invariants:
- **Registration Number:** Verified using regex `^[A-Z0-9]{4,12}$` preventing invalid, malformed, or SQL/script-injected inputs.
- **Negative Dimensions:** `ParkingSlot` constructor prevents negative floor or slot coordinates (`IllegalArgumentException`).
- **Occupancy Invariants:** `assignVehicle()` throws `IllegalStateException` if a slot is already occupied, avoiding slot overwrite collisions.

---

## 5. Wrapper Classes and Autoboxing / Unboxing

In `ValidationService.java`:
```java
public static Double calculateValidatedFee(Integer durationHours, Double hourlyRate, Boolean hasDiscount)
```
- **Autoboxing:** Automatic conversion of primitive types (`int`, `double`, `boolean`) into their corresponding object wrapper classes (`Integer`, `Double`, `Boolean`).
- **Unboxing:** Automatic conversion of wrapper objects back into primitive representations during arithmetic calculations (`baseFee = durationHours * hourlyRate`).
- **Null Safety:** Wrapper classes allow `null` checks before arithmetic processing to prevent `NullPointerException`.

---

## 6. Why Public Mutable Fields are Dangerous

If fields like `isOccupied` or `billedAmount` were declared `public`:
1. **Broken Invariants:** External client code could execute `ticket.billedAmount = 0.0;` or `slot.isOccupied = false;` without vacating the vehicle, corrupting the grid state.
2. **Loss of Audit Trail:** No opportunity to validate data, trigger events, or compute telemetry upon value change.
3. **Fragile Coupling:** Any refactoring of internal data structures (e.g., changing from `boolean[][]` to `List<Floor>`) would break all external client code.

---

## 7. Compilation and Execution
Compile from the `Task10_Encapsulation_Packages` folder:
```bash
javac parking/model/*.java parking/service/*.java parking/app/*.java
java parking.app.EncapsulationDemo
```
All package boundaries, validation defenses, autoboxing routines, and unmodifiable collections execute cleanly.
