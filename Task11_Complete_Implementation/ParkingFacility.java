/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingFacility {
    private final String name;
    private final ParkingSlot[][] grid;
    // Fast O(1) Lookups for active tickets
    private final Map<String, ParkingTicket> activeTicketsByReg;
    private final Map<String, ParkingTicket> activeTicketsById;
    private final List<ParkingTicket> settledTickets;
    private BigDecimal dailyTotalCollection;

    /**
     * Initializes a multi-floor parking facility.
     * 
     * @param name Name of the facility
     * @param floorCapacities Array of slot counts per floor
     * @param evSlotsPerFloor Array of EV-charging equipped slots count per floor
     */
    public ParkingFacility(String name, int[] floorCapacities, int[] evSlotsPerFloor) {
        this.name = name != null ? name : "Campus Smart Parking Facility";
        this.grid = new ParkingSlot[floorCapacities.length][];
        for (int f = 0; f < floorCapacities.length; f++) {
            int cap = floorCapacities[f];
            int evCap = (evSlotsPerFloor != null && f < evSlotsPerFloor.length) ? evSlotsPerFloor[f] : 0;
            grid[f] = new ParkingSlot[cap];
            for (int s = 0; s < cap; s++) {
                boolean isEv = (s < evCap); // First evCap slots on this floor are EV equipped
                grid[f][s] = new ParkingSlot(f, s, isEv);
            }
        }
        this.activeTicketsByReg = new HashMap<>();
        this.activeTicketsById = new HashMap<>();
        this.settledTickets = new ArrayList<>();
        this.dailyTotalCollection = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Vehicle Entry & Automatic Slot Allocation.
     */
    public ParkingTicket parkVehicle(Vehicle vehicle, PassType passType, boolean needsEvCharging, LocalDateTime entryTime) {
        Objects.requireNonNull(vehicle, "Vehicle cannot be null");
        String reg = vehicle.getRegistrationNumber();

        // 17. Duplicate registration check
        if (activeTicketsByReg.containsKey(reg)) {
            throw new IllegalStateException("Security Alert: Vehicle [" + reg + "] already has an active parking session!");
        }

        // 5. Automatic First-Fit slot allocation
        ParkingSlot targetSlot = null;

        // If EV charging is requested, prioritize EV-equipped slot
        if (needsEvCharging) {
            for (int f = 0; f < grid.length && targetSlot == null; f++) {
                for (int s = 0; s < grid[f].length; s++) {
                    if (!grid[f][s].isOccupied() && grid[f][s].isEvChargingCapable()) {
                        targetSlot = grid[f][s];
                        break;
                    }
                }
            }
        }

        // Fallback or standard allocation
        if (targetSlot == null) {
            for (int f = 0; f < grid.length && targetSlot == null; f++) {
                for (int s = 0; s < grid[f].length; s++) {
                    if (!grid[f][s].isOccupied()) {
                        targetSlot = grid[f][s];
                        break;
                    }
                }
            }
        }

        // 19. Parking Full notification
        if (targetSlot == null) {
            return null; // Facility full
        }

        targetSlot.assignVehicle(vehicle);

        // 6 & 7. Unique ticket generation & Entry time recording
        ParkingTicket ticket = new ParkingTicket(
                reg, vehicle.getVehicleType(), targetSlot.getFloor(), targetSlot.getSlotNumber(),
                passType, needsEvCharging, entryTime
        );

        // 16. Active ticket management (indexed in HashMaps)
        activeTicketsByReg.put(reg, ticket);
        activeTicketsById.put(ticket.getTicketId(), ticket);

        return ticket;
    }

    /**
     * Vehicle Exit & Settlement.
     */
    public ParkingTicket exitVehicle(String identifier, LocalDateTime exitTime) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID or Registration Number must be provided.");
        }
        String key = identifier.trim().toUpperCase().replaceAll("\\s+", "");

        // Lookup either by Ticket ID or by Registration Number
        ParkingTicket ticket = activeTicketsById.get(key);
        if (ticket == null) {
            ticket = activeTicketsByReg.get(key);
        }
        if (ticket == null) {
            throw new NoSuchElementException("No active parking record found for identifier: " + identifier);
        }

        // 15. Slot release
        int floor = ticket.getFloor();
        int slot = ticket.getSlot();
        Vehicle vehicle = grid[floor][slot].vacate();

        // Rates
        double baseRate = vehicle.getHourlyRate();
        double evRate = (vehicle instanceof ElectricCar) ? ((ElectricCar) vehicle).getChargingRatePerHour() : 0.0;

        // 8, 9, 10, 11, 12, 13, 14. Fee, EV fee, and pass calculations
        ticket.processExit(exitTime, baseRate, evRate);

        // Remove from active maps
        activeTicketsById.remove(ticket.getTicketId());
        activeTicketsByReg.remove(ticket.getRegistrationNumber());
        settledTickets.add(ticket);

        // 20. Update daily collection
        this.dailyTotalCollection = this.dailyTotalCollection.add(ticket.getNetFeePaid());

        return ticket;
    }

    public void displayGrid() {
        System.out.println("\n--- PARKING FACILITY OCCUPANCY GRID: " + name + " ---");
        int totalSlots = 0;
        int occupiedSlots = 0;

        for (int f = 0; f < grid.length; f++) {
            System.out.printf("Floor %d [%2d slots]: ", f, grid[f].length);
            for (int s = 0; s < grid[f].length; s++) {
                totalSlots++;
                if (grid[f][s].isOccupied()) {
                    occupiedSlots++;
                    System.out.print("[" + grid[f][s].getParkedVehicle().getRegistrationNumber() + "] ");
                } else {
                    System.out.print(grid[f][s].isEvChargingCapable() ? "[--EV--] " : "[------] ");
                }
            }
            System.out.println();
        }
        System.out.printf("Summary: %d / %d slots occupied (Available: %d)%n",
                occupiedSlots, totalSlots, (totalSlots - occupiedSlots));
    }

    public List<ParkingTicket> getActiveTicketsList() {
        return new ArrayList<>(activeTicketsById.values());
    }

    public BigDecimal getDailyTotalCollection() {
        return dailyTotalCollection;
    }

    public int getTotalAvailableSlots() {
        int free = 0;
        for (ParkingSlot[] floor : grid) {
            for (ParkingSlot slot : floor) {
                if (!slot.isOccupied()) free++;
            }
        }
        return free;
    }

    public String getName() {
        return name;
    }
}
