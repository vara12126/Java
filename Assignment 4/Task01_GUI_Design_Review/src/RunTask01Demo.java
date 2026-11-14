package task01_fee;

import utils.GuiScreenshotEngine;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Runner and Test Harness for Task 01.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class RunTask01Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 4 - TASK 01: GUI DESIGN REVIEW & ARCHITECTURAL REPAIR             ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Course: Programming in Java (CSA09) | CO4: Event-Driven GUI Design           ");
        System.out.println("================================================================================\n");

        List<String> logs = new ArrayList<>();
        logs.add("=== TASK 01: TEST LOGS & EXECUTION VERIFICATION ===");
        logs.add("Student: Devi Vara Prasad | Reg: 192365032\n");

        FeeService service = new FeeService();

        // 1. Validation Test Cases
        System.out.println(">>> RUNNING INPUT VALIDATION TESTS (SERVICE LAYER) <<<");
        testValidation(service, "", "1000", "Empty RegNo Check", logs);
        testValidation(service, "192365032", "", "Empty Amount Check", logs);
        testValidation(service, "192365032", "ABCXYZ", "Non-numeric Amount Check", logs);
        testValidation(service, "192365032", "-500", "Negative Amount Check", logs);
        testValidation(service, "192365032", "600000", "Exceeds Max Cap Check", logs);

        // 2. Successful Payment Test
        System.out.println("\n>>> EXECUTING ASYNCHRONOUS PAYMENT TRANSACTION <<<");
        FeePaymentDTO payment = service.processPayment("192365032", 45000.00);
        String payLog = String.format("[PAYMENT SUCCESS] Reg: %s | Amount: ₹%.2f | Ref: %s | Status: %s",
                payment.getRegisterNumber(), payment.getAmount(), payment.getTransactionReference(), payment.getMessage());
        System.out.println(payLog);
        logs.add(payLog);

        // 3. Receipt Retrieval Test
        FeePaymentDTO receipt = service.getLatestReceipt("192365032");
        String recLog = String.format("[RECEIPT VERIFIED] Latest receipt found for %s: Amount ₹%.2f at %s",
                receipt.getRegisterNumber(), receipt.getAmount(), receipt.getTimestamp());
        System.out.println(recLog);
        logs.add(recLog);

        // 4. GUI Window Instantiation & Screenshot Capture
        System.out.println("\n>>> INITIALIZING GUI & CAPTURING EXECUTION SCREENSHOTS <<<");
        SwingUtilities.invokeAndWait(() -> {
            FeeWindow window = new FeeWindow(service);
            window.getTxtRegNo().setText("192365032");
            window.getTxtAmount().setText("45000.00");
            window.pack();
            window.setSize(480, 320);

            try {
                File shot1 = new File("Assignment 4/Task01_GUI_Design_Review/screenshots/Task01_Fee_Window.png");
                File shotTop = new File("Assignment 4/Screenshots/Task01_Fee_Window.png");
                GuiScreenshotEngine.captureComponent(window, shot1);
                GuiScreenshotEngine.captureComponent(window, shotTop);
            } catch (Exception e) {
                e.printStackTrace();
            }
            window.dispose();
        });

        // 5. Save Output Logs
        File logFile = new File("Assignment 4/Task01_GUI_Design_Review/output/Task01_Test_Results.txt");
        logFile.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : logs) pw.println(l);
        }
        System.out.println("[TASK 01] Verification logs exported to Task01_Test_Results.txt");
        System.out.println("================================================================================\n");
    }

    private static void testValidation(FeeService service, String reg, String amt, String testName, List<String> logs) {
        try {
            service.validateInput(reg, amt);
            String msg = "[FAIL] " + testName + " unexpectedly passed!";
            System.err.println(msg);
            logs.add(msg);
        } catch (IllegalArgumentException e) {
            String msg = "[PASS] " + testName + " correctly caught: " + e.getMessage();
            System.out.println(msg);
            logs.add(msg);
        }
    }
}
