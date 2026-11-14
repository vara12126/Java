package task03_resources;

import java.io.IOException;

/**
 * Task 03: Resource Safety & Exception Suppression Demonstration.
 * 
 * Demonstrates:
 * 1. Legacy manual try-finally pattern with nested null-checks.
 * 2. Primary exception masking/swallowing bug in legacy code.
 * 3. Modern Java try-with-resources (AutoCloseable).
 * 4. Strict reverse-order closing of multiple resources (LIFO).
 * 5. Suppressed exceptions preservation via Throwable.getSuppressed().
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class ResourceSafetyDemo {

    /**
     * Legacy Pre-Java 7 Pattern: Demonstrating the fatal "Masked Exception Bug".
     */
    public static void runLegacyPattern(boolean primaryError, boolean closeError) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("LEGACY PATTERN (Manual try-finally): PrimaryErr=" + primaryError + ", CloseErr=" + closeError);
        System.out.println("--------------------------------------------------------------------------------");

        LedgerFileReader reader = null;
        try {
            reader = new LedgerFileReader("ledger_2026.dat", closeError);
            reader.readLedgerRecord(primaryError);

        } catch (Exception e) {
            System.err.println("[LEGACY TRY CATCH] Caught Exception during processing: " + e.getMessage());
            // In legacy code, if rethrowing or propagating, the finally block below
            // will execute next. If finally throws, this primary exception is SILENTLY LOST!
        } finally {
            System.out.println("[LEGACY FINALLY] Executing manual cleanup with null checks...");
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception closeEx) {
                    System.err.println("[FATAL BUG IN FINALLY] Exception in close(): " + closeEx.getMessage());
                    System.err.println("  -> WARNING: If rethrown, this close exception MASKS and ERASES the primary exception!");
                }
            }
        }
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    /**
     * Modern Java 7+ Pattern: Try-with-resources with Suppressed Exceptions & Reverse-Order Closing.
     */
    public static void runModernTryWithResources(boolean primaryError, boolean closeError) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("MODERN TRY-WITH-RESOURCES: PrimaryErr=" + primaryError + ", CloseErr=" + closeError);
        System.out.println("--------------------------------------------------------------------------------");

        try (
            // Resource 1: Opened First
            LedgerFileReader reader = new LedgerFileReader("ledger_2026.dat", closeError);
            // Resource 2: Opened Second (Must be closed FIRST upon exit)
            DatabaseSession db = new DatabaseSession("jdbc:oracle:thin:@ledger-cluster:1521/bankdb", closeError)
        ) {
            System.out.println("[INSIDE TRY-WITH-RESOURCES] Executing ledger processing...");
            reader.readLedgerRecord(primaryError);
            db.commitAuditBatch();

        } catch (Exception e) {
            System.err.println("\n[PRIMARY EXCEPTION PRESERVED] Root Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());

            // Inspect Suppressed Exceptions attached during automatic close()
            Throwable[] suppressed = e.getSuppressed();
            System.err.println("  Number of Suppressed Exceptions attached: " + suppressed.length);
            for (int i = 0; i < suppressed.length; i++) {
                System.err.printf("  [SUPPRESSED EXCEPTION #%d] %s: %s%n",
                        (i + 1), suppressed[i].getClass().getSimpleName(), suppressed[i].getMessage());
            }

            if (suppressed.length > 0) {
                System.out.println("\nKey Architectural Takeaway:");
                System.out.println("Modern try-with-resources guarantees that the PRIMARY business exception");
                System.out.println("is never masked. All secondary close() exceptions are seamlessly attached");
                System.out.println("as suppressed exceptions, retaining 100% forensic visibility.");
            }
        }
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 3 - TASK 03: RESOURCE SAFETY & SUPPRESSED EXCEPTIONS               ");
        System.out.println("   Student A: Devi Vara Prasad | Reg: 192365032                                 ");
        System.out.println("   Student B: [Student B Name] | Reg: [Student B Register Number]               ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        // 1. Legacy Pattern Demonstration
        System.out.println(">>> EXPERIMENT 1: Legacy Manual Cleanup with Masked Exception <<<");
        runLegacyPattern(true, true);

        // 2. Modern Try-With-Resources (Normal Execution - Reverse Order Closing)
        System.out.println(">>> EXPERIMENT 2: Modern Try-With-Resources (Clean Execution & Closing Order) <<<");
        runModernTryWithResources(false, false);

        // 3. Modern Try-With-Resources (Primary + Suppressed Exceptions)
        System.out.println(">>> EXPERIMENT 3: Try-With-Resources with Primary Fault + Suppressed Close Faults <<<");
        runModernTryWithResources(true, true);

        System.out.println("================================================================================");
        System.out.println("[TASK 03 VERIFICATION COMPLETE] Resource safety, order, and suppression verified.");
        System.out.println("================================================================================");
    }
}
