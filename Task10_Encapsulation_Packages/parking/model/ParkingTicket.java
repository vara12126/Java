package parking.model;

import java.time.LocalDateTime;

public class ParkingTicket {
    private static int counter = 1000;

    private final String ticketId;
    private final String vehicleRegistration;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double billedAmount;
    private boolean active;

    public ParkingTicket(String vehicleRegistration, int floor, int slot) {
        this.ticketId = "TICK-" + (++counter);
        this.vehicleRegistration = vehicleRegistration;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = LocalDateTime.now();
        this.active = true;
    }

    public String getTicketId() { return ticketId; }
    public String getVehicleRegistration() { return vehicleRegistration; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getBilledAmount() { return billedAmount; }
    public boolean isActive() { return active; }

    public void closeTicket(LocalDateTime exitTime, double fee) {
        if (!this.active) {
            throw new IllegalStateException("Ticket " + ticketId + " has already been closed.");
        }
        this.exitTime = exitTime != null ? exitTime : LocalDateTime.now();
        this.billedAmount = fee;
        this.active = false;
    }

    @Override
    public String toString() {
        return String.format("Ticket[ID=%s, Reg=%s, Slot=F%d:S%d, Active=%b, Fee=Rs %.1f]",
                ticketId, vehicleRegistration, floor, slot, active, billedAmount);
    }
}
