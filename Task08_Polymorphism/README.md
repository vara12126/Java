# Task 08: Polymorphism

## 1. Overview
Polymorphism ("many forms") enables a uniform programming interface to manipulate objects of varying underlying types. In Java, this is realized via:
1. **Compile-Time Polymorphism (Static Binding / Method Overloading)**
2. **Runtime Polymorphism (Dynamic Binding / Method Overriding / Dynamic Method Dispatch)**

---

## 2. Compile-Time vs. Runtime Polymorphism

| Feature | Compile-Time Polymorphism | Runtime Polymorphism |
| :--- | :--- | :--- |
| **Mechanism** | Method Overloading (`calculateFee(int)`, `calculateFee(int, double)`) | Method Overriding (`calculateFee(int)`) |
| **Binding Time** | Compile time (compiler checks argument count/types) | Runtime (JVM inspects the actual heap object type) |
| **Performance** | Slightly faster (direct offset invocation) | Dispatched via Virtual Method Table (vtable) |
| **Flexibility** | Provides convenient convenience overloads | Enables extensible frameworks (Open-Closed Principle) |

---

## 3. Dynamic Method Dispatch Mechanics
When executing:
```java
Vehicle[] vehicles = new Vehicle[] {
    new TwoWheeler("TN01A1"),
    new Car("TN02B2"),
    new ElectricCar("TN03C3")
};

for (Vehicle v : vehicles) {
    v.calculateFee(hours);
}
```
1. The declared reference type is `Vehicle`.
2. At runtime, the JVM looks up the target object in the heap.
3. If the object is a `TwoWheeler`, it invokes `TwoWheeler.calculateFee()`.
4. If the object is an `ElectricCar`, it invokes `ElectricCar.calculateFee()`.
This decoupling ensures that client code interacts strictly with the `Vehicle` or `Chargeable` contract without worrying about subtype specifics.

---

## 4. Expected vs. Actual Output Verification

The assignment specifically requires `FeeDemo` to produce the following output:
```text
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```

### Breakdown of Calculation for `hours = 3`:
- **TwoWheeler (`TN01A1`):** $3 \times \text{Rs } 10.0 = \text{Rs } 30.0$
- **Car (`TN02B2`):** $3 \times \text{Rs } 30.0 = \text{Rs } 90.0$
- **ElectricCar (`TN03C3`):** $(3 \times 30.0 \text{ car rate}) + (3 \times 40.0 \text{ EV charging rate}) = \text{Rs } 210.0$
- **Dynamic Interface Check:** `vehicles[2] instanceof Chargeable` evaluates to `true`.

### Terminal Output:
```
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```
The output matches the assignment requirement 100%.
