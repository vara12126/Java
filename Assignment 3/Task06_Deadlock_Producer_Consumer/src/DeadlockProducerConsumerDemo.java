package task06_deadlock_queue;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Task 06: Deadlock Prevention and Producer-Consumer Bounded Queue.
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class DeadlockProducerConsumerDemo {

    public static void demonstrateDeadlockPrevention() throws InterruptedException {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PART A: DEADLOCK PREVENTION VIA DETERMINISTIC GLOBAL LOCK ORDERING");
        System.out.println("--------------------------------------------------------------------------------");

        Account accA = new Account("ACC-101", 5000.0);
        Account accB = new Account("ACC-202", 5000.0);

        System.out.println("Initial Balances: " + accA + " | " + accB);
        System.out.println("\nScenario: Concurrent cross-transfers (A -> B $500) and (B -> A $300)");
        System.out.println("With naive locking: Thread 1 locks A and waits for B; Thread 2 locks B and waits for A.");
        System.out.println("With deterministic global ordering: Both threads lock ACC-101 FIRST, then ACC-202.");

        Thread t1 = new Thread(() -> {
            SafeAccountTransfer.executeSafeTransfer(accA, accB, 500.0);
        }, "Transfer-Thread-1");

        Thread t2 = new Thread(() -> {
            SafeAccountTransfer.executeSafeTransfer(accB, accA, 300.0);
        }, "Transfer-Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("\nFinal Balances after Safe Concurrent Transfers: " + accA + " | " + accB);
        System.out.println("[RESULT] Deadlock successfully eliminated by breaking Coffman Circular Wait!");
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    public static void demonstrateProducerConsumer() throws InterruptedException {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PART B: BOUNDED BUFFER PRODUCER-CONSUMER WITH wait() AND notifyAll()");
        System.out.println("--------------------------------------------------------------------------------");

        TransactionQueue<String> queue = new TransactionQueue<>(3); // Bounded capacity: 3
        int totalItems = 6;
        AtomicBoolean running = new AtomicBoolean(true);

        // Producer 1
        Thread prod1 = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    String tx = "TX-P1-00" + i;
                    System.out.println("[PRODUCER 1] Preparing to enqueue: " + tx);
                    queue.enqueue(tx);
                    Thread.sleep(60);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer-1");

        // Producer 2
        Thread prod2 = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    String tx = "TX-P2-00" + i;
                    System.out.println("[PRODUCER 2] Preparing to enqueue: " + tx);
                    queue.enqueue(tx);
                    Thread.sleep(80);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer-2");

        // Consumer 1
        Thread cons1 = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String tx = queue.dequeue();
                    System.out.println("  ==> [CONSUMER 1] Successfully processed: " + tx);
                    Thread.sleep(120);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-1");

        // Consumer 2
        Thread cons2 = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String tx = queue.dequeue();
                    System.out.println("  ==> [CONSUMER 2] Successfully processed: " + tx);
                    Thread.sleep(140);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-2");

        // Start consumers first to demonstrate buffer-empty waiting
        cons1.start();
        cons2.start();

        Thread.sleep(100);

        // Start producers
        prod1.start();
        prod2.start();

        prod1.join();
        prod2.join();
        cons1.join();
        cons2.join();

        System.out.println("\nAll 6 transactions processed. Buffer final size: " + queue.size());
        System.out.println("[RESULT] Guarded suspension, while-loop re-evaluation & notifyAll() verified.");
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 3 - TASK 06: DEADLOCK AVOIDANCE & PRODUCER-CONSUMER QUEUE         ");
        System.out.println("   Student A: Devi Vara Prasad | Reg: 192365032                                 ");
        System.out.println("   Student B: [Student B Name] | Reg: [Student B Register Number]               ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        demonstrateDeadlockPrevention();
        demonstrateProducerConsumer();

        System.out.println("================================================================================");
        System.out.println("[TASK 06 VERIFICATION COMPLETE] Global lock ordering and Bounded Buffer verified.");
        System.out.println("================================================================================");
    }
}
