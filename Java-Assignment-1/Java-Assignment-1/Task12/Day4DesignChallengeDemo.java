package Task12;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Task 12: Day-4 Design Challenge Implementation
 * Adapted system supporting:
 * - 5 floors with irregular slot counts (20, 30, 40, 25, 15)
 * - E-Bikes with flat-fee pricing (Rs 15 flat)
 * - Delivery Vans with time-of-day surge pricing (double car rate 9 AM - 5 PM)
 * - Visitor One-Day Pass hierarchy
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */

// --- 1. Base Domain Hierarchy ---

abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle registration number cannot be blank");
        }
        this.regNo = regNo;
    }

    public final String getRegNo() { return regNo; }

    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        if (hours == 0) hours = 1;
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + regNo + "]";
    }
}

class Car extends Vehicle {
    public Car(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 10.0; }
}

// --- 2. New Vehicle Categories for Day-4 ---

/**
 * New Category 1: E-Bike
 * Charged a flat fee regardless of duration.
 */
class EBike extends Vehicle {
    public static final double FLAT_FEE = 15.0;

    public EBike(String regNo) { super(regNo); }

    @Override
    protected double ratePerHour() { return 0.0; }

    @Override
    public double calculateFee(int minutes) {
        // E-bikes pay a flat fee regardless of whether they stay 5 minutes or 500 minutes
        return FLAT_FEE;
    }
}

/**
 * New Category 2: Delivery Van
 * Charged at double the car rate (2 x Rs 30 = Rs 60/hr) during 9 AM - 5 PM (minutes 540 to 1020).
 * Charged at standard car rate (Rs 30/hr) outside peak hours.
 */
class DeliveryVan extends Car {
    public DeliveryVan(String regNo) { super(regNo); }

    /**
     * Time-of-day fee calculation:
     * Peak business hours: 9:00 AM (minute 540) to 5:00 PM (minute 1020).
     */
    public double calculateFeeWithTime(int entryMinuteOfDay, int exitMinuteOfDay) {
        int duration = exitMinuteOfDay - entryMinuteOfDay;
        int hours = (duration + 59) / 60;
        if (hours == 0) hours = 1;

        // Check if stay overlaps with 9 AM (540) to 5 PM (1020)
        boolean isPeak = (entryMinuteOfDay < 1020 && exitMinuteOfDay > 540);
        double rate = isPeak ? 60.0 : 30.0; // Double car rate during peak
        return hours * rate;
    }
}

// --- 3. Pass Abstraction Hierarchy ---

interface Pass {
    boolean isExempt();
    String getPassType();
}

class MonthlyPass implements Pass {
    private final String holderId;
    public MonthlyPass(String holderId) { this.holderId = holderId; }
    @Override public boolean isExempt() { return true; }
    @Override public String getPassType() { return "Monthly Pass (" + holderId + ")"; }
}

class OneDayPass implements Pass {
    private final String date;
    public static final double PASS_PRICE = 100.0;

    public OneDayPass(String date) { this.date = date; }
    @Override public boolean isExempt() { return true; }
    @Override public String getPassType() { return "Visitor One-Day Pass (" + date + ")"; }
}

// --- 4. Adapted Smart-Parking System ---

public class Day4DesignChallengeDemo {

    static class AdaptedFacility {
        private final String[][] slots; // 5 floors with irregular slots
        private final Map<String, Pass> activePasses = new HashMap<>();

        public AdaptedFacility(int[] slotsPerFloor) {
            slots = new String[slotsPerFloor.length][];
            for (int f = 0; f < slotsPerFloor.length; f++) {
                slots[f] = new String[slotsPerFloor[f]];
            }
        }

