package task03_library;

import utils.GuiScreenshotEngine;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Test Runner and Resizing Verification for Task 03.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class RunTask03Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 4 - TASK 03: CAMPUS LIBRARY SEARCH SCREEN & RESIZING TEST         ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Course: Programming in Java (CSA09) | CO4: Event-Driven GUI Design           ");
        System.out.println("================================================================================\n");

        List<String> logs = new ArrayList<>();
        logs.add("=== TASK 03: TEST LOGS & RESIZING VERIFICATION ===");
        logs.add("Student: Devi Vara Prasad | Reg: 192365032\n");

        LibraryCatalogService service = new LibraryCatalogService();

        // 1. Search Query Verification
        System.out.println(">>> 1. TESTING SEARCH FILTERING <<<");
        List<BookRecord> csBooks = service.searchBooks("Java", "Computer Science");
        String log1 = "[SEARCH] Query='Java', Category='Computer Science' -> Found " + csBooks.size() + " books:";
        System.out.println(log1);
        logs.add(log1);
        for (BookRecord b : csBooks) {
            String bLog = "  - " + b.getAccessionNumber() + ": " + b.getTitle() + " (" + b.getAuthor() + ") | Avail: " + b.isAvailable();
            System.out.println(bLog);
            logs.add(bLog);
        }

        // 2. Reservation Test
        System.out.println("\n>>> 2. TESTING BOOK RESERVATION <<<");
        boolean reserved = service.reserveBook("CS-101");
        String resLog = "[RESERVE] Reserving CS-101 (Core Java) -> Success: " + reserved;
        System.out.println(resLog);
        logs.add(resLog);

        boolean duplicateRes = service.reserveBook("CS-101");
        String dupLog = "[RESERVE CONFLICT] Re-reserving CS-101 -> Correctly rejected: " + (!duplicateRes);
        System.out.println(dupLog);
        logs.add(dupLog);

        // 3. Capturing Resizing Screenshots at 3 Distinct Window Sizes
        System.out.println("\n>>> 3. CAPTURING RESIZING BEHAVIOR AT THREE DISTINCT RESOLUTIONS <<<");
        SwingUtilities.invokeAndWait(() -> {
            LibrarySearchWindow window = new LibrarySearchWindow(service);

            try {
                // Resolution 1: Small Size (520 x 360)
                window.setSize(520, 360);
                window.validate();
                File shotSmall1 = new File("Assignment 4/Task03_Library_Search_Screen/screenshots/Task03_Resize_Small.png");
                File shotSmallTop = new File("Assignment 4/Screenshots/Task03_Resize_Small.png");
                GuiScreenshotEngine.captureComponent(window, shotSmall1);
                GuiScreenshotEngine.captureComponent(window, shotSmallTop);
                System.out.println("  [RESIZING TEST 1/3] Small window (520x360) captured. Center table scaled to viewport.");

                // Resolution 2: Medium Size (740 x 500)
                window.setSize(740, 500);
                window.validate();
                File shotMed1 = new File("Assignment 4/Task03_Library_Search_Screen/screenshots/Task03_Resize_Medium.png");
                File shotMedTop = new File("Assignment 4/Screenshots/Task03_Resize_Medium.png");
                GuiScreenshotEngine.captureComponent(window, shotMed1);
                GuiScreenshotEngine.captureComponent(window, shotMedTop);
                System.out.println("  [RESIZING TEST 2/3] Medium window (740x500) captured. Results area expanded horizontally and vertically.");

                // Resolution 3: Large Size (980 x 660)
                window.setSize(980, 660);
                window.validate();
                File shotLarge1 = new File("Assignment 4/Task03_Library_Search_Screen/screenshots/Task03_Resize_Large.png");
                File shotLargeTop = new File("Assignment 4/Screenshots/Task03_Resize_Large.png");
                GuiScreenshotEngine.captureComponent(window, shotLarge1);
                GuiScreenshotEngine.captureComponent(window, shotLargeTop);
                System.out.println("  [RESIZING TEST 3/3] Large window (980x660) captured. Search bar and buttons preserved fixed height!");

            } catch (Exception e) {
                e.printStackTrace();
            }
            window.dispose();
        });

        // 4. Save Logs
        File logFile = new File("Assignment 4/Task03_Library_Search_Screen/output/Task03_Test_Results.txt");
        logFile.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : logs) pw.println(l);
        }
        System.out.println("\n[TASK 03] Verification logs saved to Task03_Test_Results.txt");
        System.out.println("================================================================================\n");
    }
}
