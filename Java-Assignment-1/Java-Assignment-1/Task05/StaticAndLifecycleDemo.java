package Task05;

/**
 * Task 05 Demonstration:
 * Static Members, Object Lifecycle, Garbage Collection Eligibility,
 * and Multi-Facility Counter Collisions.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class StaticAndLifecycleDemo {

    // Demonstrating static vs instance counter
    static class FacilityWithInstanceCounter {
        private final String facilityName;
        private int counter = 0; // Instance counter (leads to collisions across gates!)

        public FacilityWithInstanceCounter(String name) { this.facilityName = name; }
        public String issueTicket() {
            counter++;
            return "Ticket #" + counter;
        }
    }

    static class FacilityWithPrefixedCounter {
        private final String facilityCode;
        private int counter = 0; // Prefixed instance counter (solves collision)

        public FacilityWithPrefixedCounter(String code) { this.facilityCode = code; }
        public String issueTicket() {
            counter++;
            return facilityCode + "-" + String.format("%04d", counter);
        }
    }

    static class TicketWithGlobalStaticCounter {
        private static int globalCounter = 1000; // Static shared counter
        private final int ticketId;
        private final String vehicleNo;

        public TicketWithGlobalStaticCounter(String vehicleNo) {
            this.ticketId = ++globalCounter;
            this.vehicleNo = vehicleNo;
        }

        public int getTicketId() { return ticketId; }
        public String getVehicleNo() { return vehicleNo; }

        @Override
        public String toString() {
            return "Ticket[ID=" + ticketId + ", RegNo=" + vehicleNo + "]";
        }
    }

    // Helper demonstrating pass-by-value of object reference
    public static void modifyTicket(TicketWithGlobalStaticCounter t) {
        // Mutating or reading the object affects caller's object
        System.out.println("   Inside method: Operating on ticket ID " + t.getTicketId());
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 05: STATIC MEMBERS & OBJECT LIFECYCLE DEMO         ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        // 1. Instance Counter Collision (Problem)
        System.out.println("--- 1. Multi-Facility Instance Counter Collision (Problem) ---");
        FacilityWithInstanceCounter northGate = new FacilityWithInstanceCounter("North Gate");
        FacilityWithInstanceCounter southGate = new FacilityWithInstanceCounter("South Gate");

        String t1North = northGate.issueTicket();
        String t1South = southGate.issueTicket();
        System.out.println("North Gate Issued: " + t1North);
        System.out.println("South Gate Issued: " + t1South);
        System.out.println("COLLISION DETECTED! Both gates issued identical ticket: " 
            + t1North.equals(t1South));

        // 2. Prefixed Counter (Solution A)
        System.out.println("\n--- 2. Prefixed Instance Counter (Solution A) ---");
        FacilityWithPrefixedCounter northPrefixed = new FacilityWithPrefixedCounter("NORTH");
        FacilityWithPrefixedCounter southPrefixed = new FacilityWithPrefixedCounter("SOUTH");
        System.out.println("North Gate Issued: " + northPrefixed.issueTicket());
        System.out.println("South Gate Issued: " + southPrefixed.issueTicket());
        System.out.println("Collision resolved via distinct namespaces!");

        // 3. Global Static Counter (Solution B)
        System.out.println("\n--- 3. Global Static Counter (Solution B) ---");
        TicketWithGlobalStaticCounter ticketA = new TicketWithGlobalStaticCounter("TN01AA1111");
        TicketWithGlobalStaticCounter ticketB = new TicketWithGlobalStaticCounter("TN02BB2222");
        System.out.println("First ticket:  " + ticketA);
        System.out.println("Second ticket: " + ticketB);

        // 4. Object Lifecycle and GC Eligibility
        System.out.println("\n--- 4. Object Lifecycle & Garbage Collection Eligibility ---");
        TicketWithGlobalStaticCounter activeTicket = new TicketWithGlobalStaticCounter("TN03CC3333");
        System.out.println("Vehicle entered: Created active ticket ID " + activeTicket.getTicketId());
        
        modifyTicket(activeTicket);

        System.out.println("Vehicle departs: Clearing ticket reference...");
        activeTicket = null; // Unreachable in heap! Now eligible for Garbage Collection
        System.out.println("activeTicket reference set to null. Object is now eligible for GC.");
    }
}
