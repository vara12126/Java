# Task 01: Problem Analysis and Object Identification

## 1. Problem Scenario Overview
The objective is to design, implement, and validate an Object-Oriented Model for a **Campus Smart-Parking System**. The system manages multi-floor parking spaces, handles diverse vehicle types (Two-Wheelers, Cars, Electric Cars, E-Bikes, Delivery Vans), allocates slots dynamically, issues unique parking tickets, tracks entry/exit timestamps, calculates tiered and special parking fees, supports campus passes (student, staff, visitor), and manages active records efficiently.

---

## 2. Input and Output Requirements

### Input Requirements
- **Vehicle Information:**
  - Vehicle Registration Number (String, standardized format, e.g., `KA01AB1234` or `TN01A1`).
  - Vehicle Category / Type (`TwoWheeler`, `Car`, `ElectricCar`, `EBike`, `DeliveryVan`).
  - Pass Status (None/Visitor, Student Monthly Pass, Staff Monthly Pass, Visitor One-Day Pass).
- **Parking Facility State:**
  - Number of floors and capacity per floor (jagged array layout).
  - Slot status (Available / Occupied).
  - Current timestamp (`LocalDateTime`).
- **User Actions:**
  - Vehicle Entry Request (allocates slot, issues ticket).
  - Vehicle Exit Request (ticket ID or registration number input).
  - Payment details (cash, pass verification).
  - Administrative queries (display grid, active tickets, daily revenue).

### Output Requirements
- **Ticket Generation:** Unique ticket ID (e.g., `TICK-1001`), entry timestamp, allocated slot coordinate `[Floor, Slot]`, vehicle registration number.
- **Exit Receipt / Billing Summary:**
  - Exit timestamp, total duration (hours/minutes).
  - Base parking fee.
  - Surcharges (e.g., peak delivery van surcharge, EV charging fee).
  - Discounts applied (student 50%, staff 100%, one-day pass).
  - Net amount payable (in INR / ₹).
- **System Telemetry:**
  - Visual floor-slot occupancy map.
  - Active tickets list.
  - Cumulative daily collection figure.
  - Clear validation error messages (e.g., `Parking Full`, `Duplicate Registration`, `Invalid Ticket ID`).

---

## 3. Candidate Classes and Class Responsibilities

| Candidate Class | Core Responsibilities | Key Attributes & Methods |
| :--- | :--- | :--- |
| `Vehicle` (Abstract) | Serves as the base abstraction for all vehicles; encapsulates registration number and vehicle type. | `regNumber`, `vehicleType`, `calculateBaseHourlyRate()`, `getRegistrationNumber()` |
| `TwoWheeler` | Represents motorcycles, scooters; applies specific two-wheeler parking rates. | `calculateBaseHourlyRate()` (₹10/hr) |
| `Car` | Represents standard internal combustion four-wheelers. | `calculateBaseHourlyRate()` (₹30/hr) |
| `ElectricCar` | Inherits from `Car`; adds EV charging logic and surcharge. | `chargingRequired`, `chargingFeePerHour`, `calculateFee()` |
| `EBike` | Subclass of `TwoWheeler` or `Vehicle`; supports flat-rate parking rules. | `batteryCapacity`, `flatRate` |
| `DeliveryVan` | Special commercial vehicle subjected to peak daytime surge pricing (9 AM–5 PM). | `cargoCapacity`, `timeWindowPricingRule` |
| `ParkingSlot` | Models an individual parking bay on a specific floor. | `floorNumber`, `slotNumber`, `isOccupied`, `allocatedVehicle`, `assignVehicle()`, `vacate()` |
| `ParkingFloor` | Represents a single floor containing an array of parking slots. | `floorNumber`, `slots[]`, `findFirstAvailableSlot()`, `getAvailableCount()` |
| `ParkingFacility` | Top-level coordinator managing multi-floor slots, allocation, deallocation, and state. | `floors[][]`, `allocateSlot()`, `releaseSlot()`, `displayOccupancy()` |
| `ParkingTicket` | Immutable record representing an active parking session. | `ticketId`, `regNumber`, `slotLocation`, `entryTime`, `exitTime`, `isPaid` |
| `Pass` (Interface) | Defines contract for student, staff, and one-day parking concessions. | `isValid(LocalDateTime)`, `getDiscountPercentage()` |
| `PricingRule` (Interface) | Strategy pattern interface for dynamic fee computation. | `computeFee(Vehicle, LocalDateTime, LocalDateTime)` |

