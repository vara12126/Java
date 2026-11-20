package Task02;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Task 02 Demonstration:
 * Data representation in Java - Jagged arrays, casting, integer division risks,
 * and input mechanisms.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class DataRepresentationDemo {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 02: DATA REPRESENTATION & DIVISION TRAP DEMO       ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        // 1. Jagged Array Representation for Irregular Floors
        System.out.println("--- 1. Jagged Array for Multi-Level Floors ---");
        int[] slotsPerFloor = { 3, 2, 4 }; // Ground floor: 3, 1st floor: 2, 2nd floor: 4
        String[][] facility = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            facility[f] = new String[slotsPerFloor[f]];
        }
        
        // Populate sample slots
        facility[0][0] = "TN01AA1001";
        facility[1][1] = "TN02BB2002";
        facility[2][3] = "TN03CC3003";

        for (int f = 0; f < facility.length; f++) {
            System.out.println("Floor " + f + " (capacity " + facility[f].length + "): " 
                + Arrays.toString(facility[f]));
        }

        // 2. Demonstrating Alternative: ArrayList<String[]>
        System.out.println("\n--- 2. ArrayList<String[]> Representation ---");
        ArrayList<String[]> listFacility = new ArrayList<>();
        for (int slotsCount : slotsPerFloor) {
            listFacility.add(new String[slotsCount]);
        }
        listFacility.get(0)[0] = "TN01AA1001";
        System.out.println("Floor 0 from ArrayList: " + Arrays.toString(listFacility.get(0)));

        // 3. Demonstrating Integer Division Risk vs Safe Ceiling
        System.out.println("\n--- 3. Integer Division Risk vs Safe Ceiling Arithmetic ---");
        int[] testDurations = { 1, 30, 60, 61, 65, 120, 130 };
        double carRatePerHour = 30.0;

        System.out.printf("%-10s | %-12s | %-12s | %-12s%n", 
            "Minutes", "Truncated Fee", "Safe Int Fee", "Math.ceil Fee");
        System.out.println("------------------------------------------------------------");

        for (int minutes : testDurations) {
            // Integer division (flawed)
            int flawedHours = minutes / 60;
            double flawedFee = flawedHours * carRatePerHour;

            // Safe integer ceiling arithmetic: (minutes + 59) / 60
            int safeIntHours = (minutes + 59) / 60;
            double safeIntFee = safeIntHours * carRatePerHour;

            // Floating point ceiling
            int mathCeilHours = (int) Math.ceil((double) minutes / 60.0);
            double mathCeilFee = mathCeilHours * carRatePerHour;

            System.out.printf("%-10d | Rs %-9.1f | Rs %-9.1f | Rs %-9.1f%n", 
                minutes, flawedFee, safeIntFee, mathCeilFee);
        }

        System.out.println("\nConclusion: Integer division truncates 61 and 65 minutes to 1 hour,");
        System.out.println("losing Rs 30.0 per vehicle. The safe formula (minutes + 59) / 60 prevents this.");
    }
}
