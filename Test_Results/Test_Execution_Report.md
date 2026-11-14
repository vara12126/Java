# Test Execution Report - Campus Smart-Parking System

**Date:** 2026-11-14  
**Java Version:** Oracle JDK 25.0.2 (LTS)  
**Test Suite:** CSA09 Comprehensive Scenario Harness  
**Status:** **19 / 19 PASSED (100% SUCCESS)**

---

## 1. Complete Test Matrix

| ID | Test Scenario | Description | Expected Output | Actual Output | Status |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **01** | **Normal Vehicle Entry** | Park standard car in first available slot | Allocated Bay Floor 0, Slot 0 | Allocated Bay Floor 0, Slot 0 (Ticket: TICK-1001) | **PASS** |
| **02** | **Normal Vehicle Exit** | Exit car after 2 hours (2 × ₹30) | Active=false, Net Fee=₹60.00 | Active=false, Net Fee=Rs 60.00 | **PASS** |
| **03** | **Last Available Parking Slot** | Park vehicle when only 1 slot remains | Assigned to last remaining bay (Floor 2, Slot 0) | Assigned to Floor 2, Slot 0 | **PASS** |
| **04** | **Parking Facility Full** | Attempt entry when all slots occupied | Returns null (Parking Full Alert) | Returns null (Facility Full Alert) | **PASS** |
| **05** | **Zero-Minute Parking** | Vehicle exits immediately at entry timestamp | Duration=0 mins, Fee=₹0.00 | Duration=0 mins, Fee=Rs 0.00 | **PASS** |
| **06** | **Fee-Slab Boundary** | 61 minutes duration (crosses 1-hr boundary into 2nd hour) | Ceiling 2 hours @ ₹30 = ₹60.00 | Duration=61 mins, Fee=Rs 60.00 | **PASS** |
| **07** | **Invalid Registration** | Alphanumeric validation rejecting special characters | IllegalArgumentException intercepted | Caught exception = true | **PASS** |
| **08** | **Duplicate Registration** | Rejecting vehicle with active session | IllegalStateException intercepted | Caught duplicate = true | **PASS** |
| **09** | **Invalid Slot / ID Lookup** | Attempt exit on non-existent registration or slot | NoSuchElementException intercepted | Caught invalid lookup = true | **PASS** |
| **10** | **EV Charging Fee** | EV car 3 hours: ₹90 base + ₹75 charging | Net Fee = ₹165.00 | Net Fee = Rs 165.00 (Charging Fee: Rs 75.00) | **PASS** |
| **11** | **Student Monthly Pass** | 4 hours stay (₹120) with Student Pass (50% off) | Net Fee = ₹60.00 | Net Fee = Rs 60.00 (Discount: Rs 60.00) | **PASS** |
| **12** | **Staff Monthly Pass** | 6 hours stay (₹180) with Staff Pass (100% off) | Net Fee = ₹0.00 | Net Fee = Rs 0.00 (Discount: Rs 180.00) | **PASS** |
| **13** | **Visitor Parking** | 5 hours stay with standard visitor tariff | Net Fee = ₹150.00 | Net Fee = Rs 150.00 | **PASS** |
| **14** | **E-Bike Flat Fee** | Flat fee for both 1 hr and 8 hrs | Fee = ₹20.00 for both stays | 1h: Rs 20.00, 8h: Rs 20.00 | **PASS** |
| **15** | **Delivery Van (9 AM–5 PM)** | 3 hours strictly within peak window @ ₹60/hr | Net Fee = ₹180.00 | Net Fee = Rs 180.00 | **PASS** |
| **16** | **Delivery Van Outside Peak** | 2 hours strictly outside peak window @ ₹30/hr | Net Fee = ₹60.00 | Net Fee = Rs 60.00 | **PASS** |
| **17** | **Delivery Van Crossing Boundaries** | 08:00 to 11:00 (1h off-peak + 2h peak split) | Net Fee = ₹150.00 | Net Fee = Rs 150.00 | **PASS** |
| **18** | **Visitor One-Day Pass** | 8 hours parking with 24-hr prepaid pass | Net Fee = ₹0.00 (100% hourly fee waived) | Net Fee = Rs 0.00 | **PASS** |
| **19** | **5 Floors Unequal Capacities** | Capacities: `[6, 5, 4, 3, 2]` = 20 total bays | 5 floors correctly sized and verified | Floors count=5, Capacities: [6,5,4,3,2] | **PASS** |

---

## 2. Summary of Test Execution
- **Total Test Scenarios Executed:** 19
- **Total Tests Passed:** 19 (100.0%)
- **Total Tests Failed:** 0 (0.0%)
- **All Edge Cases Verified:** Boundaries, concurrency/duplicates, invalid strings, time window splits, EV rates, passes, non-uniform jagged floor arrays.
