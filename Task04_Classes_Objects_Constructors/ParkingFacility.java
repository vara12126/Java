/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 04: Classes, Objects and Constructors
 */
import java.util.ArrayList;
import java.util.List;

public class ParkingFacility {
    private final String facilityName;
    private final boolean[][] slots;
    private final List<ParkingTicket> issuedTickets;

    // Parameterized Constructor
    public ParkingFacility(String facilityName, int[] floorCapacities) {
        this.facilityName = facilityName != null ? facilityName : "Campus Central Parking";
        this.slots = new boolean[floorCapacities.length][];
        for (int i = 0; i < floorCapacities.length; i++) {
            this.slots[i] = new boolean[floorCapacities[i]];
        }
        this.issuedTickets = new ArrayList<>();
    }

    // Static Factory Method for standard campus parking configuration
    public static ParkingFacility createStandardCampusFacility() {
        return new ParkingFacility("Main Campus Smart Garage", new int[]{5, 5, 3});
    }

    public String getFacilityName() { return facilityName; }
    public int getFloorCount() { return slots.length; }

    public ParkingTicket parkVehicle(Vehicle vehicle, String ticketId) {
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (!slots[f][s]) {
                    slots[f][s] = true;
                    ParkingTicket ticket = new ParkingTicket(ticketId, vehicle.getRegistrationNumber(), f, s);
                    issuedTickets.add(ticket);
                    return ticket;
                }
            }
        }
        return null; // Full
    }

    public List<ParkingTicket> getIssuedTickets() {
        return new ArrayList<>(issuedTickets); // Defensive copy
    }
}
