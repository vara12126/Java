/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 05: Static Members and Object Life Cycle
 * 
 * Demonstrates:
 * 1. Static block execution order.
 * 2. Static variables vs. instance variables.
 * 3. Unique ticket ID generation across multiple object creations.
 * 4. Static utility methods.
 * 5. Object lifecycle and garbage collection eligibility.
 */
import java.lang.ref.Cleaner;

public class StaticDemo {

    // Demonstrating Object Life Cycle with java.lang.ref.Cleaner (Modern replacement for finalize)
    static class TrackedSession implements AutoCloseable {
        private static final Cleaner cleaner = Cleaner.create();
        private final String sessionId;
        private final Cleaner.Cleanable cleanable;

        private static class State implements Runnable {
            private final String id;
            State(String id) { this.id = id; }
            @Override
            public void run() {
                System.out.println("[Garbage Collector] TrackedSession " + id + " has been reclaimed from Heap!");
            }
        }

        public TrackedSession(String sessionId) {
            this.sessionId = sessionId;
            this.cleanable = cleaner.register(this, new State(sessionId));
            System.out.println("[Object Lifecycle] Created: " + sessionId + " in Heap");
        }

        @Override
        public void close() {
            cleanable.clean();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("TASK 05: Static Members and Object Life Cycle");
        System.out.println("==================================================\n");

        System.out.println("--- 1. Static Counter vs Instance Counter ---");
        ParkingTicket t1 = new ParkingTicket("KA01AA1001");
        ParkingTicket t2 = new ParkingTicket("KA02BB2002");
        ParkingTicket t3 = new ParkingTicket("KA03CC3003");

        System.out.println("Ticket 1 -> Static ID: " + t1.getTicketId() + " | Instance Counter: " + t1.getInstanceTicketCounter());
        System.out.println("Ticket 2 -> Static ID: " + t2.getTicketId() + " | Instance Counter: " + t2.getInstanceTicketCounter());
        System.out.println("Ticket 3 -> Static ID: " + t3.getTicketId() + " | Instance Counter: " + t3.getInstanceTicketCounter());

        System.out.println("\nObservation:");
        System.out.println("  - Static counter incremented across instances: " + ParkingTicket.getCurrentCounterValue());
        System.out.println("  - Total tickets issued: " + ParkingTicket.getTotalTicketsIssued());
        System.out.println("  - Instance counters remained fixed at 1001 because each instance has its own separate variable in heap!");

        System.out.println("\n--- 2. Object Life Cycle & Garbage Collection ---");
        // Creating objects that become unreachable
        System.out.println("Creating temporary session objects...");
        createTemporarySessions();

        // Suggesting GC
        System.out.println("Calling System.gc() to request reclamation of unreferenced objects...");
        System.gc();
        Thread.sleep(300); // Give cleaner thread a moment to process

        System.out.println("\n--- 3. Static Method Demonstration ---");
        System.out.println("Querying ParkingTicket.getTotalTicketsIssued(): " + ParkingTicket.getTotalTicketsIssued());
        ParkingTicket.resetCounter(5000);
        ParkingTicket t4 = new ParkingTicket("MH12ZZ9999");
        System.out.println("New ticket after reset: " + t4.getTicketId());
        System.out.println("==================================================");
    }

    private static void createTemporarySessions() {
        TrackedSession s1 = new TrackedSession("SESSION-ALPHA");
        TrackedSession s2 = new TrackedSession("SESSION-BETA");
        // As method ends, s1 and s2 leave stack frame scope and become eligible for GC
    }
}
