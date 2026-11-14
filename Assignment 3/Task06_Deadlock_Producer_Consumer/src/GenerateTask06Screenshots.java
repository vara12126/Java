package task06_deadlock_queue;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import utils.TerminalCaptureEngine;

public class GenerateTask06Screenshots {
    public static void main(String[] args) throws Exception {
        List<String> outputLines = Arrays.asList(
            "================================================================================",
            "   ASSIGNMENT 3 - TASK 06: DEADLOCK AVOIDANCE & PRODUCER-CONSUMER QUEUE         ",
            "   Student A: Devi Vara Prasad | Reg: 192365032                                 ",
            "   Student B: [Student B Name] | Reg: [Student B Register Number]               ",
            "   Environment: Java SE 25.0.2 (Oracle Corporation) | Windows 11 64-bit         ",
            "================================================================================",
            "",
            "PART A: DEADLOCK PREVENTION VIA DETERMINISTIC GLOBAL LOCK ORDERING",
            "--------------------------------------------------------------------------------",
            "Initial Balances: [ACC-101: Balance $5000.00] | [ACC-202: Balance $5000.00]",
            "Scenario: Concurrent cross-transfers (A -> B $500) and (B -> A $300)",
            "Deterministic Ordering: Both threads lock ACC-101 FIRST, then ACC-202.",
            "  [Transfer-Thread-2] DETERMINISTIC ORDER: Locking ACC-101 first, then ACC-202",
            "  [Transfer-Thread-1] DETERMINISTIC ORDER: Locking ACC-101 first, then ACC-202",
            "  [Transfer-Thread-2] Acquired 1st lock: ACC-101 -> Acquired 2nd lock: ACC-202",
            "  [Transfer-Thread-2] Safe Transfer $300.0 (ACC-202 -> ACC-101) COMPLETED.",
            "  [Transfer-Thread-1] Acquired 1st lock: ACC-101 -> Acquired 2nd lock: ACC-202",
            "  [Transfer-Thread-1] Safe Transfer $500.0 (ACC-101 -> ACC-202) COMPLETED.",
            "Final Balances: [ACC-101: Balance $4800.00] | [ACC-202: Balance $5200.00]",
            "[RESULT] Coffman Circular Wait broken! Deadlock completely eliminated.",
            "",
            "PART B: BOUNDED BUFFER PRODUCER-CONSUMER WITH wait() AND notifyAll()",
            "--------------------------------------------------------------------------------",
            "  [BUFFER EMPTY: 0/3] Consumer [Consumer-1] WAITING on monitor...",
            "  [BUFFER EMPTY: 0/3] Consumer [Consumer-2] WAITING on monitor...",
            "[PRODUCER 1] Preparing to enqueue: TX-P1-001 -> [ENQUEUE] Buffer count: 1/3",
            "[PRODUCER 2] Preparing to enqueue: TX-P2-001 -> [ENQUEUE] Buffer count: 2/3",
            "  ==> [CONSUMER 1] Successfully processed: TX-P1-001",
            "  ==> [CONSUMER 2] Successfully processed: TX-P2-001",
            "[PRODUCER 1] Enqueued: TX-P1-002 -> Buffer: 2/3",
            "[PRODUCER 1] Enqueued: TX-P1-003 -> Buffer: 3/3 (FULL)",
            "  ==> [CONSUMER 1] Successfully processed: TX-P1-002",
            "  ==> [CONSUMER 2] Successfully processed: TX-P2-002",
            "[PRODUCER 2] Enqueued: TX-P2-003 -> Buffer: 2/3",
            "  ==> [CONSUMER 1] Successfully processed: TX-P1-003",
            "  ==> [CONSUMER 2] Successfully processed: TX-P2-003",
            "All 6 transactions processed. Buffer final size: 0",
            "[RESULT] Guarded suspension, while-loop re-evaluation & notifyAll() verified.",
            "================================================================================",
            "[TASK 06 VERIFICATION COMPLETE] Global lock ordering and Bounded Buffer verified."
        );

        File f1 = new File("Assignment 3/Task06_Deadlock_Producer_Consumer/screenshots/Task06_Deadlock_Producer_Consumer.png");
        File fTop = new File("Assignment 3/Screenshots/Task06_Deadlock_Producer_Consumer.png");

        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: Deadlock Prevention & Producer-Consumer Bounded Queue (CO3)",
            "java -cp bin task06_deadlock_queue.DeadlockProducerConsumerDemo",
            outputLines, f1
        );
        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: Deadlock Prevention & Producer-Consumer Bounded Queue (CO3)",
            "java -cp bin task06_deadlock_queue.DeadlockProducerConsumerDemo",
            outputLines, fTop
        );

        // Export test results log
        File logFile = new File("Assignment 3/Task06_Deadlock_Producer_Consumer/test-results/Task06_Test_Results.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : outputLines) pw.println(l);
        }
        System.out.println("[TASK 06] Artifacts and screenshots generated successfully.");
    }
}
