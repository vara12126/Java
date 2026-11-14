/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 04: Classes, Objects and Constructors
 */
import java.time.LocalDateTime;

public class ParkingTicket {
    private final String ticketId;
    private final String vehicleRegistration;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;

    // Overloaded Constructor with custom entry time (useful for simulation/testing)
    public ParkingTicket(String ticketId, String vehicleRegistration, int floor, int slot, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicleRegistration = vehicleRegistration;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime != null ? entryTime : LocalDateTime.now();
    }

    // Constructor chaining using `this(...)` to set entryTime as current time
    public ParkingTicket(String ticketId, String vehicleRegistration, int floor, int slot) {
        this(ticketId, vehicleRegistration, floor, slot, LocalDateTime.now());
    }

    public String getTicketId() { return ticketId; }
    public String getVehicleRegistration() { return vehicleRegistration; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }

    @Override
    public String toString() {
        return String.format("Ticket[ID=%s, Reg=%s, Location=[F%d, S%d], Time=%s]",
                ticketId, vehicleRegistration, floor, slot, entryTime);
    }
}
