/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Five-floor parking facility with jagged array non-uniform floor capacities.
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class ExtendedParkingFacility {

    private final String name;
    // 5 floors with differing slot capacities
    private final ParkingSlot[][] grid;
    private final Map<String, ActiveSession> activeSessions;
    private BigDecimal totalDailyRevenue;

    public static class ActiveSession {
        final String ticketId;
        final Vehicle vehicle;
        final int floor;
        final int slot;
        final Pass pass;
        final LocalDateTime entryTime;

        ActiveSession(String ticketId, Vehicle vehicle, int floor, int slot, Pass pass, LocalDateTime entryTime) {
            this.ticketId = ticketId;
            this.vehicle = vehicle;
            this.floor = floor;
            this.slot = slot;
            this.pass = pass;
            this.entryTime = entryTime;
        }

        public String getTicketId() { return ticketId; }
        public Vehicle getVehicle() { return vehicle; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public Pass getPass() { return pass; }
        public LocalDateTime getEntryTime() { return entryTime; }
    }

    public ExtendedParkingFacility(String name, int[] fiveFloorCapacities) {
        if (fiveFloorCapacities == null || fiveFloorCapacities.length < 5) {
            throw new IllegalArgumentException("Facility must support at least 5 floors.");
        }
        this.name = name;
        this.grid = new ParkingSlot[fiveFloorCapacities.length][];
        for (int f = 0; f < fiveFloorCapacities.length; f++) {
            this.grid[f] = new ParkingSlot[fiveFloorCapacities[f]];
            for (int s = 0; s < fiveFloorCapacities[f]; s++) {
                this.grid[f][s] = new ParkingSlot(f, s);
            }
        }
        this.activeSessions = new HashMap<>();
        this.totalDailyRevenue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public ActiveSession park(Vehicle vehicle, Pass pass, LocalDateTime entryTime) {
        String reg = vehicle.getRegistrationNumber();
        if (activeSessions.containsKey(reg)) {
            throw new IllegalStateException("Duplicate entry! Vehicle " + reg + " is already inside.");
        }

        // Search for first free slot across the 5 floors
        for (int f = 0; f < grid.length; f++) {
            for (int s = 0; s < grid[f].length; s++) {
                if (!grid[f][s].isOccupied()) {
                    grid[f][s].assignVehicle(vehicle);
                    String ticketId = "T12-" + (1001 + activeSessions.size());
                    ActiveSession session = new ActiveSession(ticketId, vehicle, f, s, pass, entryTime);
                    activeSessions.put(reg, session);
                    return session;
                }
            }
        }
        return null; // Full
    }

    public BigDecimal exit(String registrationNumber, LocalDateTime exitTime) {
        String cleanReg = registrationNumber.trim().toUpperCase();
        ActiveSession session = activeSessions.get(cleanReg);
        if (session == null) {
            throw new NoSuchElementException("No active session for vehicle: " + cleanReg);
        }

        // Vacate slot
        grid[session.floor][session.slot].vacate();

        // 1. Calculate base fee via Strategy Pattern PricingRule
        BigDecimal grossFee = session.vehicle.computeFee(session.entryTime, exitTime);

        // 2. Apply Pass discount if valid pass held
        BigDecimal netFee = grossFee;
        if (session.pass != null && session.pass.isValid(exitTime)) {
            netFee = session.pass.applyDiscount(grossFee);
        }

        activeSessions.remove(cleanReg);
        this.totalDailyRevenue = this.totalDailyRevenue.add(netFee);

        return netFee;
    }

    public void displayOccupancy() {
        System.out.println("=== 5-FLOOR EXTENDED FACILITY OCCUPANCY: " + name + " ===");
        int totalSlots = 0;
        int occupied = 0;
        for (int f = 0; f < grid.length; f++) {
            System.out.printf("Floor %d (Capacity: %2d slots): ", f, grid[f].length);
            for (int s = 0; s < grid[f].length; s++) {
                totalSlots++;
                if (grid[f][s].isOccupied()) {
                    occupied++;
                    System.out.print("[" + grid[f][s].getParkedVehicle().getRegistrationNumber() + "] ");
                } else {
                    System.out.print("[FREE] ");
                }
            }
            System.out.println();
        }
        System.out.printf("Total Capacity: %d slots across %d floors | Occupied: %d | Available: %d%n",
                totalSlots, grid.length, occupied, totalSlots - occupied);
    }

    public int getFloorCount() { return grid.length; }
    public int getFloorCapacity(int floor) { return grid[floor].length; }
    public BigDecimal getTotalDailyRevenue() { return totalDailyRevenue; }
}
