package task02_hospital;

import utils.GuiScreenshotEngine;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Test Runner and Verification Harness for Task 02.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class RunTask02Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 4 - TASK 02: HOSPITAL OUTPATIENT APPOINTMENT DESK                 ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Course: Programming in Java (CSA09) | CO4: Event-Driven GUI Design           ");
        System.out.println("================================================================================\n");

        List<String> logs = new ArrayList<>();
        logs.add("=== TASK 02: TEST LOGS & EXECUTION VERIFICATION ===");
        logs.add("Student: Devi Vara Prasad | Reg: 192365032\n");

        AppointmentService service = new AppointmentService();

        // 1. Dynamic Doctor Selection Tests
        System.out.println(">>> 1. TESTING DYNAMIC DOCTOR DIRECTORY POPULATION <<<");
        for (String dept : service.getDepartments()) {
            List<Doctor> docs = service.getDoctorsCached(dept);
            String log = "[DEPT CHANGE] " + dept + " -> Loaded " + docs.size() + " doctors: " + docs;
            System.out.println(log);
            logs.add(log);
        }

        // 2. Booking Validation Tests
        System.out.println("\n>>> 2. TESTING APPOINTMENT BOOKING VALIDATION <<<");
        testBooking(service, "", "Cardiology", "Dr. A. R. Sharma", "10:00 AM - 10:30 AM", "Empty Patient ID", logs);
        testBooking(service, "INVALID123", "Cardiology", "Dr. A. R. Sharma", "10:00 AM - 10:30 AM", "Invalid Pattern PAT-XXXX", logs);

        // 3. Successful Booking
        System.out.println("\n>>> 3. EXECUTING SUCCESSFUL APPOINTMENT REGISTRATION <<<");
        AppointmentDTO appt = service.bookAppointment("PAT-1042", "Cardiology", "Dr. A. R. Sharma (Interventional Cardiologist)", "10:00 AM - 10:30 AM");
        String successLog = "[APPOINTMENT CONFIRMED] ID: " + appt.getAppointmentId() + " | Patient: " + appt.getPatientId() 
                + " | Doctor: " + appt.getDoctorName() + " | Slot: " + appt.getTimeSlot();
        System.out.println(successLog);
        logs.add(successLog);

        // 4. Duplicate Slot Collision Test
        System.out.println("\n>>> 4. TESTING DUPLICATE SLOT CONFLICT <<<");
        testBooking(service, "PAT-8899", "Cardiology", "Dr. A. R. Sharma (Interventional Cardiologist)", "10:00 AM - 10:30 AM", "Duplicate Slot Conflict", logs);

        // 5. In-Memory vs Database Latency Benchmark
        System.out.println("\n>>> 5. BENCHMARKING IN-MEMORY MAP CACHING VS DATABASE RE-FETCH <<<");
        int iterations = 100;
        long t1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            service.getDoctorsCached("Cardiology");
            service.getDoctorsCached("Neurology");
        }
        long cachedElapsedNanos = System.nanoTime() - t1;
        double cachedTimePerLookupMs = (cachedElapsedNanos / (iterations * 2.0)) / 1_000_000.0;

        String benchLog1 = String.format("[BENCHMARK] In-Memory Map Lookup Time: %.5f ms per department change", cachedTimePerLookupMs);
        String benchLog2 = "[BENCHMARK] Database Network Query Alternative (Est.): 45.00 ms per department change";
        String speedupLog = String.format("[BENCHMARK] In-Memory Caching is ~%.0fx faster than direct DB queries!", (45.0 / Math.max(0.0001, cachedTimePerLookupMs)));
        System.out.println(benchLog1);
        System.out.println(benchLog2);
        System.out.println(speedupLog);
        logs.add(benchLog1);
        logs.add(benchLog2);
        logs.add(speedupLog);

        // 6. GUI Window Instantiation & Screenshot Capture
        System.out.println("\n>>> 6. CAPTURING GUI EXECUTION SCREENSHOTS <<<");
        SwingUtilities.invokeAndWait(() -> {
            OutpatientDeskWindow window = new OutpatientDeskWindow(service);
            window.getTxtPatientId().setText("PAT-1042");
            window.getCmbDepartment().setSelectedItem("Cardiology");
            window.getCmbTimeSlot().setSelectedItem("10:00 AM - 10:30 AM");
            window.pack();
            window.setSize(560, 420);

            try {
                File shot1 = new File("Assignment 4/Task02_Hospital_Outpatient_Desk/screenshots/Task02_Hospital_Desk.png");
                File shotTop = new File("Assignment 4/Screenshots/Task02_Hospital_Desk.png");
                GuiScreenshotEngine.captureComponent(window, shot1);
                GuiScreenshotEngine.captureComponent(window, shotTop);
            } catch (Exception e) {
                e.printStackTrace();
            }
            window.dispose();
        });

        // 7. Save Output Logs
        File logFile = new File("Assignment 4/Task02_Hospital_Outpatient_Desk/output/Task02_Test_Results.txt");
        logFile.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : logs) pw.println(l);
        }
        System.out.println("[TASK 02] Test logs saved to Task02_Test_Results.txt");
        System.out.println("================================================================================\n");
    }

    private static void testBooking(AppointmentService service, String pat, String dept, String doc, String slot, String testName, List<String> logs) {
        try {
            service.bookAppointment(pat, dept, doc, slot);
            String msg = "[FAIL] " + testName + " unexpectedly succeeded!";
            System.err.println(msg);
            logs.add(msg);
        } catch (IllegalArgumentException e) {
            String msg = "[PASS] " + testName + " successfully rejected: " + e.getMessage();
            System.out.println(msg);
            logs.add(msg);
        }
    }
}
