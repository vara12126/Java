/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 03: Control Logic
 * 
 * Implementations:
 * 1. Slot allocation using nested loops and early return.
 * 2. Slot release with boundary checks and return flags.
 * 3. Parking fee calculation using switch-case, if-else, break, and continue.
 * 4. Exact assignment verification output:
 *    [0, 0]
 *    [0, 1]
 *    [1, 0]
 *    null
 *    true false false
 *    [0, 1]
 */
import java.util.Arrays;

public class ParkingFacility {

    // 2D boolean array representing slot occupancy: true = occupied, false = free
    private final boolean[][] slots;

    public ParkingFacility(int[] floorCapacities) {
        slots = new boolean[floorCapacities.length][];
        for (int i = 0; i < floorCapacities.length; i++) {
            slots[i] = new boolean[floorCapacities[i]];
        }
    }

    /**
     * Allocates the first available parking slot using First-Fit algorithm.
     * Uses nested loops with return / break.
     * 
     * @return int[] containing {floor, slot}, or null if the facility is full.
     */
    public int[] allocateSlot() {
        for (int floor = 0; floor < slots.length; floor++) {
            for (int slot = 0; slot < slots[floor].length; slot++) {
                if (!slots[floor][slot]) { // Slot is free
                    slots[floor][slot] = true; // Mark occupied
                    return new int[]{floor, slot}; // Return immediately
                }
            }
        }
        return null; // Facility full
    }

    /**
     * Releases an occupied slot.
     * 
     * @param floor Floor index
     * @param slot  Slot index
     * @return true if successfully freed; false if already free or invalid coordinate
     */
    public boolean releaseSlot(int floor, int slot) {
        // Boundary validation
        if (floor < 0 || floor >= slots.length) {
            return false;
        }
        if (slot < 0 || slot >= slots[floor].length) {
            return false;
        }

        // Check if currently occupied
        if (slots[floor][slot]) {
            slots[floor][slot] = false;
            return true;
        }

        // Already empty
        return false;
    }

    /**
     * Computes parking fee using switch-case and if-else control logic.
     * 
     * @param vehicleType Type of vehicle ("TwoWheeler", "Car", "ElectricCar")
     * @param hours       Duration in hours
     * @param hasPass     Whether the driver holds a valid campus concession pass
     * @return Total fee in INR
     */
    public double calculateFee(String vehicleType, int hours, boolean hasPass) {
        if (hours <= 0) {
            return 0.0;
        }

        double ratePerHour;
        switch (vehicleType.toLowerCase()) {
            case "twowheeler":
                ratePerHour = 10.0;
                break;
            case "car":
                ratePerHour = 30.0;
                break;
            case "electriccar":
                ratePerHour = 50.0; // Includes standard EV hourly fee
                break;
            default:
                ratePerHour = 20.0;
                break;
        }

        double total = hours * ratePerHour;
        if (hasPass) {
            total = total * 0.5; // 50% concession for pass holders
        }
        return total;
    }

    public static void main(String[] args) {
        // Floor capacities: Floor 0 has 2 slots, Floor 1 has 1 slot (Total capacity: 3 slots)
        int[] capacities = {2, 1};
        ParkingFacility facility = new ParkingFacility(capacities);

        // Verification sequence matching assignment requirements exactly:
        int[] s1 = facility.allocateSlot();
        int[] s2 = facility.allocateSlot();
        int[] s3 = facility.allocateSlot();
        int[] s4 = facility.allocateSlot(); // Should be null (full)

        System.out.println(Arrays.toString(s1));
        System.out.println(Arrays.toString(s2));
        System.out.println(Arrays.toString(s3));
        System.out.println(Arrays.toString(s4));

        // Release sequence:
        // 1. Release slot [0, 1] (occupied -> true)
        // 2. Release slot [0, 1] again (already free -> false)
        // 3. Release invalid slot [3, 0] (out of bounds -> false)
        boolean r1 = facility.releaseSlot(0, 1);
        boolean r2 = facility.releaseSlot(0, 1);
        boolean r3 = facility.releaseSlot(3, 0);

        System.out.println(r1 + " " + r2 + " " + r3);

        // Reallocate: [0, 1] should be reclaimed
        int[] s5 = facility.allocateSlot();
        System.out.println(Arrays.toString(s5));
    }
}
