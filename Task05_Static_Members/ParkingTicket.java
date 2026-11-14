/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 05: Static Members and Object Life Cycle
 */
import java.time.LocalDateTime;

public class ParkingTicket {

    // 1. Static Members (Shared across all instances of ParkingTicket)
    private static int ticketCounter;
    private static final String PREFIX;

    // Static Initialization Block (Executes once when class is first loaded by ClassLoader)
    static {
        ticketCounter = 1000;
        PREFIX = "TICK-";
        System.out.println("[ParkingTicket] Static block executed. Initialized ticketCounter=" + ticketCounter);
    }

    // 2. Instance Members (Unique to each object instance)
    private final String ticketId;
    private final String vehicleReg;
    private final LocalDateTime issueTime;

    // Contrast member: An instance-level counter (Flawed for global uniqueness)
    private int instanceTicketCounter = 1000;

    public ParkingTicket(String vehicleReg) {
        // Increment shared class-level static counter
        ticketCounter++;
        this.ticketId = PREFIX + ticketCounter;
        this.vehicleReg = vehicleReg;
        this.issueTime = LocalDateTime.now();

        // Increment instance counter (only affects this single instance)
        this.instanceTicketCounter++;
    }

    // Static Methods
    public static int getTotalTicketsIssued() {
        return ticketCounter - 1000;
    }

    public static int getCurrentCounterValue() {
        return ticketCounter;
    }

    public static synchronized void resetCounter(int startValue) {
        ticketCounter = startValue;
        System.out.println("[ParkingTicket] Static counter reset to " + startValue);
    }

    // Instance Methods
    public String getTicketId() { return ticketId; }
    public String getVehicleReg() { return vehicleReg; }
    public LocalDateTime getIssueTime() { return issueTime; }
    public int getInstanceTicketCounter() { return instanceTicketCounter; }

    @Override
    public String toString() {
        return String.format("Ticket[ID=%s, Reg=%s, InstanceCount=%d, Issued=%s]",
                ticketId, vehicleReg, instanceTicketCounter, issueTime);
    }
}
