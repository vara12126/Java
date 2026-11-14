/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12 Verification Test: Tests Scenarios 14-19 (Extended Design Challenge)
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Task12_Verification_Test {

    public static class Result {
        public final int id;
        public final String name;
        public final String description;
        public final String expected;
        public final String actual;
        public final boolean passed;

        public Result(int id, String name, String description, String expected, String actual, boolean passed) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.expected = expected;
            this.actual = actual;
            this.passed = passed;
        }
    }

    public static List<Result> runTests() {
        List<Result> list = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.of(2026, 11, 14, 9, 0);

        int[] fiveFloors = {6, 5, 4, 3, 2};
        ExtendedParkingFacility extFacility = new ExtendedParkingFacility("IIT South Gate Complex", fiveFloors);

        // 14. E-bike flat fee
        EBike ebike = new EBike("EBIKE01");
        BigDecimal ebikeFee1 = ebike.computeFee(baseTime, baseTime.plusHours(1));
        BigDecimal ebikeFee8 = ebike.computeFee(baseTime, baseTime.plusHours(8));
        list.add(new Result(14, "E-Bike Flat Fee", "Flat fee for both 1 hr and 8 hrs",
                "Fee = Rs 20.00 for both stays",
                "1h: Rs " + ebikeFee1 + ", 8h: Rs " + ebikeFee8,
                ebikeFee1.compareTo(new BigDecimal("20.00")) == 0 && ebikeFee8.compareTo(new BigDecimal("20.00")) == 0));

        // 15. Delivery van during 9 AM–5 PM
        DeliveryVan van = new DeliveryVan("VANPEAK");
        LocalDateTime t10am = LocalDateTime.of(2026, 11, 14, 10, 0);
        LocalDateTime t1pm  = LocalDateTime.of(2026, 11, 14, 13, 0);
        BigDecimal vanFeePeak = van.computeFee(t10am, t1pm);
        list.add(new Result(15, "Delivery Van (9 AM-5 PM)", "3 hours strictly within peak window @ Rs 60/hr",
                "Net Fee = Rs 180.00",
                "Net Fee = Rs " + vanFeePeak,
                vanFeePeak.compareTo(new BigDecimal("180.00")) == 0));

        // 16. Delivery van outside 9 AM–5 PM
        LocalDateTime t6pm = LocalDateTime.of(2026, 11, 14, 18, 0);
        LocalDateTime t8pm = LocalDateTime.of(2026, 11, 14, 20, 0);
        BigDecimal vanFeeOff = van.computeFee(t6pm, t8pm);
        list.add(new Result(16, "Delivery Van Outside Peak", "2 hours strictly outside peak window @ Rs 30/hr",
                "Net Fee = Rs 60.00",
                "Net Fee = Rs " + vanFeeOff,
                vanFeeOff.compareTo(new BigDecimal("60.00")) == 0));

        // 17. Delivery van crossing time boundaries
        LocalDateTime t8am  = LocalDateTime.of(2026, 11, 14, 8, 0);
        LocalDateTime t11am = LocalDateTime.of(2026, 11, 14, 11, 0);
        BigDecimal vanFeeSplit = van.computeFee(t8am, t11am);
        list.add(new Result(17, "Delivery Van Crossing Boundaries", "08:00 to 11:00 (1h off-peak @ Rs 30 + 2h peak @ Rs 60)",
                "Net Fee = Rs 150.00",
                "Net Fee = Rs " + vanFeeSplit,
                vanFeeSplit.compareTo(new BigDecimal("150.00")) == 0));

        // 18. Visitor one-day pass
        OneDayPass odp = new OneDayPass("ODP-101", "Mr. Visitor", baseTime);
        extFacility.park(new Car("ODP_CAR"), odp, baseTime);
        BigDecimal odpFee = extFacility.exit("ODP_CAR", baseTime.plusHours(8));
        list.add(new Result(18, "Visitor One-Day Pass", "8 hours parking with 24-hr prepaid pass",
                "Net Fee = Rs 0.00 (100% hourly fee waived)",
                "Net Fee = Rs " + odpFee,
                odpFee.compareTo(BigDecimal.ZERO) == 0));

        // 19. Five floors with unequal slot counts
        boolean fiveFloorsValid = extFacility.getFloorCount() == 5 &&
                extFacility.getFloorCapacity(0) == 6 &&
                extFacility.getFloorCapacity(1) == 5 &&
                extFacility.getFloorCapacity(2) == 4 &&
                extFacility.getFloorCapacity(3) == 3 &&
                extFacility.getFloorCapacity(4) == 2;
        list.add(new Result(19, "5 Floors Unequal Capacities", "Capacities: [6, 5, 4, 3, 2] = 20 total bays",
                "5 floors correctly sized and verified",
                "Floors count=" + extFacility.getFloorCount() + ", Capacities: [6,5,4,3,2]",
                fiveFloorsValid));

        return list;
    }

    public static void main(String[] args) {
        System.out.println("Running Task 12 Test Suite (Scenarios 14-19)...");
        List<Result> results = runTests();
        for (Result r : results) {
            System.out.printf("[%s] Test %02d: %-30s | %s%n", r.passed ? "PASS" : "FAIL", r.id, r.name, r.actual);
        }
    }
}
