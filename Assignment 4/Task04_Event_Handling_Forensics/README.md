# Task 04: Event-Handling Forensics – Drawing Pad

## Student Information
- **Student Name**: Devi Vara Prasad
- **Register Number**: 192365032
- **Course**: Programming in Java (CSA09) | **Course Outcome**: CO4 – Event-Driven GUI Design
- **GitHub Repository**: [https://github.com/vara12126/Java](https://github.com/vara12126/Java)

---

## 1. Problem Overview
A drawing pad application was implemented by a developer with the expectation that clicking on the canvas draws a dot and increments a click counter label. However, in production, users reported that **clicking anywhere produces nothing**, and the **click counter remains stuck at 0**. Furthermore, even when basic clicking was attempted, any drawn content vanished whenever the window was **minimised and restored**.

This task conducts an in-depth forensic investigation into the event-handling defects, repairs the architecture using retained-mode graphics, and verifies the solution with before/after minimisation evidence.

---

## 2. Task 4.1 – Locate the Defect

### Defect 1: Listener Method Signature Typo
In the original code:
```java
addMouseListener(new MouseAdapter() {
    public void mouseclicked(MouseEvent e) { // <-- TYPO: lowercase 'c'
        count++;
        Graphics g = getGraphics(); 
        g.fillOval(e.getX(), e.getY(), 6, 6);
    }
});
```
- In Java's `java.awt.event.MouseListener` and `MouseAdapter`, the required method signature is:
  `public void mouseClicked(MouseEvent e)` (camelCase with capital `'C'`).
- Because `MouseAdapter` provides default empty implementations for all mouse listener methods, the compiler saw `mouseclicked(MouseEvent)` as a completely new, overloaded method specific to that anonymous class.
- When a user clicked the mouse, the native AWT event multicaster invoked the standard `mouseClicked(MouseEvent)` method—which remained the empty method inherited from `MouseAdapter`! The developer's code was **never called**.

### Defect 2: Missing Status Label Update
Even if the method had been called, `status.setText(...)` was completely omitted from the method body. The `count` variable would increment in memory, but the GUI label would never change from `"Clicks: 0"`.

---

## 3. Task 4.2 – Construct a Failing Interaction

### The Minimisation / Exposure Wipeout Defect:
Even after fixing the method name to `mouseClicked` and adding `status.setText(...)`, a second critical flaw exists:
```java
Graphics g = getGraphics(); 
g.fillOval(e.getX(), e.getY(), 6, 6);
```
- Calling `getGraphics()` performs **immediate-mode transient drawing** directly onto the OS display surface buffer.
- **Failing Interaction Sequence**:
  1. The user clicks 5 times; 5 crimson dots appear on the canvas.
  2. The user clicks the operating system **Minimise** button ('_').
  3. The OS releases the window's hardware frame buffer to conserve VRAM.
  4. The user clicks the taskbar icon to **Restore (De-iconify)** the window.
  5. The AWT/Swing window manager detects that the window surface is exposed and dispatches a `PaintEvent` to redraw the component by calling `paint(Graphics g)`.
  6. The default `Frame.paint(Graphics g)` clears the background with the default window fill color.
  7. **Result**: Because the click coordinates were never stored in memory and `paint()` was never overridden, **every previously drawn dot is permanently erased!**

---

## 4. Task 4.3 – Repair the Program (Retained-Mode Graphics)

The architectural repair transforms the application from fragile immediate-mode rendering into **retained-mode event-driven rendering**:
1. **Persistent Domain Model**: Created a `List<Point> clickedPoints = new ArrayList<>()` to store coordinates.
2. **Event Handler (TODO 1)**:
   ```java
   @Override
   public void mouseClicked(MouseEvent e) {
       clickedPoints.add(new Point(e.getX(), e.getY()));
       count++;
       status.setText("Clicks: " + count + " | Latest Point: (" + e.getX() + ", " + e.getY() + ")");
       repaint(); // Asynchronously requests AWT to invoke paint()
   }
   ```
3. **Retained-Mode Painting (TODO 2)**:
   ```java
   @Override
   public void paint(Graphics g) {
       super.paint(g);
       g.setColor(new Color(220, 20, 60)); // Crimson Red
       for (Point p : clickedPoints) {
           g.fillOval(p.x - 4, p.y - 4, 8, 8);
       }
   }
   ```
Whenever the window is restored, uncovered, or resized, the AWT subsystem calls `paint(Graphics g)`, which iterates through `clickedPoints` and faithfully reconstructs every dot.

---

## 5. Task 4.4 – Prevent Recurrence via `@Override`

If the junior developer had annotated the method with `@Override`:
```java
addMouseListener(new MouseAdapter() {
    @Override // <-- COMPILE-TIME GUARD
    public void mouseclicked(MouseEvent e) {
        ...
    }
});
```
The Java compiler (`javac`) would inspect the superclass `MouseAdapter`. Finding no method matching `mouseclicked(MouseEvent)`, compilation would immediately fail:
```text
Error: method does not override or implement a method from a supertype
    @Override
    ^
```
The bug would have been caught **at compile-time within milliseconds**, before the code ever reached testing or production.

---

## 6. Complete the Code Verification Evidence

### Screenshot 1: Before Minimising the Window (5 Dots Drawn)
![Before Minimize](screenshots/Task04_Before_Minimize.png)

### Screenshot 2: After Minimising and Restoring the Window (All 5 Dots Retained)
![After Minimize](screenshots/Task04_After_Minimize.png)

---

## 7. How to Compile & Run

```bash
# Compile from repository root
javac -d bin -cp bin "Assignment 4/Task04_Event_Handling_Forensics/src/*.java" "Assignment 4/Report/*.java"

# Run forensics test harness
java -cp bin task04_forensics.RunTask04Demo
```
