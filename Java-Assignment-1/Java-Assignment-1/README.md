# Campus Smart-Parking System — Java Assignment 1

**Course Title:** Java Programming  
**Course Outcome:** CO1 – Java fundamentals and object-oriented programming  
**Assignment Title:** Design, Implement and Validate an Object-Oriented Model for a Campus Smart-Parking System  
**Student Name:** Kattiripalli Dhanush  
**Register Number:** 192311094  
**GitHub Repository:** [https://github.com/Dhanushssmb/java.git](https://github.com/Dhanushssmb/java.git)  
**Java Version:** Java SE 27 (Java 11+ syntax compliant)  

---

## 1. Project Introduction

The **Campus Smart-Parking System** is an enterprise-grade object-oriented solution designed for a modern university campus. The system manages multi-level parking operations accommodating various vehicle categories (**Two-Wheelers**, **Passenger Cars**, and **Electric Vehicles (EVs)**) driven by diverse user groups (**Students**, **Staff/Faculty**, and **Visitors**).

The solution automates ticket issuance, parking bay allocation across irregular floor matrices, vehicle identification, EV charging tariff calculation, and checkout fee settlement adhering strictly to the university's ceiling-hour billing rules and monthly/daily pass exemptions.

---

## 2. Problem Statement

A university operates a multi-level parking facility spanning multiple floors, where each floor contains a fixed or variable number of numbered slots. As vehicles enter, the facility must:
1. Search and allocate an available slot on the lowest possible floor.
2. Generate an immutable, sequentially numbered parking ticket.
3. Record entry and exit timestamps.
4. Calculate parking charges using integer ceiling arithmetic (every started hour billed in full).
5. Apply specialized billing rules (free parking for monthly pass holders, separate charging rates for EVs, flat fees for e-bikes, and daytime surge pricing for delivery vans).
6. Release slots upon departure and handle edge conditions gracefully.

---

## 3. Project Objectives

- **Decomposition & Modeling:** Apply noun-verb analysis to decompose the system into cohesive domain entities.
- **Data Representation:** Select memory-efficient and structurally sound data structures (2D jagged arrays) and prevent integer division precision loss.
- **Control Logic & Algorithmic Rigor:** Implement short-circuit slot search algorithms and formulate standardized algorithmic pseudocode (SIMATS Engineering Standard).
- **Class Design & Memory Architecture:** Implement robust constructors with constructor chaining (`this()`), copy constructors, and factory methods while understanding JVM stack/heap storage.
- **Static Members & Lifecycle:** Utilize static class members for collision-free global counters and analyze object reachability and garbage collection.
- **Inheritance & Reusability:** Design a clean inheritance taxonomy using hierarchical and multilevel inheritance (`Vehicle` $\rightarrow$ `Car` $\rightarrow$ `ElectricCar`).
- **Overriding & Business Key Identity:** Specialize tariffs via `@Override` and enforce identity equality based on registration numbers (`equals()` and `hashCode()`).
- **Polymorphism:** Eliminate fragile conditional cascades using dynamic method dispatch and method overloading.
- **Abstraction:** Decouple behavior using abstract base classes and capability interfaces (`Chargeable`).
- **Encapsulation:** Shield internal states via private fields, validating mutators, and structured packages (`parking.model`, `parking.service`, `parking.app`).
- **Comprehensive Validation:** Execute rigorous automated test suites covering boundary, invalid, and hand-calculated financial scenarios.
- **Day-4 Adaptability:** Seamlessly absorb requirement evolutions (E-Bikes, Delivery Vans, Visitor One-Day Passes, and 5-floor irregular expansions) without modifying established base classes.

---

## 4. Technologies Used

- **Programming Language:** Java SE 27 (Java 11+ compatible syntax, pattern matching `instanceof`)
- **Build & Execution Tools:** Java Development Kit (`javac`, `java`)
- **Version Control:** Git & GitHub (Progressive task commits)
- **Design & Modeling:** Unified Modeling Language (UML) with Mermaid diagrams
- **Operating Environment:** Windows PowerShell

---

## 5. Project Folder Structure

```text
Java-Assignment-1/
│
├── README.md                                 # Master assignment documentation
├── .gitignore                                # Git ignore file for compiled classes
│
├── Theory-Answers/                           # In-depth theory, pseudocode, and complexity analysis
│   ├── Task01.md                             # Problem analysis, candidate classes, parallel arrays
│   ├── Task02.md                             # Data representation, casting risks, division traps
│   ├── Task03.md                             # Control logic, SIMATS pseudocode, allocation algorithms
│   ├── Task04.md                             # Constructors, chaining, copy constructor, memory model
│   ├── Task05.md                             # Static members, multi-facility collision, GC lifecycle
│   ├── Task06.md                             # Inheritance hierarchy, super trace, composition
│   ├── Task07.md                             # Method overriding, final rules, equals/hashCode
│   ├── Task08.md                             # Polymorphism, dynamic dispatch, equals analysis
│   ├── Task09.md                             # Abstraction, Chargeable interface, inversion analysis
│   ├── Task10.md                             # Encapsulation, package design, autoboxing == trap
│   ├── Task11.md                             # Full system implementation, test defense, complexity
│   └── Task12.md                             # Day-4 Design Challenge, impact analysis, defence
│
├── Task01/                                   # Task 01 Code & Execution Outputs
│   ├── ParallelArrayVsObjectDemo.java
│   └── output.txt
├── Task02/                                   # Task 02 Code & Execution Outputs
│   ├── DataRepresentationDemo.java
│   └── output.txt
├── Task03/                                   # Task 03 Complete the Code
│   ├── ParkingFacility.java                  # Completed TODO 1 & TODO 2
│   └── output.txt                            # Verified expected output
├── Task04/                                   # Task 04 Code & Execution Outputs
│   ├── ClassDesignDemo.java
│   └── output.txt
├── Task05/                                   # Task 05 Code & Execution Outputs
│   ├── StaticAndLifecycleDemo.java
│   └── output.txt
├── Task06/                                   # Task 06 Code & Execution Outputs
│   ├── InheritanceHierarchyDemo.java
│   └── output.txt
├── Task07/                                   # Task 07 Code & Execution Outputs
│   ├── MethodOverridingDemo.java
│   └── output.txt
├── Task08/                                   # Task 08 Complete the Code
│   ├── FeeDemo.java                          # Completed TODO 1, 2 & 3
│   └── output.txt                            # Verified expected output
├── Task09/                                   # Task 09 Code & Execution Outputs
│   ├── AbstractionDemo.java
│   └── output.txt
├── Task10/                                   # Task 10 Code & Execution Outputs
│   ├── EncapsulationDemo.java
│   └── output.txt
├── Task11/                                   # Task 11 Complete System Implementation
│   ├── CampusSmartParkingSystem.java         # Integrated 12-test comprehensive suite
│   └── output.txt
└── Task12/                                   # Task 12 Day-4 Design Challenge
    ├── Day4DesignChallengeDemo.java          # E-Bikes, Delivery Vans, 1-Day Pass, 5 Floors
    └── output.txt
```

---

## 6. Summary of Tasks (1 to 12)

### Task 01 – Problem Analysis & Object Identification
- Formulated inputs, outputs, noun-verb analysis, class responsibilities, and business rules.
- Compared object model vs parallel arrays (`regNo[]`, `type[]`, `entryTime[]`), analyzing $O(N)$ lookup and memory fragmentation.

### Task 02 – Data Representation & Casting Precision
- Justified primitive and reference types (`int`, `String`, `double`, `boolean`).
- Demonstrated how a jagged array (`String[][]`) supports irregular floor layouts.
- Uncovered the integer division trap (`minutes / 60`) causing loss of revenue and resolved it via `(minutes + 59) / 60`.

### Task 03 – Control Logic & Algorithmic Pseudocode
- Designed first-fit search across floors with short-circuit returns and break statements.
- Formulated SIMATS Engineering Standard algorithmic pseudocode for `allocate()` and `computeParkingFee()`.
- Completed `ParkingFacility.java` (TODO 1 & 2), matching the mandatory expected output:
  ```text
  [0, 0]
  [0, 1]
  [1, 0]
  null
  true false false
  [0, 1]
  ```

### Task 04 – Classes, Objects & Constructors
- Implemented parameterized constructors, overloaded constructors, and constructor chaining via `this()`.
- Implemented a copy constructor for re-issuing duplicate tickets without mutating originals.
- Traced memory allocation on JVM Stack vs Heap.

### Task 05 – Static Members & Object Lifecycle
- Analyzed static global ticket counters vs instance counters across multi-gate facilities (North and South gates).
- Resolved counter collision via prefixed namespaces (`NORTH-0001`, `SOUTH-0001`) and static shared counters.
- Traced object lifecycle, pass-by-value of references, and Garbage Collection eligibility.

### Task 06 – Inheritance Hierarchy & Constructor Tracing
- Designed hierarchical and multilevel inheritance (`Vehicle` $\rightarrow$ `Car` $\rightarrow$ `ElectricCar`).
- Traced step-by-step top-down constructor execution for `ElectricCar` (`Object` $\rightarrow$ `Vehicle` $\rightarrow$ `Car` $\rightarrow$ `ElectricCar`).
- Compared inheritance ("is-a") with composition ("has-a").

### Task 07 – Method Overriding & Object Contracts
- Overrode `ratePerHour()` and `calculateFee()` using `@Override`.
- Applied `final` to immutable fields (`regNo`) and getters.
- Overrode `equals()` and `hashCode()` to enforce business identity (vehicles with matching `regNo` evaluate as equal, preventing duplicate set insertions).

### Task 08 – Polymorphism & FeeDemo Verification
- Demonstrated compile-time overloading (`calculateFee(int)` vs `calculateFee(LocalTime, LocalTime)`).
- Traced dynamic method dispatch through the JVM virtual method table (`vtable`).
- Completed `FeeDemo.java` (TODO 1, 2, and 3), producing the exact expected output:
  ```text
  TwoWheeler TN01A1 -> Rs 30.0
  Car TN02B2 -> Rs 90.0
  ElectricCar TN03C3 -> Rs 210.0
  true
  ```
- Evaluated why `new Car("TN02B2").equals(new ElectricCar("TN02B2"))` evaluates to `true` and justified its correctness.

### Task 09 – Abstraction & Pluggable Interfaces
- Articulated why `Vehicle` is an abstract class (holds state, constructor, template method) while `Chargeable` is an interface (optional capability).
- Implemented default and static methods in `Chargeable`.
- Conducted the Inversion Analysis (why making `Vehicle` an interface and `Chargeable` an abstract class breaks single inheritance).

### Task 10 – Encapsulation, Packages & Autoboxing
- Structured classes into `parking.model`, `parking.service`, and `parking.app`.
- Implemented defensive mutator validation rejecting negative durations and duplicate occupancies.
- Demonstrated the JVM Integer cache trap (`Integer 100 == 100` is `true`, while `Integer 500 == 500` is `false`).

### Task 11 – Full Implementation & Evidence-Based Defense
- Built `CampusSmartParkingSystem.java` with a comprehensive 12-test automated suite.
- Validated normal, boundary, full, zero-minute, slab boundary, and invalid scenarios with 100% pass rate.
- Verified a hand-calculated multi-vehicle scenario totaling **Rs 430.00** matching program output with Rs 0.00 variance.

### Task 12 – Day-4 Design Challenge
- Adapted the system to accommodate:
  1. **E-Bikes:** Flat fee of Rs 15.0 regardless of duration.
  2. **Delivery Vans:** Double car rate (Rs 60.0/hr) during peak daytime business hours (9 AM - 5 PM) and standard Rs 30.0/hr off-peak.
  3. **Visitor One-Day Pass:** Exemption from hourly fees for Rs 100.0 flat pass.
  4. **5-Floor Heterogeneous Facility:** Initialized with capacities `[20, 30, 40, 25, 15]` (Total 130 slots) using native jagged arrays.
- Verified all requirements with zero modifications to existing tested base classes.

---

## 7. Compilation and Execution Guide

All programs can be compiled and executed directly from the terminal or any Java IDE.

### Prerequisites
- JDK 11 or higher (Tested and verified on Java SE 27).

### Compiling and Running Programs

Navigate to the `Java-Assignment-1` directory:
```powershell
cd "c:\Users\Dhanush\OneDrive\Documents\Assignment 1 -java\Java-Assignment-1"
```

#### Task 01:
```powershell
javac Task01\ParallelArrayVsObjectDemo.java
java Task01.ParallelArrayVsObjectDemo
```

#### Task 02:
```powershell
javac Task02\DataRepresentationDemo.java
java Task02.DataRepresentationDemo
```

#### Task 03 (Mandatory Complete the Code):
```powershell
javac Task03\ParkingFacility.java
java Task03.ParkingFacility
```

#### Task 04:
```powershell
javac Task04\ClassDesignDemo.java
java Task04.ClassDesignDemo
```

#### Task 05:
```powershell
javac Task05\StaticAndLifecycleDemo.java
java Task05.StaticAndLifecycleDemo
```

#### Task 06:
```powershell
javac Task06\InheritanceHierarchyDemo.java
java Task06.InheritanceHierarchyDemo
```

#### Task 07:
```powershell
javac Task07\MethodOverridingDemo.java
java Task07.MethodOverridingDemo
```

#### Task 08 (Mandatory Complete the Code - FeeDemo):
```powershell
javac Task08\FeeDemo.java
java Task08.FeeDemo
```

#### Task 09:
```powershell
javac Task09\AbstractionDemo.java
java Task09.AbstractionDemo
```

#### Task 10:
```powershell
javac Task10\EncapsulationDemo.java
java Task10.EncapsulationDemo
```

#### Task 11 (Full System Implementation & 12-Case Test Suite):
```powershell
javac Task11\CampusSmartParkingSystem.java
java Task11.CampusSmartParkingSystem
```

#### Task 12 (Day-4 Design Challenge Demo):
```powershell
javac Task12\Day4DesignChallengeDemo.java
java Task12.Day4DesignChallengeDemo
```

---

## 8. Master Test Results Summary

| Test Suite | Scenario / Test Description | Expected Output | Actual Output | Status |
| :---: | :--- | :--- | :--- | :---: |
| **Task 03** | `ParkingFacility.main()` mandatory execution | `[0,0] [0,1] [1,0] null true false false [0,1]` | `[0,0] [0,1] [1,0] null true false false [0,1]` | **MATCHED** |
| **Task 08** | `FeeDemo.main()` mandatory execution | `TwoWheeler TN01A1 -> Rs 30.0`<br>`Car TN02B2 -> Rs 90.0`<br>`ElectricCar TN03C3 -> Rs 210.0`<br>`true` | `TwoWheeler TN01A1 -> Rs 30.0`<br>`Car TN02B2 -> Rs 90.0`<br>`ElectricCar TN03C3 -> Rs 210.0`<br>`true` | **MATCHED** |
| **Task 11: TC-01** | TwoWheeler 45 min parking stay | Fee = Rs 10.0 | Fee = Rs 10.0 | **PASS** |
| **Task 11: TC-02** | Car 130 min parking stay (3 billable hrs) | Fee = Rs 90.0 | Fee = Rs 90.0 | **PASS** |
| **Task 11: TC-03** | ElectricCar 60 min + 10 kWh charging | Fee = Rs 150.0 (30 + 120) | Fee = Rs 150.0 | **PASS** |
| **Task 11: TC-04** | Student Monthly Pass Holder Car 180 min | Fee = Rs 0.0 (Exemption) | Fee = Rs 0.0 | **PASS** |
| **Task 11: TC-05** | Staff Monthly Pass Holder EV + 15 kWh | Fee = Rs 180.0 (Charging only) | Fee = Rs 180.0 | **PASS** |
| **Task 11: TC-06** | Fill capacity to final slot | Free count becomes 0 | Free count = 0 | **PASS** |
| **Task 11: TC-07** | Vehicle entry attempt when facility full | Allocation returns `null` | Returns `null` | **PASS** |
| **Task 11: TC-08** | Zero-minute stay boundary | Billed 1 full hr = Rs 30.0 | Fee = Rs 30.0 | **PASS** |
| **Task 11: TC-09** | Exact slab boundary: 60m vs 61m | 60m = Rs 30.0, 61m = Rs 60.0 | 60m = Rs 30.0, 61m = Rs 60.0 | **PASS** |
| **Task 11: TC-10** | Duplicate registration number admission | `IllegalStateException` thrown | Duplicate caught & rejected | **PASS** |
| **Task 11: TC-11** | Unregistered vehicle exit attempt | `IllegalArgumentException` thrown | Unregistered caught & rejected | **PASS** |
| **Task 11: TC-12** | Negative parking duration exit | `IllegalArgumentException` thrown | Negative caught & rejected | **PASS** |
| **Task 11: Audit** | Hand-calculated 5-vehicle daily collection | Total = Rs 430.00 | Total = Rs 430.00 (Variance: 0.00) | **PASS** |
| **Task 12: 5-Floor**| Heterogeneous capacities `[20, 30, 40, 25, 15]` | Total capacity = 130 slots | Total capacity = 130 slots | **PASS** |
| **Task 12: E-Bike** | E-Bike flat fee for 15m vs 360m stay | Rs 15.0 flat for both | Rs 15.0 flat for both | **PASS** |
| **Task 12: Van Peak**| Delivery Van 10 AM - 12 PM (Peak 2 hrs) | 2 hrs @ Rs 60/hr = Rs 120.0 | Fee = Rs 120.0 | **PASS** |
| **Task 12: Van Off** | Delivery Van 6 PM - 8 PM (Off-Peak 2 hrs) | 2 hrs @ Rs 30/hr = Rs 60.0 | Fee = Rs 60.0 | **PASS** |
| **Task 12: Pass**   | Visitor One-Day Pass holder for 8 hrs | Base fee waived = Rs 0.0 | Fee = Rs 0.0 | **PASS** |

---

## 9. Conclusion

The Campus Smart-Parking System project rigorously demonstrates all core competencies of **CO1 (Java fundamentals and object-oriented programming)**. Through methodical application of inheritance, polymorphism, encapsulation, and abstraction, the system achieves complete operational reliability, numerical precision, and effortless extensibility under evolving campus requirements.
