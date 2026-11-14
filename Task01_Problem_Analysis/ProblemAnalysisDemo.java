/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 01: Problem Analysis and Object Identification
 * 
 * Demonstrates:
 * 1. Parallel Array approach vs. Object-Oriented approach.
 * 2. Risk of data inconsistency when updating parallel arrays.
 * 3. Search and space complexity comparison (Linear Search O(N) vs Map O(1)).
 */
import java.util.*;

public class ProblemAnalysisDemo {

    // Representation 1: Parallel Arrays (Procedural / Fragile)
    static class ParallelArrayDemo {
        static final int CAPACITY = 5;
        static String[] regNumbers = new String[CAPACITY];
        static String[] vehicleTypes = new String[CAPACITY];
        static int[] floorNumbers = new int[CAPACITY];
        static int[] slotNumbers = new int[CAPACITY];
        static boolean[] isOccupied = new boolean[CAPACITY];
        static int count = 0;

        public static void park(String reg, String type, int floor, int slot) {
            if (count >= CAPACITY) {
                System.out.println("[Parallel Arrays] Parking Full!");
                return;
            }
            regNumbers[count] = reg;
            vehicleTypes[count] = type;
            floorNumbers[count] = floor;
            slotNumbers[count] = slot;
            isOccupied[count] = true;
            count++;
        }

        // Searching in parallel arrays requires O(N) linear scan
        public static int findByRegNumber(String reg) {
            for (int i = 0; i < count; i++) {
                if (reg.equalsIgnoreCase(regNumbers[i])) {
                    return i;
                }
            }
            return -1;
        }

        // Demonstration of fragility: removing an element requires shifting ALL 5 arrays
        public static void remove(int index) {
            if (index < 0 || index >= count) return;
            for (int i = index; i < count - 1; i++) {
                regNumbers[i] = regNumbers[i + 1];
                vehicleTypes[i] = vehicleTypes[i + 1];
                floorNumbers[i] = floorNumbers[i + 1];
                slotNumbers[i] = slotNumbers[i + 1];
                isOccupied[i] = isOccupied[i + 1];
            }
            count--;
        }
    }

    // Representation 2: Object-Oriented Domain Model
    static class VehicleRecord {
        private final String registrationNumber;
        private final String vehicleType;
        private final int floor;
        private final int slot;
        private final long entryTimestamp;

        public VehicleRecord(String registrationNumber, String vehicleType, int floor, int slot) {
            this.registrationNumber = Objects.requireNonNull(registrationNumber, "Registration cannot be null");
            this.vehicleType = vehicleType;
            this.floor = floor;
            this.slot = slot;
            this.entryTimestamp = System.currentTimeMillis();
        }

        public String getRegistrationNumber() { return registrationNumber; }
        public String getVehicleType() { return vehicleType; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }

        @Override
        public String toString() {
            return String.format("VehicleRecord[Reg=%s, Type=%s, Location=F%d:S%d]",
                    registrationNumber, vehicleType, floor, slot);
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("TASK 01: Problem Analysis & Object Identification");
        System.out.println("==================================================\n");

        System.out.println("--- 1. Parallel Array Demonstration (Fragile) ---");
        ParallelArrayDemo.park("KA01AB1234", "Car", 0, 1);
        ParallelArrayDemo.park("MH02CD5678", "TwoWheeler", 0, 2);
        ParallelArrayDemo.park("DL03EF9012", "ElectricCar", 1, 1);

        int idx = ParallelArrayDemo.findByRegNumber("MH02CD5678");
        System.out.println("Search 'MH02CD5678' in parallel arrays: Index " + idx + 
                           " (Slot: F" + ParallelArrayDemo.floorNumbers[idx] + ":S" + 
                           ParallelArrayDemo.slotNumbers[idx] + ")");

        System.out.println("\n--- 2. Object-Oriented Model Demonstration (Robust) ---");
        Map<String, VehicleRecord> activeParking = new HashMap<>();
        VehicleRecord v1 = new VehicleRecord("KA01AB1234", "Car", 0, 1);
        VehicleRecord v2 = new VehicleRecord("MH02CD5678", "TwoWheeler", 0, 2);
        VehicleRecord v3 = new VehicleRecord("DL03EF9012", "ElectricCar", 1, 1);

        activeParking.put(v1.getRegistrationNumber(), v1);
        activeParking.put(v2.getRegistrationNumber(), v2);
        activeParking.put(v3.getRegistrationNumber(), v3);

        System.out.println("Lookup by Key (Registration Number O(1)):");
        VehicleRecord found = activeParking.get("MH02CD5678");
        System.out.println("Found: " + found);

        System.out.println("\n--- 3. Complexity Analysis ---");
        System.out.println("Parallel Arrays:");
        System.out.println("  - Search Time Complexity: O(N) linear search");
        System.out.println("  - Insertion Time Complexity: O(1) amortized");
        System.out.println("  - Deletion Time Complexity: O(N) (shifting 5 parallel arrays)");
        System.out.println("  - Data Integrity Risk: HIGH (unsynchronized modifications)");
        System.out.println("OOP Model with HashMap / Index:");
        System.out.println("  - Search Time Complexity: O(1) average lookup");
        System.out.println("  - Insertion Time Complexity: O(1)");
        System.out.println("  - Deletion Time Complexity: O(1)");
        System.out.println("  - Data Integrity: Encapsulated, strongly typed, coherent");
        System.out.println("==================================================");
    }
}
