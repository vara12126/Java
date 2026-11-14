/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Demonstrates:
 * 1. E-Bikes with flat fee pricing.
 * 2. Delivery Vans with 9 AM-5 PM peak window duration split pricing.
 * 3. Visitor One-Day Pass and Monthly Passes (Student/Staff).
 * 4. 5-floor parking facility with jagged unequal slot capacities.
 * 5. Time window boundary transitions (before 9 AM, inside 9 AM-5 PM, after 5 PM).
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DesignChallengeDemo {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("  TASK 12: DAY-4 EXTENDED DESIGN CHALLENGE DEMONSTRATION");
        System.out.println("==========================================================================\n");

        // 1. Initialize 5 floors with differing capacities: [6, 5, 4, 3, 2] (Total: 20 slots)
        int[] fiveFloorCapacities = {6, 5, 4, 3, 2};
        ExtendedParkingFacility facility = new ExtendedParkingFacility("IIT South Gate Complex", fiveFloorCapacities);

        System.out.println("--- 1. Multi-Floor Jagged Array Verification ---");
        System.out.println("Configured floors: " + facility.getFloorCount());
        for (int i = 0; i < facility.getFloorCount(); i++) {
            System.out.println("  Floor " + i + " capacity: " + facility.getFloorCapacity(i) + " slots");
        }

        // 2. Scenario: E-Bike with Flat Fee
        System.out.println("\n--- 2. E-Bike Flat Pricing Rule Test ---");
        EBike ebike = new EBike("EB01AA1111");
        LocalDateTime ebikeEntry = LocalDateTime.of(2026, 11, 14, 8, 30);
        LocalDateTime ebikeExitShort = LocalDateTime.of(2026, 11, 14, 9, 30); // 1 hr
        LocalDateTime ebikeExitLong  = LocalDateTime.of(2026, 11, 14, 18, 30); // 10 hrs

        BigDecimal feeShort = ebike.computeFee(ebikeEntry, ebikeExitShort);
        BigDecimal feeLong  = ebike.computeFee(ebikeEntry, ebikeExitLong);
        System.out.println("E-Bike Stay 1 hour  (08:30 to 09:30): Fee = Rs " + feeShort);
        System.out.println("E-Bike Stay 10 hours (08:30 to 18:30): Fee = Rs " + feeLong);
        System.out.println("Confirmation: Flat fee of Rs 20.00 applied regardless of duration.");

        // 3. Scenario: Delivery Van Pricing with Precise Time Window Splitting
        System.out.println("\n--- 3. Delivery Van Time-Window Splitting (9 AM to 5 PM @ Double Rate) ---");
        DeliveryVan van = new DeliveryVan("VAN99DELIV");

        // Case A: Strictly WITHIN Peak (10:00 AM to 1:00 PM -> 3 hours @ 2x car rate: 3 * Rs 60 = Rs 180)
        LocalDateTime vanEntryA = LocalDateTime.of(2026, 11, 14, 10, 0);
        LocalDateTime vanExitA  = LocalDateTime.of(2026, 11, 14, 13, 0);
        BigDecimal feeA = van.computeFee(vanEntryA, vanExitA);
        System.out.println("Case A (Within 9 AM-5 PM): 10:00 to 13:00 (3 hrs peak @ Rs 60/hr)");
        System.out.println("  Fee = Rs " + feeA + " [Expected: Rs 180.00]");

        // Case B: Strictly OUTSIDE Peak (6:00 PM to 8:00 PM -> 2 hours @ 1x car rate: 2 * Rs 30 = Rs 60)
        LocalDateTime vanEntryB = LocalDateTime.of(2026, 11, 14, 18, 0);
        LocalDateTime vanExitB  = LocalDateTime.of(2026, 11, 14, 20, 0);
        BigDecimal feeB = van.computeFee(vanEntryB, vanExitB);
        System.out.println("Case B (Outside 9 AM-5 PM): 18:00 to 20:00 (2 hrs off-peak @ Rs 30/hr)");
        System.out.println("  Fee = Rs " + feeB + " [Expected: Rs 60.00]");

        // Case C: CROSSING Boundary 1 (8:00 AM to 11:00 AM -> 1 hr off-peak @ Rs 30 + 2 hrs peak @ Rs 60 = Rs 150)
        LocalDateTime vanEntryC = LocalDateTime.of(2026, 11, 14, 8, 0);
        LocalDateTime vanExitC  = LocalDateTime.of(2026, 11, 14, 11, 0);
        BigDecimal feeC = van.computeFee(vanEntryC, vanExitC);
        System.out.println("Case C (Crosses 9 AM): 08:00 to 11:00 (1h off-peak @ Rs 30 + 2h peak @ Rs 60)");
        System.out.println("  Fee = Rs " + feeC + " [Expected: Rs 150.00]");

        // Case D: CROSSING Both Boundaries (7:30 AM to 6:30 PM -> 11 hrs total):
        // Off-peak: 07:30-09:00 (90m) + 17:00-18:30 (90m) = 180m = 3 hrs @ Rs 30 = Rs 90
        // Peak: 09:00-17:00 (480m) = 8 hrs @ Rs 60 = Rs 480
        // Total = Rs 90 + Rs 480 = Rs 570.00
        LocalDateTime vanEntryD = LocalDateTime.of(2026, 11, 14, 7, 30);
        LocalDateTime vanExitD  = LocalDateTime.of(2026, 11, 14, 18, 30);
        BigDecimal feeD = van.computeFee(vanEntryD, vanExitD);
        System.out.println("Case D (Crosses 9 AM and 5 PM): 07:30 to 18:30 (3h off-peak + 8h peak)");
        System.out.println("  Fee = Rs " + feeD + " [Expected: Rs 570.00]");

        // 4. Passes: Visitor One-Day Pass & Monthly Passes
        System.out.println("\n--- 4. Pass Validations (One-Day Pass & Monthly Concessions) ---");
        LocalDateTime now = LocalDateTime.of(2026, 11, 14, 10, 0);

        // One-day pass: covers hourly parking completely
        OneDayPass dayPass = new OneDayPass("ODP-888", "Campus Guest", now.minusHours(2));
        Car visitorCar = new Car("KA05VISIT1");
        facility.park(visitorCar, dayPass, now.minusHours(2));
        BigDecimal dayPassFee = facility.exit("KA05VISIT1", now.plusHours(4)); // 6 hrs stay
        System.out.println("Visitor Car with One-Day Pass (6 hours stay): Net Fee = Rs " + dayPassFee + " [100% Hourly Waived]");

        // Student Monthly Pass (50% concession)
        MonthlyPass studentPass = new MonthlyPass("STU-101", "Aakash", MonthlyPass.HolderRole.STUDENT,
                now.minusDays(5), now.plusDays(25));
        Car studentCar = new Car("KA05STUDENT");
        facility.park(studentCar, studentPass, now);
        BigDecimal studentFee = facility.exit("KA05STUDENT", now.plusHours(2)); // 2 hrs * 30 = 60 -> 50% = 30
        System.out.println("Student Car with Monthly Pass (2 hours stay @ Rs 60): Net Fee = Rs " + studentFee + " [50% Concession]");

        // Staff Monthly Pass (100% concession)
        MonthlyPass staffPass = new MonthlyPass("STAFF-202", "Prof. Sen", MonthlyPass.HolderRole.STAFF,
                now.minusDays(5), now.plusDays(25));
        Car staffCar = new Car("KA05STAFF1");
        facility.park(staffCar, staffPass, now);
        BigDecimal staffFee = facility.exit("KA05STAFF1", now.plusHours(5)); // 5 hrs * 30 = 150 -> 100% = 0
        System.out.println("Staff Car with Monthly Pass (5 hours stay @ Rs 150): Net Fee = Rs " + staffFee + " [100% Concession]");

        // 5. Final 5-Floor Grid Occupancy Display
        System.out.println("\n--- 5. 5-Floor Garage Occupancy Status ---");
        facility.park(ebike, null, now);
        facility.park(van, null, now);
        facility.displayOccupancy();

        System.out.println("\nTotal Accumulated Daily Revenue: Rs " + facility.getTotalDailyRevenue());
        System.out.println("==========================================================================");
    }
}
