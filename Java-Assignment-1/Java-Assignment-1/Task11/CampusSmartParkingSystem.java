package Task11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task 11: Complete Campus Smart-Parking System Implementation and Defense
 * Full object-oriented system with comprehensive test runner and test table.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */

// 1. Capability Interface
interface Chargeable {
    double chargingFee(int kWh);
}

// 2. Abstract Vehicle Base Class
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
        if (minutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative: " + minutes);
        }
        // Ceiling rule using integer arithmetic
        int hours = (minutes + 59) / 60;
        if (hours == 0) hours = 1; // Minimum billable unit is 1 hour
        return hours * ratePerHour();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + regNo + "]";
    }
}

// 3. Concrete Vehicle Subclasses
class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 10.0; }
}

class Car extends Vehicle {
    public Car(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
}

class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String regNo) { super(regNo); }
    @Override
    public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("kWh cannot be negative");
        return kWh * 12.0;
    }
}

// 4. Transactional Parking Ticket
class ParkingTicket {
    private static int counter = 1000;
    private final int ticketId;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final int entryMinutes;
    private final boolean isPassHolder;
    private boolean isPaid;

    public ParkingTicket(Vehicle vehicle, int floor, int slot, int entryMinutes, boolean isPassHolder) {
        this.ticketId = ++counter;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryMinutes = entryMinutes;
        this.isPassHolder = isPassHolder;
        this.isPaid = false;
    }

    public int getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public int getEntryMinutes() { return entryMinutes; }
    public boolean isPassHolder() { return isPassHolder; }
    public boolean isPaid() { return isPaid; }
    public void markPaid() { this.isPaid = true; }

    @Override
    public String toString() {
        return "Ticket#" + ticketId + " [" + vehicle.getRegNo() + " at Floor " + floor + ", Slot " + slot + "]";
    }
}

// 5. Parking Facility Coordinator
public class CampusSmartParkingSystem {
    private final String[][] slots; // Multi-level slot matrix
    private final Map<String, ParkingTicket> activeTickets = new HashMap<>();
    private double totalRevenueCollected = 0.0;

