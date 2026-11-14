# Task 11: Complete Smart-Parking Implementation

## 1. Overview
This task represents the complete, fully integrated production-ready Object-Oriented Smart-Parking System. It consolidates all OOP paradigms covered across Tasks 1–10 into a cohesive architecture, managing multi-floor layouts, specialized vehicle types, dynamic allocation, EV charging, campus concession passes, active ticket indexing, and daily financial auditing.

---

## 2. Implemented Features Checklist (All 20 Specifications)

1. [x] **Multi-Floor Parking Management:** Jagged array structure modeling floors with non-uniform capacities.
2. [x] **Two-Wheeler Parking:** Standard ₹10/hr base rate.
3. [x] **Car Parking:** Standard ₹30/hr base rate.
4. [x] **Electric Vehicle Parking:** Dedicated EV charging bays with ₹25/hr charging surcharge.
5. [x] **Automatic Parking Slot Allocation:** First-Fit search prioritizing EV bays for charging vehicles.
6. [x] **Unique Ticket Generation:** Class-level atomic sequential ID generator (`TICK-1001`, `TICK-1002`).
7. [x] **Entry Date and Time Recording:** High-precision `LocalDateTime` timestamping.
8. [x] **Exit Processing:** Full automated checkout computing stay and releasing bays.
9. [x] **Parking Duration Calculation:** Exact minute tracking converted to ceiling billing slabs.
10. [x] **Parking Fee Calculation:** Exact financial arithmetic using `BigDecimal`.
11. [x] **EV Charging Fee Calculation:** Billed dynamically based on charging duration.
12. [x] **Student Monthly Pass Validation:** Automatic 50% concession on base parking fee.
13. [x] **Staff Monthly Pass Validation:** 100% concession on base parking fee.
14. [x] **Visitor Full-Fee Calculation:** Standard un-discounted fee calculation.
15. [x] **Parking Slot Release:** Instant release of physical bay upon vehicle exit.
16. [x] **Active Ticket Management:** Dual `HashMap` indexing by Registration Number and Ticket ID for $O(1)$ lookups.
17. [x] **Duplicate Registration Validation:** Rejects admission if vehicle is already parked inside.
18. [x] **Invalid Input Handling:** Defensive regex sanitization and exception interception.
19. [x] **Parking-Full Notification:** Graceful system alert when facility capacity is exhausted.
20. [x] **Daily Collection Calculation:** Cumulative financial tally maintaining exact running revenue.

---

## 3. Class Architecture

```text
Task11_Complete_Implementation/
├── PassType.java                 (Enum defining pass concessions)
├── Vehicle.java                  (Abstract base vehicle with regex validation)
├── TwoWheeler.java               (Subclass with Rs 10/hr rate)
├── Car.java                      (Subclass with Rs 30/hr rate)
├── ElectricCar.java              (Subclass with EV charging surcharge)
├── ParkingSlot.java              (Physical parking bay entity)
├── ParkingTicket.java            (Immutable parking session & BigDecimal billing)
├── ParkingFacility.java          (Facility coordinator managing floors and state)
├── SmartParkingApp.java          (Interactive menu-driven console interface)
├── SmartParkingTestRunner.java   (Automated test harness verifying all 20 specs)
└── README.md
```

---

## 4. Comprehensive Test Execution Table

The automated test suite `SmartParkingTestRunner` was executed against all 20 functional specifications:

| ID | Feature Required | Scenario Tested | Expected Result | Actual Result | Status |
| :---: | :--- | :--- | :--- | :--- | :---: |
| 1 | Multi-Floor Garage | 3 floors initialized: `[2, 2, 1]` | Total capacity = 5 slots | Available = 5 slots | **PASS** |
| 2 | Two-Wheeler Entry | Park `TN01A1001` (TwoWheeler) | Assigned Bay, Rate = ₹10.0/hr | Assigned F0:S0, Rate = 10.0 | **PASS** |
| 3 | Car Entry | Park `KA02B2002` (Car) | Assigned Bay, Rate = ₹30.0/hr | Assigned F0:S1, Rate = 30.0 | **PASS** |
| 4 | EV Charging Entry | Park `DL03C3003` requiring EV plug | Assigned EV Bay, Charging = ₹25.0 | Assigned F1:S0, Charging = 25.0 | **PASS** |
| 5 | Auto Allocation | Sequential First-Fit Allocation | Allocated in lowest vacant slot | tBike @ F0:S0, tCar @ F0:S1 | **PASS** |
| 6 | Unique Tickets | Monotonic ticket ID progression | `ticketId1 != ticketId2` | TICK-1001 != TICK-1002 | **PASS** |
| 7 | Entry Timestamp | Capture exact `LocalDateTime` | Match simulated arrival clock | Match: 2026-11-14T09:00 | **PASS** |
| 8 | Exit Processing | Settle ticket upon vehicle exit | `active == false` | `active == false` | **PASS** |
| 9 | Duration Calculation | 150 minutes parking duration | 150 minutes | 150 minutes (3 billable hrs) | **PASS** |
| 10 | Parking Fee Calculation | TwoWheeler 3 billable hrs @ ₹10 | ₹30.00 | ₹30.00 | **PASS** |
| 11 | EV Charging Fee | EV 2 hrs: ₹60 base + ₹50 charging | ₹110.00 | ₹110.00 | **PASS** |
| 12 | Student Pass (50%) | Car 2 hrs (₹60 base) with Student Pass | ₹30.00 (50% concession) | ₹30.00 (Discount: ₹30.00) | **PASS** |
| 13 | Staff Pass (100%) | Car 4 hrs (₹120 base) with Staff Pass | ₹0.00 (100% concession) | ₹0.00 (Discount: ₹120.00) | **PASS** |
| 14 | Visitor Full Fee | Car 3 hrs with No Pass | ₹90.00 (0% discount) | ₹90.00 | **PASS** |
| 15 | Slot Release | Free bay immediately upon exit | Slots: Before=5, During=4, After=5 | Slots restored to 5 | **PASS** |
| 16 | Active Ticket Map | $O(1)$ HashMap lookup by Reg Number | Exit processed by Reg Number | Exit by Reg: Success | **PASS** |
| 17 | Duplicate Registration | Attempt duplicate vehicle entry | `IllegalStateException` thrown | Duplicate caught = true | **PASS** |
| 18 | Invalid Input Defenses | Malformed registration string | `IllegalArgumentException` thrown | Invalid caught = true | **PASS** |
| 19 | Parking Full Notice | Attempt allocation when 0 slots left | Returns `null` (Full Alert) | Result = `null` (FULL) | **PASS** |
| 20 | Daily Collection | Running sum across all settled tickets | Cumulative revenue tallied | Accumulated = ₹260.00 | **PASS** |

---

## 5. Execution Instructions
### Running the Automated Test Suite:
```bash
javac *.java
java SmartParkingTestRunner
```

### Running the Interactive Console Application:
```bash
java SmartParkingApp
```
Menu Options:
```
1. Vehicle Entry
2. Vehicle Exit
3. Display Parking Slots
4. View Active Tickets
5. Calculate Daily Collection
6. Exit Application
```