        public int[] allocate(Vehicle vehicle, Pass pass) {
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    if (slots[f][s] == null) {
                        slots[f][s] = vehicle.getRegNo();
                        if (pass != null) activePasses.put(vehicle.getRegNo(), pass);
                        return new int[]{f, s};
                    }
                }
            }
            return null; // Full
        }

        public double release(Vehicle vehicle, int entryMin, int exitMin) {
            Pass pass = activePasses.get(vehicle.getRegNo());
            if (pass != null && pass.isExempt()) {
                clearSlot(vehicle.getRegNo());
                return 0.0; // Pass holder parks free of charge
            }

            double fee;
            if (vehicle instanceof DeliveryVan van) {
                fee = van.calculateFeeWithTime(entryMin, exitMin);
            } else {
                fee = vehicle.calculateFee(exitMin - entryMin);
            }

            clearSlot(vehicle.getRegNo());
            return fee;
        }

        private void clearSlot(String regNo) {
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    if (regNo.equals(slots[f][s])) {
                        slots[f][s] = null;
                        activePasses.remove(regNo);
                        return;
                    }
                }
            }
        }

        public int getTotalCapacity() {
            int sum = 0;
            for (String[] floor : slots) sum += floor.length;
            return sum;
        }

        public int getFloorsCount() { return slots.length; }
        public int getSlotsOnFloor(int floor) { return slots[floor].length; }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 12: DAY-4 DESIGN CHALLENGE - ADAPTED SYSTEM DEMO   ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        // 1. Initializing 5 Floors with irregular capacities
        // Floor 0: 20 slots, Floor 1: 30 slots, Floor 2: 40 slots, Floor 3: 25 slots, Floor 4: 15 slots
        int[] floorConfig = { 20, 30, 40, 25, 15 };
        AdaptedFacility facility = new AdaptedFacility(floorConfig);

        System.out.println("--- 1. Verification of 5-Floor Irregular Array Expansion ---");
        System.out.println("Total Facility Floors: " + facility.getFloorsCount());
        for (int f = 0; f < facility.getFloorsCount(); f++) {
            System.out.println("   Floor " + f + " capacity: " + facility.getSlotsOnFloor(f) + " slots");
        }
        System.out.println("Total System Capacity: " + facility.getTotalCapacity() + " slots across 5 floors.\n");

        // 2. Testing E-Bike Category (Flat Fee)
        System.out.println("--- 2. E-Bike Flat Fee Pricing Test ---");
        EBike ebike1 = new EBike("TN01EB0001");
        EBike ebike2 = new EBike("TN01EB0002");
        facility.allocate(ebike1, null);
        facility.allocate(ebike2, null);

        double ebikeFeeShort = facility.release(ebike1, 600, 615); // 15 mins stay
        double ebikeFeeLong  = facility.release(ebike2, 600, 960); // 360 mins (6 hrs) stay

        System.out.println("E-Bike 1 (15 min stay)  Fee: Rs " + ebikeFeeShort + " (Expected Rs 15.0)");
        System.out.println("E-Bike 2 (360 min stay) Fee: Rs " + ebikeFeeLong + " (Expected Rs 15.0)");
        System.out.println("Flat fee verified: " + (ebikeFeeShort == 15.0 && ebikeFeeLong == 15.0 ? "PASS" : "FAIL"));

        // 3. Testing Delivery Van Category (Time-of-Day Surge Pricing)
        System.out.println("\n--- 3. Delivery Van Time-of-Day Double Rate Test ---");
        DeliveryVan vanPeak = new DeliveryVan("TN02DV1111");
        DeliveryVan vanOffPeak = new DeliveryVan("TN02DV2222");

        facility.allocate(vanPeak, null);
        facility.allocate(vanOffPeak, null);

        // Peak Stay: 10:00 AM (600) to 12:00 PM (720) = 2 hours within 9 AM - 5 PM
        // Double Car Rate = 2 hrs * Rs 60 = Rs 120.0
        double feePeak = facility.release(vanPeak, 600, 720);

        // Off-Peak Stay: 6:00 PM (1080) to 8:00 PM (1200) = 2 hours outside 9 AM - 5 PM
        // Standard Car Rate = 2 hrs * Rs 30 = Rs 60.0
        double feeOffPeak = facility.release(vanOffPeak, 1080, 1200);

        System.out.println("Delivery Van Peak (10 AM - 12 PM, 2 hrs) Fee:     Rs " + feePeak + " (Expected Rs 120.0)");
        System.out.println("Delivery Van Off-Peak (6 PM - 8 PM, 2 hrs) Fee:  Rs " + feeOffPeak + " (Expected Rs 60.0)");
        System.out.println("Surge pricing verified: " + (feePeak == 120.0 && feeOffPeak == 60.0 ? "PASS" : "FAIL"));

        // 4. Testing Visitor One-Day Pass
        System.out.println("\n--- 4. Visitor One-Day Pass Test ---");
        Car visitorCar = new Car("TN09VISITOR");
        OneDayPass dayPass = new OneDayPass("2026-11-17");
        facility.allocate(visitorCar, dayPass);

        // Visitor stays for 8 hours (480 mins)
        double visitorFee = facility.release(visitorCar, 540, 1020);
        System.out.println("Visitor with One-Day Pass (8 hrs stay) Exit Fee: Rs " + visitorFee + " (Expected Rs 0.0)");
        System.out.println("Pass exemption verified: " + (visitorFee == 0.0 ? "PASS" : "FAIL"));

        System.out.println("\nDay-4 Design Challenge requirements successfully satisfied with zero modifications to existing base classes!");
    }
}
