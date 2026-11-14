# Programming in Java – Assignment 4 (CO4)
## Event-Driven GUI Design

## Student Information
- **Student Name**: Devi Vara Prasad
- **Register Number**: 192365032
- **Course**: Programming in Java (CSA09)
- **Course Outcome**: CO4 – Event-Driven GUI Design (Sessions 49–56)
- **Nature of Assignment**: Individual
- **Total Marks**: 50 Marks (Bloom's Levels: K4 – Analyse, K5 – Evaluate)
- **GitHub Repository**: [https://github.com/vara12126/Java](https://github.com/vara12126/Java)
- **Target Folder**: `Assignment 4`
- **Java Platform**: Java SE 25.0.2 (Oracle Corporation), Windows 11 64-bit

---

## Repository Structure

```text
Assignment 4/
├── Task01_GUI_Design_Review/
│   ├── src/
│   │   ├── FeePaymentDTO.java
│   │   ├── FeeService.java
│   │   ├── FeeWindow.java
│   │   └── RunTask01Demo.java
│   ├── screenshots/
│   │   └── Task01_Fee_Window.png
│   ├── output/
│   │   └── Task01_Test_Results.txt
│   └── README.md
│
├── Task02_Hospital_Outpatient_Desk/
│   ├── src/
│   │   ├── Doctor.java
│   │   ├── AppointmentDTO.java
│   │   ├── AppointmentService.java
│   │   ├── OutpatientDeskWindow.java
│   │   └── RunTask02Demo.java
│   ├── screenshots/
│   │   └── Task02_Hospital_Desk.png
│   ├── output/
│   │   └── Task02_Test_Results.txt
│   └── README.md
│
├── Task03_Library_Search_Screen/
│   ├── src/
│   │   ├── BookRecord.java
│   │   ├── LibraryCatalogService.java
│   │   ├── LibrarySearchWindow.java
│   │   └── RunTask03Demo.java
│   ├── screenshots/
│   │   ├── Task03_Resize_Small.png
│   │   ├── Task03_Resize_Medium.png
│   │   └── Task03_Resize_Large.png
│   ├── output/
│   │   └── Task03_Test_Results.txt
│   └── README.md
│
├── Task04_Event_Handling_Forensics/
│   ├── src/
│   │   ├── FlawedDrawPad.java
│   │   ├── RepairedDrawPad.java
│   │   └── RunTask04Demo.java
│   ├── screenshots/
│   │   ├── Task04_Before_Minimize.png
│   │   └── Task04_After_Minimize.png
│   ├── output/
│   │   └── Task04_Test_Results.txt
│   └── README.md
│
├── Screenshots/
│   ├── Task01_Fee_Window.png
│   ├── Task02_Hospital_Desk.png
│   ├── Task03_Resize_Small.png
│   ├── Task03_Resize_Medium.png
│   ├── Task03_Resize_Large.png
│   ├── Task04_Before_Minimize.png
│   └── Task04_After_Minimize.png
│
├── Report/
│   ├── Assignment4_Report.md
│   └── GuiScreenshotEngine.java
│
└── README.md
```

---

## Task Summary Table

| Task Folder | Core Concepts & Design Highlights | Key Classes | Status |
|---|---|---|:---:|
| **Task 01: GUI Design Review** | Separation of concerns, `SwingWorker` non-blocking EDT, `PreparedStatement` parameterization, dedicated listeners, modal alerts. | `FeeWindow`, `FeeService`, `FeePaymentDTO` | **100% Passed** |
| **Task 02: Hospital Outpatient Desk** | Event delegation model, dynamic combo box filtering on `ItemEvent`, SIMATS pseudocode, window closing guard, in-memory $O(1)$ vs DB benchmark. | `OutpatientDeskWindow`, `AppointmentService`, `Doctor` | **100% Passed** |
| **Task 03: Library Search Screen** | Layout manager selection, nested container architecture, strict resizing verification at Small (520x360), Medium (740x500), Large (980x660). | `LibrarySearchWindow`, `LibraryCatalogService`, `BookRecord` | **100% Passed** |
| **Task 04: Event-Handling Forensics** | Method typo diagnosis (`mouseclicked` vs `mouseClicked`), `@Override` compile guard, retained-mode rendering via `paint(Graphics g)` and `List<Point>`. | `RepairedDrawPad`, `FlawedDrawPad` | **100% Passed** |

---

## How to Compile & Run

### 1. Build All Tasks
From the root of the repository:
```bash
# In Windows PowerShell:
$src = (Get-ChildItem -Path "Assignment 4\*\src\*.java", "Assignment 4\Report\*.java").FullName
javac -d bin -cp bin $src
```

### 2. Run Individual Demonstrations
```bash
# Task 01: Fee Payment Window & Verification
java -cp bin task01_fee.RunTask01Demo

# Task 02: Hospital Outpatient Desk & Caching Benchmark
java -cp bin task02_hospital.RunTask02Demo

# Task 03: Library Search Screen Resizing Test
java -cp bin task03_library.RunTask03Demo

# Task 04: Event-Handling Forensics & Retained Graphics Test
java -cp bin task04_forensics.RunTask04Demo
```

---

## Submission Details
- **Student**: Devi Vara Prasad (Register Number: 192365032)
- **Mode**: Individual Student Submission
- **Master Report**: [`Assignment 4/Report/Assignment4_Report.md`](Report/Assignment4_Report.md)
- **Screenshots Gallery**: [`Assignment 4/Screenshots/`](Screenshots/)
