package parking.service;

import parking.model.ParkingSlot;
import parking.model.ParkingTicket;
import parking.model.Vehicle;

import java.util.*;

/**
 * Service orchestrating parking operations.
 * Demonstrates:
 * 1. Encapsulation of internal grid and collections.
 * 2. Defensive copies preventing outside tampering.
 * 3. Package imports and cross-package encapsulation boundaries.
 */
public class ParkingService {

    // Internal state is private - never exposed directly!
    private final ParkingSlot[][] slots;
    private final Map<String, ParkingTicket> activeTickets;
    private double dailyTotalRevenue;

    public ParkingService(int[] floorCapacities) {
        slots = new ParkingSlot[floorCapacities.length][];
        for (int f = 0; f < floorCapacities.length; f++) {
            slots[f] = new ParkingSlot[floorCapacities[f]];
            for (int s = 0; s < floorCapacities[f]; s++) {
                slots[f][s] = new ParkingSlot(f, s);
            }
        }
        this.activeTickets = new HashMap<>();
        this.dailyTotalRevenue = 0.0;
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "Vehicle cannot be null");
        String reg = vehicle.getRegistrationNumber();

        // Duplicate registration check
        if (activeTickets.containsKey(reg)) {
            throw new IllegalStateException("Duplicate entry rejected! Vehicle [" + reg + "] is already parked.");
        }

        // First-fit slot allocation
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (!slots[f][s].isOccupied()) {
                    slots[f][s].assignVehicle(vehicle);
                    ParkingTicket ticket = new ParkingTicket(reg, f, s);
                    activeTickets.put(reg, ticket);
                    return ticket;
                }
            }
        }
        return null; // Full
    }

    public double exitVehicle(String registrationNumber, int durationHours) {
        String cleanReg = ValidationService.validateAndNormalizeRegistration(registrationNumber);
        ParkingTicket ticket = activeTickets.get(cleanReg);
        if (ticket == null) {
            throw new NoSuchElementException("No active parking record found for registration: " + cleanReg);
        }

        // Vacate slot
        int floor = ticket.getFloor();
        int slot = ticket.getSlot();
        Vehicle vehicle = slots[floor][slot].vacate();

        // Compute fee
        double rate = vehicle.getBaseHourlyRate();
        Double feeObj = ValidationService.calculateValidatedFee(durationHours, rate, false);
        double fee = feeObj.doubleValue(); // Unboxing

        ticket.closeTicket(null, fee);
        activeTickets.remove(cleanReg);
        dailyTotalRevenue += fee;

        return fee;
    }

    public double getDailyTotalRevenue() {
        return dailyTotalRevenue;
    }

    // Defensive copy: Clients cannot modify our internal activeTickets map
    public Map<String, ParkingTicket> getActiveTickets() {
        return Collections.unmodifiableMap(new HashMap<>(activeTickets));
    }
}
