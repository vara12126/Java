package q1_string;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demonstrates the critical concurrency difference between StringBuilder and StringBuffer.
 * 
 * In a real railway reservation system:
 * 1. Generating an individual ticket document is THREAD-CONFINED (local to the booking thread).
 *    Therefore, StringBuilder is strongly recommended because no synchronization is needed.
 * 2. Logging ticket issuance events into a SHARED AUDIT LOG from multiple concurrent worker threads
 *    requires synchronization. StringBuffer ensures thread safety and prevents corrupted text.
 */
public class SharedAuditLogDemo {

    public static void runThreadSafetyComparison(int numThreads, int appendsPerThread) {
        System.out.println("\n-----------------------------------------------------------------");
        System.out.println("   THREAD SAFETY DEMONSTRATION: SHARED AUDIT LOGGING");
        System.out.println("   Threads: " + numThreads + " | Appends per Thread: " + appendsPerThread);
        System.out.println("-----------------------------------------------------------------");

        final int expectedLength = numThreads * appendsPerThread * 10; // each append is 10 chars "1234567890"

        // 1. Shared StringBuilder test (Unsafe in concurrent access)
        final StringBuilder unsafeLog = new StringBuilder();
        ExecutorService executor1 = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch1 = new CountDownLatch(numThreads);

        long start1 = System.nanoTime();
        for (int t = 0; t < numThreads; t++) {
            executor1.submit(() -> {
                try {
                    for (int i = 0; i < appendsPerThread; i++) {
                        unsafeLog.append("ABCDEFGHIJ");
                    }
                } catch (Exception ex) {
                    // ArrayIndexOutOfBoundsException may occur due to unsynchronized internal buffer expansion
                    System.err.println("StringBuilder concurrency exception: " + ex.getMessage());
                } finally {
                    latch1.countDown();
                }
            });
        }

        try {
            latch1.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor1.shutdown();
        long end1 = System.nanoTime();

        // 2. Shared StringBuffer test (Safe in concurrent access)
        final StringBuffer safeLog = new StringBuffer();
        ExecutorService executor2 = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch2 = new CountDownLatch(numThreads);

        long start2 = System.nanoTime();
        for (int t = 0; t < numThreads; t++) {
            executor2.submit(() -> {
                try {
                    for (int i = 0; i < appendsPerThread; i++) {
                        safeLog.append("ABCDEFGHIJ");
                    }
                } finally {
                    latch2.countDown();
                }
            });
        }

        try {
            latch2.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor2.shutdown();
        long end2 = System.nanoTime();

        System.out.println("Expected Final Character Count : " + expectedLength);
        System.out.println("StringBuilder Actual Length    : " + unsafeLog.length() + 
                (unsafeLog.length() == expectedLength ? " (WARNING: No race observed in tiny run, but unsafe!)" : " [CORRUPTED / LOST DATA due to race condition!]"));
        System.out.println("StringBuffer Actual Length     : " + safeLog.length() + " [100% DATA INTEGRITY GUARANTEED]");
        System.out.printf("StringBuilder Wall Time        : %.3f ms%n", (end1 - start1) / 1_000_000.0);
        System.out.printf("StringBuffer Wall Time         : %.3f ms%n", (end2 - start2) / 1_000_000.0);

        System.out.println("\nArchitectural Verdict:");
        System.out.println("- Ticket Document Generation: Use StringBuilder (confined to a single request thread; zero lock overhead).");
        System.out.println("- Centralized Shared Audit Log: Use StringBuffer or synchronized logging wrapper to prevent data loss.");
    }
}
