package Task10;

/**
 * Task 10 Demonstration:
 * Encapsulation, Access Modifiers, Defensive Data Validation,
 * and the Wrapper Autoboxing == Equality Trap.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class EncapsulationDemo {

    // Encapsulated Class with Validating Mutators
    public static class EncapsulatedParkingSlot {
        private final int floor;
        private final int slotNumber;
        private String parkedVehicle;
        private int durationMinutes;

        public EncapsulatedParkingSlot(int floor, int slotNumber) {
            if (floor < 0) throw new IllegalArgumentException("Floor cannot be negative");
            if (slotNumber < 0) throw new IllegalArgumentException("Slot cannot be negative");
            this.floor = floor;
            this.slotNumber = slotNumber;
        }

        public void setDurationMinutes(int durationMinutes) {
            if (durationMinutes < 0) {
                throw new IllegalArgumentException("Validation Error: Duration cannot be negative: " + durationMinutes);
            }
            this.durationMinutes = durationMinutes;
        }

        public void parkVehicle(String regNo) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Validation Error: Vehicle registration number cannot be blank");
            }
            if (this.parkedVehicle != null) {
                throw new IllegalStateException("Validation Error: Slot is already occupied by " + this.parkedVehicle);
            }
            this.parkedVehicle = regNo;
        }

        public void vacate() {
            this.parkedVehicle = null;
        }

        public int getFloor() { return floor; }
        public int getSlotNumber() { return slotNumber; }
        public String getParkedVehicle() { return parkedVehicle; }
        public int getDurationMinutes() { return durationMinutes; }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 10: ENCAPSULATION & DATA VALIDATION DEMO           ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        EncapsulatedParkingSlot slot = new EncapsulatedParkingSlot(1, 5);
        System.out.println("Created Slot: Floor " + slot.getFloor() + ", Slot " + slot.getSlotNumber());

        // 1. Successful Valid Operations
        slot.parkVehicle("TN01AA5555");
        slot.setDurationMinutes(75);
        System.out.println("Slot parked vehicle: " + slot.getParkedVehicle() + ", Duration: " + slot.getDurationMinutes() + " mins");

        // 2. Testing Defensive Validation - Negative Duration
        System.out.println("\n--- Defensive Validation Tests ---");
        try {
            System.out.println("Attempting to set negative duration (-45 mins)...");
            slot.setDurationMinutes(-45);
        } catch (IllegalArgumentException ex) {
            System.out.println("Caught Expected Exception: " + ex.getMessage());
        }

        // 3. Testing Defensive Validation - Double Occupancy
        try {
            System.out.println("Attempting to park another car into already occupied slot...");
            slot.parkVehicle("TN02BB6666");
        } catch (IllegalStateException ex) {
            System.out.println("Caught Expected Exception: " + ex.getMessage());
        }

        // 4. Wrapper Classes and Autoboxing == Trap
        System.out.println("\n--- Wrapper Autoboxing & == Pitfall Demonstration ---");
        Integer smallA = 100;
        Integer smallB = 100;
        System.out.println("Comparing Integer 100 == 100 (Cached range -128 to 127): " + (smallA == smallB));

        Integer largeA = 500;
        Integer largeB = 500;
        System.out.println("Comparing Integer 500 == 500 using ==:     " + (largeA == largeB) + " (BUG: Different heap objects!)");
        System.out.println("Comparing Integer 500.equals(500):        " + largeA.equals(largeB) + " (CORRECT: Value comparison)");

        System.out.println("\nEncapsulation and validating setters successfully protected internal state.");
    }
}
