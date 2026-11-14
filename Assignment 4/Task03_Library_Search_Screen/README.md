# Task 03: From Sketch to Layout – Campus Library Search Screen

## Student Information
- **Student Name**: Devi Vara Prasad
- **Register Number**: 192365032
- **Course**: Programming in Java (CSA09) | **Course Outcome**: CO4 – Event-Driven GUI Design
- **GitHub Repository**: [https://github.com/vara12126/Java](https://github.com/vara12126/Java)

---

## 1. Problem Overview
The Campus Digital Library requires a responsive search interface comprising:
1. A **Title Banner** and **Search Row** (text field, category choice, search and clear buttons) at the top.
2. A **Results Area** in the centre displaying matching books in a table inside a scrollable viewport.
3. A **Bottom Row** with pagination controls (`< Previous`, page indicator, `Next >`), a `Reserve Selected` action button, and a status message bar.

### Core Resizing Requirement:
> *When the window is resized by the user, the results area must automatically expand in both dimensions (width and height) to display more catalog rows, while the search controls row and the bottom button row must strictly preserve their natural preferred heights without stretching or distortion.*

---

## 2. Task 3.1 – Layout Manager Selection & Justification

| Container / Region | Selected Layout Manager | Resizing Behavior & Architectural Justification |
|---|---|---|
| **Root Frame** | `BorderLayout` | `BorderLayout` divides space into 5 zones. Crucially, the **`CENTER` zone is assigned all surplus horizontal and vertical space** remaining after `NORTH`, `SOUTH`, `EAST`, and `WEST` take their preferred heights/widths. Placing the results scroll pane in `CENTER` satisfies the primary resizing mandate. |
| **Top Container (`NORTH`)** | `BorderLayout` | Subdivides into a fixed-height title banner (`NORTH`) and the search control row (`SOUTH`). Under `BorderLayout`, the `NORTH` component respects its preferred height and stretches horizontally across the window width. |
| **Search Control Row** | `FlowLayout(FlowLayout.CENTER, 8, 6)` | `FlowLayout` arranges label, text field, combo box, and buttons sequentially from left to right, strictly preserving each control's natural **`preferredSize`** and preventing vertical stretching. |
| **Results Container (`CENTER`)** | `BorderLayout` (via `JScrollPane`) | Houses the `JTable`. As the user resizes the window, `BorderLayout.CENTER` stretches both horizontally and vertically, allowing more table columns and book rows to become visible simultaneously. |
| **Bottom Navigation (`SOUTH`)** | `BorderLayout` | Subdivides into the navigation button row (`NORTH`) and the status bar (`SOUTH`). Guarantees that the buttons never stretch vertically into empty space. |
| **Navigation Button Row** | `FlowLayout(FlowLayout.CENTER, 14, 6)` | Centrally aligns pagination and reservation buttons, keeping their heights and widths constant during resizing. |

---

## 3. Task 3.2 – Nested Container Architecture

```text
JFrame (LibrarySearchWindow) [BorderLayout]
│
├── NORTH: JPanel (pnlNorth) [BorderLayout]
│   ├── NORTH: JPanel (pnlHeader) [GridLayout(2, 1)]
│   │   ├── JLabel (lblTitle: "CAMPUS DIGITAL LIBRARY SEARCH PORTAL")
│   │   └── JLabel (lblSub: "CO4: Layout Management | Devi Vara Prasad (192365032)")
│   │
│   └── SOUTH: JPanel (pnlSearchRow) [FlowLayout(FlowLayout.CENTER, 8, 6)]
│       ├── JLabel ("Keyword:")
│       ├── JTextField (txtSearch)
│       ├── JLabel ("Category:")
│       ├── JComboBox (cmbCategory)
│       ├── JButton (btnSearch)
│       └── JButton (btnClear)
│
├── CENTER: JScrollPane (scrollPane) [EXPANDS IN BOTH AXES]
│   └── JTable (tableResults: Accession No, Title, Author, Category, Year, Availability)
│
└── SOUTH: JPanel (pnlSouth) [BorderLayout]
    ├── NORTH: JPanel (pnlNav) [FlowLayout(FlowLayout.CENTER, 14, 6)]
    │   ├── JButton (btnPrev)
    │   ├── JLabel (lblPageInfo)
    │   ├── JButton (btnNext)
    │   └── JButton (btnReserve)
    │
    └── SOUTH: JPanel (pnlStatus) [FlowLayout(FlowLayout.LEFT, 10, 4)]
        └── JLabel (lblStatus: Dynamic record count and reservation status)
```

---

## 4. Task 3.4 – Resizing Behaviour Verification

The screen was dynamically resized and evaluated at three standard desktop viewport resolutions:

### 1. Small Viewport (520 × 360)
The search row and navigation buttons retain their compact, legible layout. The `JTable` viewport adjusts to show the top 5 rows, with scrollbars automatically appearing if required.
![Task 03 Small](screenshots/Task03_Resize_Small.png)

### 2. Medium Viewport (740 × 500)
The central `JTable` expands horizontally to provide generous whitespace for long book titles and author names, and expands vertically. The search row and footer buttons maintain identical vertical thickness.
![Task 03 Medium](screenshots/Task03_Resize_Medium.png)

### 3. Large Viewport (980 × 660)
The results area claims all surplus display area, ensuring maximum reading comfort. The search controls remain pinned cleanly at the top, and navigation buttons stay anchored at the bottom without vertical distortion.
![Task 03 Large](screenshots/Task03_Resize_Large.png)

---

## 5. Alternative Concept & Complexity Analysis

### Alternative: Absolute Positioning (`setLayout(null)` with `setBounds`) vs `GridBagLayout` vs Nested `BorderLayout`

| Design Approach | Resizing Behavior on Window Resize | Layout Computation Time | Container Memory & Object Count | Maintenance & Developer Velocity |
|---|---|---|---|---|
| **Absolute Positioning (`null` layout)** | **Broken / Static**: Coordinates remain fixed at hardcoded pixel positions. Components clip or leave massive empty voids on enlarge. Requires manual `ComponentListener` math. | $\mathcal{O}(1)$ (No layout calculation performed) | Minimal ($0$ layout manager objects) | Fragile, non-portable across DPIs and operating systems. |
| **`GridBagLayout`** | **Flexible**: Supports variable `weightx` and `weighty`. Can achieve identical resizing behavior within a single container. | $\mathcal{O}(N)$ with complex 2-pass matrix constraint equations. | Single layout manager object, but allocates $N$ `GridBagConstraints` objects. | Very high boilerplate code; difficult to debug and maintain. |
| **Nested `BorderLayout` + `FlowLayout` (Chosen)** | **Optimal & Deterministic**: Native L-R and T-B constraints natively expand `CENTER` while fixing `NORTH`/`SOUTH` preferred heights. | $\mathcal{O}(N)$ simple bounding-box arithmetic across nested panels. | 4 lightweight panel containers (~180 bytes total). | Highly modular, clean separation, declarative and intuitive. |

---

## 6. How to Compile & Run

```bash
# Compile from repository root
javac -d bin -cp bin "Assignment 4/Task03_Library_Search_Screen/src/*.java" "Assignment 4/Report/*.java"

# Run resizing test suite and demo
java -cp bin task03_library.RunTask03Demo
```