---

## 4. Business Rules and Assumptions

1. **First-Fit Allocation:** When a vehicle enters, the system scans floor-by-floor from lowest floor (`Floor 0`) and lowest slot index (`Slot 0`) to find the first unoccupied compatible slot.
2. **Duplicate Registration Check:** A vehicle with an active parking ticket cannot be admitted again until it has formally exited.
3. **Billing Increment:** Parking fee is calculated on an hourly basis; fractional hours are rounded up (ceiling) or calculated precisely per minute/hour slab depending on rule specifications.
4. **Pass Concessions:**
   - **Student Monthly Pass:** 50% discount on base fee.
   - **Staff Monthly Pass:** 100% discount on standard parking fee.
   - **Visitor One-Day Pass:** Flat 24-hour validity with zero hourly increments.
5. **Electric Vehicle Charging:** Optional EV charging service incurs an additional flat or hourly charging surcharge.
6. **Delivery Van Peak Window:** Delivery vans parked between 09:00 AM and 05:00 PM are charged at 2x the standard car rate during those peak hours; hours outside this window are billed at standard rate.
7. **Thread/State Safety Assumption:** In this academic model, memory state is maintained during runtime; collections use appropriate maps for $O(1)$ lookups.

---

## 5. Parallel Arrays vs. Object-Oriented Architecture

In procedural programming, a multi-attribute parking record is often represented using synchronized parallel arrays:
```java
String[] regNumbers = new String[CAPACITY];
String[] vehicleTypes = new String[CAPACITY];
int[] floorNumbers = new int[CAPACITY];
int[] slotNumbers = new int[CAPACITY];
boolean[] isOccupied = new boolean[CAPACITY];
```

### Critical Flaws of Parallel Arrays:
1. **Lack of Cohesion & Encapsulation:** Data elements describing a single logical entity are scattered across independent arrays. There is no language-level guarantee that `regNumbers[i]` belongs to `slotNumbers[i]`.
2. **Fragile Mutations:** Removing or sorting an element requires manual, synchronized shifts across **all 5 arrays**. If one shift is missed or throws an exception halfway, the entire dataset suffers catastrophic desynchronization.
3. **Type Inflexibility & Scalability:** Adding a new property (e.g., `passType`, `entryTimestamp`) requires adding another array globally and touching every single allocation and release method.
4. **Poor Readability & Maintainability:** Passing 5 arrays to every service function violates clean code principles and increases bug frequency.

### Advantages of Object-Oriented Domain Entities:
1. **Encapsulation:** All attributes and mutating operations are bound together inside class definitions (`ParkingTicket`, `VehicleRecord`).
2. **Type Safety & Validation:** Constructors enforce invariants (e.g., rejecting `null` registration numbers or negative floor numbers).
3. **Polymorphic Extensibility:** New vehicle types (`ElectricCar`, `EBike`) can be introduced via inheritance without altering existing parking allocation logic.

---

## 6. Search Time and Space Complexity Analysis

| Operation | Parallel Arrays Approach | Object-Oriented Approach (with HashMap) | Explanation |
| :--- | :--- | :--- | :--- |
| **Search by Ticket ID / Reg No** | $O(N)$ Time | $O(1)$ Average Time | Parallel arrays require a linear scan across $N$ elements; HashMap utilizes hash bucket indexing. |
| **Slot Allocation (First Fit)** | $O(F \times S)$ Time | $O(F \times S)$ Time | In both cases, scanning an $F$-floor by $S$-slot grid takes proportional time to the total number of slots $M = \sum S_i$. |
| **Slot Release by Coordinate** | $O(1)$ Time | $O(1)$ Time | Direct index access via array coordinates `grid[floor][slot]`. |
| **Record Deletion on Exit** | $O(N)$ Time (Array shifting) | $O(1)$ Time (Map removal) | Shifting elements in 5 parallel arrays takes $O(N)$, whereas `activeTickets.remove(id)` is $O(1)$. |
| **Auxiliary Space Complexity** | $O(N)$ | $O(N)$ | Both require linear memory relative to the number of parked vehicles, but OOP uses structured object references in heap memory. |

---

## 7. Execution and Verification
The accompanying demonstration `ProblemAnalysisDemo.java` can be compiled and run:
```bash
javac ProblemAnalysisDemo.java
java ProblemAnalysisDemo
```
It demonstrates the side-by-side behavioral contrast and complexity benchmarks.
