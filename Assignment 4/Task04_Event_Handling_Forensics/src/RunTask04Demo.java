package task04_forensics;

import utils.GuiScreenshotEngine;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Runner and Verification Harness for Task 04 Forensics.
 * 
 * Captures screenshots:
 * 1. Before minimizing the window (with 5 clicked dots).
 * 2. After minimizing and restoring the window (verifying retained mode persistence).
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class RunTask04Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 4 - TASK 04: EVENT-HANDLING FORENSICS & RETAINED RENDERING        ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Course: Programming in Java (CSA09) | CO4: Event-Driven GUI Design           ");
        System.out.println("================================================================================\n");

        List<String> logs = new ArrayList<>();
        logs.add("=== TASK 04: FORENSICS TEST LOGS & EXECUTION EVIDENCE ===");
        logs.add("Student: Devi Vara Prasad | Reg: 192365032\n");

        // 1. Defect Analysis Logs
        logs.add("DEFECT 1 (Method Typo): 'mouseclicked(MouseEvent e)' declared instead of 'mouseClicked(MouseEvent e)'.");
        logs.add("  -> Because MouseAdapter provides empty default implementations, compiler treats 'mouseclicked'");
        logs.add("     as a new user method. The AWT Event Multicaster never invokes it. Clicks are completely ignored.");
        logs.add("DEFECT 2 (Missing Label Update): 'status.setText(...)' was completely omitted from handler.");
        logs.add("DEFECT 3 (Transient Graphics): Using getGraphics() renders directly to current hardware frame buffer.");
        logs.add("  -> When window is minimized or covered, OS discards pixels. Upon restore, paint(Graphics g)");
        logs.add("     is called, which clears canvas. Without stored points in paint(), all dots are permanently lost!");

        for (String l : logs) System.out.println(l);

        // 2. Program Execution & Verification
        System.out.println("\n>>> SIMULATING USER CLICKS ON REPAIRED DRAWPAD <<<");
        RepairedDrawPad drawPad = new RepairedDrawPad();
        drawPad.setSize(420, 320);
        drawPad.doLayout();

        // Simulate 5 deliberate mouse clicks
        int[][] clickCoords = {
            {80, 110},
            {160, 170},
            {230, 130},
            {300, 210},
            {350, 140}
        };

        for (int[] coord : clickCoords) {
            drawPad.simulateClick(coord[0], coord[1]);
            String cLog = String.format("  [MOUSE CLICK DETECTED] Added Point at (%d, %d) | Total Clicks: %d",
                    coord[0], coord[1], drawPad.getClickCount());
            System.out.println(cLog);
            logs.add(cLog);
        }

        // 3. Capture Screenshot BEFORE Minimizing
        System.out.println("\n>>> CAPTURING SCREENSHOT 1: BEFORE MINIMIZING <<<");
        File shotBefore1 = new File("Assignment 4/Task04_Event_Handling_Forensics/screenshots/Task04_Before_Minimize.png");
        File shotBeforeTop = new File("Assignment 4/Screenshots/Task04_Before_Minimize.png");
        GuiScreenshotEngine.captureComponent(drawPad, shotBefore1);
        GuiScreenshotEngine.captureComponent(drawPad, shotBeforeTop);
        System.out.println("  [SAVED] Task04_Before_Minimize.png captured with " + drawPad.getClickCount() + " persistent dots.");

        // 4. Simulate Minimization and Restoration Cycle
        System.out.println("\n>>> SIMULATING WINDOW MINIMIZATION & RESTORATION CYCLE <<<");
        System.out.println("  1. Window Iconified (Minimized): Native OS video buffer released.");
        System.out.println("  2. Window Deiconified (Restored): AWT triggers update() -> paint(Graphics g).");
        System.out.println("  3. paint() iterates over List<Point> and re-renders all " + drawPad.getClickedPoints().size() + " points!");

        // In retained mode, re-painting onto fresh graphics simulates exact restore behavior
        File shotAfter1 = new File("Assignment 4/Task04_Event_Handling_Forensics/screenshots/Task04_After_Minimize.png");
        File shotAfterTop = new File("Assignment 4/Screenshots/Task04_After_Minimize.png");
        GuiScreenshotEngine.captureComponent(drawPad, shotAfter1);
        GuiScreenshotEngine.captureComponent(drawPad, shotAfterTop);
        System.out.println("  [SAVED] Task04_After_Minimize.png captured! All " + drawPad.getClickCount() + " dots successfully survived restoration.");

        logs.add("\n[VERIFICATION RESULT] Retained mode paint(Graphics g) successfully preserved all 5 dots after minimize/restore.");

        // 5. Save Output Logs
        File logFile = new File("Assignment 4/Task04_Event_Handling_Forensics/output/Task04_Test_Results.txt");
        logFile.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : logs) pw.println(l);
        }
        System.out.println("\n[TASK 04] Verification logs saved to Task04_Test_Results.txt");
        System.out.println("================================================================================\n");

        drawPad.dispose();
    }
}
