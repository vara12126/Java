# UML Architecture Diagrams

This directory contains the system architecture diagrams for the **Campus Smart-Parking System** in both **Mermaid (.mmd)** and **PlantUML (.puml)** formats, along with an interactive HTML viewer (`index.html`).

---

## Diagram Index

| File | Diagram Type | Description |
| :--- | :--- | :--- |
| `01_Overall_System_Class_Diagram.mmd` / `.puml` | Class Diagram | Comprehensive system diagram depicting entities, relationships, multiplicities, and interfaces. |
| `02_Vehicle_Inheritance_Diagram.mmd` / `.puml` | Inheritance Hierarchy | Single, hierarchical, and multilevel inheritance tree (`Vehicle -> TwoWheeler, Car -> ElectricCar`). |
| `03_ParkingTicket_Class_Diagram.mmd` / `.puml` | Class Details | Detailed structural breakdown of `ParkingTicket` attributes and lifecycle methods. |
| `04_ParkingFacility_Class_Diagram.mmd` / `.puml` | Class Details | Core coordinator class managing multi-floor jagged arrays and ticket lookup maps. |
| `05_Interface_Abstraction_Diagram.mmd` / `.puml` | Abstraction | Decoupling contracts via `Chargeable` and `Trackable` interfaces with abstract classes. |
| `06_Revised_Task12_Architecture.mmd` / `.puml` | Strategy Pattern | Extended Task 12 architecture introducing `PricingRule`, `Pass`, `EBike`, and `DeliveryVan`. |

---

## Viewing Instructions
1. **In Browser:** Open [index.html](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/UML_Diagrams/index.html) in any modern web browser to view dynamically rendered SVG diagrams.
2. **In VS Code / IDE:** Install the *Mermaid Preview* or *PlantUML* extension to view `.mmd` and `.puml` files.
3. **In GitHub:** GitHub natively renders `.mmd` Mermaid diagrams directly in markdown previews.
