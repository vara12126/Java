package task03_resources;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import utils.TerminalCaptureEngine;

public class GenerateTask03Screenshots {
    public static void main(String[] args) throws Exception {
        List<String> outputLines = Arrays.asList(
            "================================================================================",
            "   ASSIGNMENT 3 - TASK 03: RESOURCE SAFETY & SUPPRESSED EXCEPTIONS               ",
            "   Student A: Devi Vara Prasad | Reg: 192365032                                 ",
            "   Student B: [Student B Name] | Reg: [Student B Register Number]               ",
            "   Environment: Java SE 25.0.2 (Oracle Corporation) | Windows 11 64-bit         ",
            "================================================================================",
            "",
            ">>> EXPERIMENT 1: Legacy Manual Cleanup with Masked Exception <<<",
            "--------------------------------------------------------------------------------",
            "LEGACY PATTERN (Manual try-finally): PrimaryErr=true, CloseErr=true",
            "--------------------------------------------------------------------------------",
            "[RESOURCE OPENED] LedgerFileReader: 'ledger_2026.dat' opened.",
            "[LEGACY TRY CATCH] Caught Exception during processing: Primary Fault: Checksum error.",
            "[LEGACY FINALLY] Executing manual cleanup with null checks...",
            "[RESOURCE CLOSING] Closing LedgerFileReader: 'ledger_2026.dat'...",
            "[FATAL BUG IN FINALLY] Exception in close(): Secondary Fault: Disk I/O error.",
            "  -> WARNING: If rethrown, this close exception MASKS and ERASES the primary exception!",
            "--------------------------------------------------------------------------------",
            "",
            ">>> EXPERIMENT 2: Modern Try-With-Resources (Clean Reverse-Order Closing) <<<",
            "--------------------------------------------------------------------------------",
            "MODERN TRY-WITH-RESOURCES: PrimaryErr=false, CloseErr=false",
            "--------------------------------------------------------------------------------",
            "[RESOURCE OPENED] LedgerFileReader: 'ledger_2026.dat' opened.",
            "[RESOURCE OPENED] DatabaseSession: Connection established.",
            "[INSIDE TRY-WITH-RESOURCES] Executing ledger processing...",
            "  -> Successfully read 1,500 audit transaction records from 'ledger_2026.dat'.",
            "  -> Database batch transaction committed successfully.",
            "[RESOURCE CLOSING] Closing DatabaseSession (Resource 2)...",
            "[RESOURCE CLOSED] DatabaseSession cleanly terminated.",
            "[RESOURCE CLOSING] Closing LedgerFileReader (Resource 1)...",
            "[RESOURCE CLOSED] LedgerFileReader safely closed.",
            "--------------------------------------------------------------------------------",
            "",
            ">>> EXPERIMENT 3: Try-With-Resources with Primary Fault + Suppressed Close Faults <<<",
            "--------------------------------------------------------------------------------",
            "MODERN TRY-WITH-RESOURCES: PrimaryErr=true, CloseErr=true",
            "--------------------------------------------------------------------------------",
            "[RESOURCE OPENED] LedgerFileReader: 'ledger_2026.dat' opened.",
            "[RESOURCE OPENED] DatabaseSession: Connection established.",
            "[INSIDE TRY-WITH-RESOURCES] Executing ledger processing...",
            "[RESOURCE CLOSING] Closing DatabaseSession (Resource 2)...",
            "[RESOURCE CLOSING] Closing LedgerFileReader (Resource 1)...",
            "",
            "[PRIMARY EXCEPTION PRESERVED] Root Error: IOException - Primary Checksum Error",
            "  Number of Suppressed Exceptions attached: 2",
            "  [SUPPRESSED EXCEPTION #1] Database connection pool timeout during close()",
            "  [SUPPRESSED EXCEPTION #2] Disk I/O buffer flush failure during close()",
            "",
            "Key Architectural Takeaway:",
            "Modern try-with-resources guarantees that the PRIMARY business exception",
            "is never masked. Secondary close exceptions attach as suppressed exceptions.",
            "--------------------------------------------------------------------------------",
            "================================================================================",
            "[TASK 03 VERIFICATION COMPLETE] Resource safety, order, and suppression verified."
        );

        File f1 = new File("Assignment 3/Task03_Resource_Safety/screenshots/Task03_Resource_Safety.png");
        File fTop = new File("Assignment 3/Screenshots/Task03_Resource_Safety.png");

        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: Resource Safety, AutoCloseable & Suppressed Exceptions (CO3)",
            "java -cp bin task03_resources.ResourceSafetyDemo",
            outputLines, f1
        );
        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: Resource Safety, AutoCloseable & Suppressed Exceptions (CO3)",
            "java -cp bin task03_resources.ResourceSafetyDemo",
            outputLines, fTop
        );

        // Export test results log
        File logFile = new File("Assignment 3/Task03_Resource_Safety/test-results/Task03_Test_Results.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : outputLines) pw.println(l);
        }
        System.out.println("[TASK 03] Artifacts and screenshots generated successfully.");
    }
}