    public CampusSmartParkingSystem(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    /**
     * Allocates a parking slot and issues a ticket.
     * Prevents duplicate registrations and checks facility capacity.
     */
    public ParkingTicket allocate(Vehicle vehicle, int entryMinutes, boolean isPassHolder) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null");
        
        // Prevent duplicate vehicle entry
        if (activeTickets.containsKey(vehicle.getRegNo())) {
            throw new IllegalStateException("Duplicate entry rejected: Vehicle " + vehicle.getRegNo() + " is already parked.");
        }

        // First-fit search across floors
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicle.getRegNo();
                    ParkingTicket ticket = new ParkingTicket(vehicle, f, s, entryMinutes, isPassHolder);
                    activeTickets.put(vehicle.getRegNo(), ticket);
                    return ticket;
                }
            }
        }
        return null; // Facility is full
    }

    /**
     * Releases a parking slot and computes final billable fee.
     */
    public double release(String regNo, int exitMinutes, int kWh) {
        ParkingTicket ticket = activeTickets.get(regNo);
        if (ticket == null) {
            throw new IllegalArgumentException("Exit failed: No active ticket found for vehicle " + regNo);
        }
        if (exitMinutes < ticket.getEntryMinutes()) {
            throw new IllegalArgumentException("Exit time (" + exitMinutes + ") cannot be earlier than entry time (" 
                + ticket.getEntryMinutes() + ")");
        }

        int duration = exitMinutes - ticket.getEntryMinutes();
        Vehicle vehicle = ticket.getVehicle();
        double baseFee = 0.0;

        // Apply pass waiver: pass holders get free parking
        if (!ticket.isPassHolder()) {
            baseFee = vehicle.calculateFee(duration); // Dynamic method dispatch
        }

        // Apply EV charging tariff if applicable
        double chargingFee = 0.0;
        if (vehicle instanceof Chargeable c && kWh > 0) {
            chargingFee = c.chargingFee(kWh);
        }

        double totalFee = baseFee + chargingFee;
        totalRevenueCollected += totalFee;

        // Clear slot and remove active ticket
        slots[ticket.getFloor()][ticket.getSlot()] = null;
        activeTickets.remove(regNo);
        ticket.markPaid();

        return totalFee;
    }

    public int getFreeSlotCount() {
        int count = 0;
        for (String[] floor : slots) {
            for (String slot : floor) {
                if (slot == null) count++;
            }
        }
        return count;
    }

    public double getTotalRevenueCollected() {
        return totalRevenueCollected;
    }

    // --- Comprehensive Test Runner ---
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 11: CAMPUS SMART-PARKING SYSTEM COMPREHENSIVE SUITE ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        // Create facility with 2 floors: Floor 0 has 2 slots, Floor 1 has 3 slots (Total 5 slots)
        CampusSmartParkingSystem parking = new CampusSmartParkingSystem(new int[]{2, 3});

        System.out.printf("%-8s | %-12s | %-42s | %-12s | %-6s%n", 
            "TestID", "Category", "Description", "Result", "Status");
        System.out.println("-----------------------------------------------------------------------------------------");

        // TC-01: TwoWheeler normal entry & exit (45 mins)
        ParkingTicket t1 = parking.allocate(new TwoWheeler("TN01A1001"), 100, false);
        double fee1 = parking.release("TN01A1001", 145, 0); // 45 min -> 1 hr @ Rs 10 = Rs 10.0
        printTestResult("TC-01", "Normal", "TwoWheeler 45m (Expected Rs 10.0)", "Rs " + fee1, fee1 == 10.0);

        // TC-02: Car normal entry & exit (130 mins)
        ParkingTicket t2 = parking.allocate(new Car("TN02B2002"), 200, false);
        double fee2 = parking.release("TN02B2002", 330, 0); // 130 min -> 3 hrs @ Rs 30 = Rs 90.0
        printTestResult("TC-02", "Normal", "Car 130m (Expected Rs 90.0)", "Rs " + fee2, fee2 == 90.0);

        // TC-03: ElectricCar normal entry & exit with charging (60 mins + 10 kWh)
        ParkingTicket t3 = parking.allocate(new ElectricCar("TN03C3003"), 300, false);
        double fee3 = parking.release("TN03C3003", 360, 10); // 1 hr (Rs 30) + 10*12 (Rs 120) = Rs 150.0
        printTestResult("TC-03", "Normal", "ElectricCar 60m + 10 kWh (Exp Rs 150.0)", "Rs " + fee3, fee3 == 150.0);

        // TC-04: Student Monthly Pass Holder Car (180 mins)
        ParkingTicket t4 = parking.allocate(new Car("TN04D4004"), 400, true);
        double fee4 = parking.release("TN04D4004", 580, 0); // Pass exemption -> Rs 0.0
        printTestResult("TC-04", "Normal", "Student Pass Holder Car (Expected Rs 0.0)", "Rs " + fee4, fee4 == 0.0);

        // TC-05: Staff Monthly Pass Holder ElectricCar + 15 kWh
        ParkingTicket t5 = parking.allocate(new ElectricCar("TN05E5005"), 500, true);
        double fee5 = parking.release("TN05E5005", 620, 15); // Pass (Rs 0) + 15*12 (Rs 180) = Rs 180.0
        printTestResult("TC-05", "Normal", "Staff Pass EV + 15 kWh (Exp Rs 180.0)", "Rs " + fee5, fee5 == 180.0);

        // TC-06: Fill to Last Free Slot
        ParkingTicket f0 = parking.allocate(new Car("TN10AA01"), 600, false);
        ParkingTicket f1 = parking.allocate(new Car("TN10AA02"), 600, false);
        ParkingTicket f2 = parking.allocate(new Car("TN10AA03"), 600, false);
        ParkingTicket f3 = parking.allocate(new Car("TN10AA04"), 600, false);
        ParkingTicket f4 = parking.allocate(new Car("TN10AA05"), 600, false); // 5th slot (lot now full)
        boolean isFullNow = (parking.getFreeSlotCount() == 0);
        printTestResult("TC-06", "Boundary", "Last Slot Fill (Capacity becomes 0)", "Free: " + parking.getFreeSlotCount(), isFullNow);

        // TC-07: Facility Full Rejection
        ParkingTicket rejected = parking.allocate(new Car("TN99OVERFLOW"), 605, false);
        printTestResult("TC-07", "Boundary", "Full Lot Rejection (Expected null)", String.valueOf(rejected), rejected == null);

        // Clear one slot for further tests
        parking.release("TN10AA05", 660, 0);

        // TC-08: Zero-Minute Parking Stay (Entry 700, Exit 700)
        ParkingTicket tZero = parking.allocate(new Car("TN00ZERO"), 700, false);
        double feeZero = parking.release("TN00ZERO", 700, 0); // Ceiling: 1 billable hr @ Rs 30 = Rs 30.0
        printTestResult("TC-08", "Boundary", "Zero-Minute Stay (Expected Rs 30.0)", "Rs " + feeZero, feeZero == 30.0);

        // TC-09: Exact Slab Boundary: 60m vs 61m
        ParkingTicket t60 = parking.allocate(new Car("TN60M"), 800, false);
        double fee60 = parking.release("TN60M", 860, 0); // 60 mins -> 1 hr = Rs 30.0
        ParkingTicket t61 = parking.allocate(new Car("TN61M"), 800, false);
        double fee61 = parking.release("TN61M", 861, 0); // 61 mins -> 2 hrs = Rs 60.0
        printTestResult("TC-09", "Boundary", "Exact Slab (60m=Rs 30, 61m=Rs 60)", "Rs " + fee60 + " / " + fee61, (fee60 == 30.0 && fee61 == 60.0));

        // TC-10: Duplicate Registration Prevention
        boolean duplicateCaught = false;
        try {
            parking.allocate(new Car("TN10AA01"), 900, false); // TN10AA01 is still parked!
        } catch (IllegalStateException ex) {
            duplicateCaught = true;
        }
        printTestResult("TC-10", "Invalid", "Duplicate RegNo Entry Rejected", duplicateCaught ? "Rejected" : "Allowed", duplicateCaught);

        // TC-11: Invalid Exit / Unknown Vehicle
        boolean unknownCaught = false;
        try {
            parking.release("UNKNOWN_PLATE", 900, 0);
        } catch (IllegalArgumentException ex) {
            unknownCaught = true;
        }
        printTestResult("TC-11", "Invalid", "Unknown Vehicle Exit Rejected", unknownCaught ? "Rejected" : "Allowed", unknownCaught);

        // TC-12: Negative Duration Rejection
        boolean negativeCaught = false;
        try {
            ParkingTicket tNeg = parking.allocate(new Car("TN_NEG"), 950, false);
            parking.release("TN_NEG", 900, 0); // Exit before entry!
        } catch (IllegalArgumentException ex) {
            negativeCaught = true;
        }
        printTestResult("TC-12", "Invalid", "Negative Duration Rejected", negativeCaught ? "Rejected" : "Allowed", negativeCaught);

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("\n--- HAND-CALCULATED SCENARIO FINANCIAL VERIFICATION ---");
        System.out.println("Vehicles in Scenario: TC-01 (Rs 10) + TC-02 (Rs 90) + TC-03 (Rs 150) + TC-04 (Rs 0) + TC-05 (Rs 180)");
        System.out.println("Expected Hand-Calculated Revenue: Rs 430.00");
        double scenarioRevenue = fee1 + fee2 + fee3 + fee4 + fee5;
        System.out.printf("Actual Scenario Collection:       Rs %.2f%n", scenarioRevenue);
        System.out.println("Financial Audit Match:            " + (scenarioRevenue == 430.00 ? "VERIFIED (100% ACCURACY)" : "MISMATCH"));
    }

    private static void printTestResult(String id, String category, String desc, String result, boolean pass) {
        System.out.printf("%-8s | %-12s | %-42s | %-12s | %-6s%n", 
            id, category, desc, result, pass ? "PASS" : "FAIL");
    }
}
