/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 02: Data Representation
 * 
 * Demonstrates:
 * 1. Java data types (String, int, long, double, LocalDateTime).
 * 2. Jagged arrays representing non-uniform parking floors and slots.
 * 3. Integer division errors vs. proper casting in fee calculation.
 * 4. Comparison of 1D array, 2D jagged array, and ArrayList.
 */
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataRepresentationDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("TASK 02: Data Representation in Java");
        System.out.println("==================================================\n");

        // 1. Primitive and Reference Types Demonstration
        System.out.println("--- 1. Data Types in Smart Parking System ---");
        String regNumber = "KA05MH1001";              // Reference type: immutable string
        int floorIndex = 1;                           // 32-bit signed int: floor index
        int slotIndex = 3;                            // 32-bit signed int: slot index
        long epochMillis = System.currentTimeMillis();// 64-bit int: epoch timestamp
        double hourlyRate = 30.0;                     // 64-bit IEEE 754 float: rate
        LocalDateTime entryTime = LocalDateTime.now().minusHours(2).minusMinutes(35);
        LocalDateTime exitTime = LocalDateTime.now();

        System.out.println("Vehicle Registration (String): " + regNumber);
        System.out.println("Slot Location (int, int): Floor " + floorIndex + ", Slot " + slotIndex);
        System.out.println("Timestamp (long millis): " + epochMillis);
        System.out.println("Hourly Rate (double): Rs " + hourlyRate);
        System.out.println("Entry Time (LocalDateTime): " + entryTime);
        System.out.println("Exit Time (LocalDateTime): " + exitTime);

        // 2. Jagged Array Representation (Floors with varying slots)
        System.out.println("\n--- 2. Jagged Array Representation of Parking Floors ---");
        // Floor 0: Ground Floor (4 slots for Cars & Two-Wheelers)
        // Floor 1: First Floor (2 slots for EV charging)
        // Floor 2: Roof Floor (5 slots for general parking)
        String[][] parkingGrid = new String[3][];
        parkingGrid[0] = new String[4]; // 4 slots
        parkingGrid[1] = new String[2]; // 2 slots
        parkingGrid[2] = new String[5]; // 5 slots

        // Initialize slots as available (null indicates empty)
        parkingGrid[0][0] = "KA01A1111"; // Occupied
        parkingGrid[0][1] = null;         // Available
        parkingGrid[1][0] = "KA02B2222"; // Occupied
        parkingGrid[2][4] = "KA03C3333"; // Occupied

        System.out.println("Jagged Array Floor Layout:");
        for (int f = 0; f < parkingGrid.length; f++) {
            System.out.print("  Floor " + f + " (Capacity: " + parkingGrid[f].length + " slots): [ ");
            for (int s = 0; s < parkingGrid[f].length; s++) {
                System.out.print(parkingGrid[f][s] == null ? "[EMPTY] " : "[" + parkingGrid[f][s] + "] ");
            }
            System.out.println("]");
        }

        // 3. Integer Division and Casting Errors in Fee Calculation
        System.out.println("\n--- 3. Integer Division & Casting Pitfall Demonstration ---");
        long durationMinutes = 75; // 1 hour and 15 minutes
        double rate = 40.0;

        // BUGGY: Integer division truncates 75 / 60 to 1 before multiplication
        double buggyFee1 = (durationMinutes / 60) * rate; // 75/60 = 1 -> 40.0 (misses 15 mins)

        // WORSE BUG: If duration is 45 minutes, 45 / 60 = 0! Result: Rs 0.0!
        long shortDurationMinutes = 45;
        double buggyFee2 = (shortDurationMinutes / 60) * rate; // 45/60 = 0 -> Rs 0.0!

        // CORRECT METHOD A: Explicit cast to double with fractional hours
        double correctFractionalFee = ((double) durationMinutes / 60.0) * rate;

        // CORRECT METHOD B: Hourly Ceiling (Standard Parking Policy - round up fractional hours)
        long billableHours = (long) Math.ceil((double) durationMinutes / 60.0);
        double correctCeilFee = billableHours * rate;

        System.out.println("Duration: " + durationMinutes + " minutes | Rate: Rs " + rate + "/hr");
        System.out.println("  [BUGGY] (75 / 60) * rate = Rs " + buggyFee1 + " (Integer division truncated)");
        System.out.println("  [BUGGY for 45 min] (45 / 60) * rate = Rs " + buggyFee2 + " (Customer parked 45m for free!)");
        System.out.println("  [CORRECT Fractional] ((double) 75 / 60.0) * rate = Rs " + correctFractionalFee);
        System.out.println("  [CORRECT Ceiling Policy] Math.ceil(75.0 / 60.0) * rate = Rs " + correctCeilFee);

        // 4. Comparison: 1D Array vs. Jagged Array vs. ArrayList
        System.out.println("\n--- 4. Comparison: 1D Array vs Jagged Array vs ArrayList ---");
        // 1D Array (Flat array requiring index mapping: index = floor * slotsPerFloor + slot)
        String[] flat1D = new String[10];
        flat1D[0] = "V1";

        // ArrayList of Lists (Dynamic, can resize and add floors/slots dynamically)
        List<List<String>> dynamicGrid = new ArrayList<>();
        dynamicGrid.add(new ArrayList<>(Arrays.asList("V1", null, "V2"))); // Floor 0
        dynamicGrid.add(new ArrayList<>(Arrays.asList(null, "V3")));       // Floor 1

        System.out.println("1D Array: Rigid fixed size, requires mathematical index mapping f*N + s.");
        System.out.println("Jagged Array: Fixed floor count, but allows non-uniform floor capacities natively (matrix[f][s]).");
        System.out.println("ArrayList: Dynamic resizing, supports runtime floor and slot additions, stores objects.");
        System.out.println("==================================================");
    }
}
