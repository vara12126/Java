package Task04;

/**
 * Task 04 Demonstration:
 * Classes, Objects, Parameterized Constructors, Constructor Chaining,
 * Copy Constructors, and Object Creation Paradigms.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class ClassDesignDemo {

    // Domain Class: ParkingTicket demonstrating constructor chaining and copy constructor
    public static class ParkingTicket {
        private final int ticketId;
        private final String vehicleRegNo;
        private final int floor;
        private final int slot;
        private final long entryTime;
        private final boolean isDuplicate;

        // Master Parameterized Constructor
        public ParkingTicket(int ticketId, String vehicleRegNo, int floor, int slot, long entryTime) {
            if (ticketId <= 0) throw new IllegalArgumentException("Invalid ticket ID: " + ticketId);
            if (vehicleRegNo == null || vehicleRegNo.trim().isEmpty()) 
                throw new IllegalArgumentException("Vehicle RegNo cannot be blank");
            
            this.ticketId = ticketId;
            this.vehicleRegNo = vehicleRegNo;
            this.floor = floor;
            this.slot = slot;
            this.entryTime = entryTime;
            this.isDuplicate = false;
        }

        // Overloaded Constructor: Chains to master constructor with fixed reference time
        public ParkingTicket(int ticketId, String vehicleRegNo, int floor, int slot) {
            this(ticketId, vehicleRegNo, floor, slot, 1716300000000L); // Constructor Chaining using this()
        }

        // Copy Constructor: Used to re-issue duplicate tickets safely
        public ParkingTicket(ParkingTicket original) {
            this.ticketId = original.ticketId;
            this.vehicleRegNo = original.vehicleRegNo;
            this.floor = original.floor;
            this.slot = original.slot;
            this.entryTime = original.entryTime;
            this.isDuplicate = true; // Mark as duplicate for audit trail
        }

        // Static Factory Method
        public static ParkingTicket issue(int id, String regNo, int floor, int slot) {
            return new ParkingTicket(id, regNo, floor, slot);
        }

        public int getTicketId() { return ticketId; }
        public String getVehicleRegNo() { return vehicleRegNo; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public long getEntryTime() { return entryTime; }
        public boolean isDuplicate() { return isDuplicate; }

        @Override
        public String toString() {
            return "Ticket[ID=" + ticketId + ", RegNo=" + vehicleRegNo 
                + ", Slot=[" + floor + ", " + slot + "], EntryTime=" + entryTime 
                + ", Duplicate=" + isDuplicate + "]";
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 04: CONSTRUCTORS & OBJECT CREATION DEMO            ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        // 1. Parameterized Constructor
        ParkingTicket t1 = new ParkingTicket(101, "TN01AB1111", 0, 0, 1716300100000L);
        System.out.println("1. Master Constructor Ticket: " + t1);

        // 2. Overloaded Constructor (Constructor Chaining)
        ParkingTicket t2 = new ParkingTicket(102, "TN02CD2222", 0, 1);
        System.out.println("2. Chained Constructor Ticket: " + t2);

        // 3. Copy Constructor (Re-issuing duplicate ticket)
        ParkingTicket t2Duplicate = new ParkingTicket(t2);
        System.out.println("3. Copy Constructor (Duplicate): " + t2Duplicate);
        System.out.println("   Are references identical? " + (t2 == t2Duplicate ? "Yes (Shallow)" : "No (Independent Heap Objects)"));

        // 4. Static Factory Method
        ParkingTicket t3 = ParkingTicket.issue(103, "TN03EF3333", 1, 0);
        System.out.println("4. Static Factory Ticket: " + t3);

        System.out.println("\nObject creation succeeded with all invariants guaranteed at initialization.");
    }
}
