/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11 Verification Test: Tests Scenarios 1-13 (Standard Smart-Parking System)
 */
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Task11_Verification_Test {

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
        ParkingFacility facility = new ParkingFacility("Campus Central Garage", new int[]{2, 2, 1}, new int[]{1, 0, 0});
        LocalDateTime baseTime = LocalDateTime.of(2026, 11, 14, 9, 0);

        // 1. Normal vehicle entry
        Car normalCar = new Car("KA01AA1111", "Hyundai");
        ParkingTicket t1 = facility.parkVehicle(normalCar, PassType.VISITOR, false, baseTime);
        list.add(new Result(1, "Normal Vehicle Entry", "Park standard car in available slot",
                "Allocated Bay Floor 0, Slot 0",
                "Allocated Bay Floor " + t1.getFloor() + ", Slot " + t1.getSlot() + " (Ticket: " + t1.getTicketId() + ")",
                t1 != null && t1.getFloor() == 0 && t1.getSlot() == 0));

        // 2. Normal vehicle exit
        ParkingTicket exitT1 = facility.exitVehicle("KA01AA1111", baseTime.plusHours(2));
        list.add(new Result(2, "Normal Vehicle Exit", "Exit car after 2 hours (2 * Rs 30)",
                "Active=false, Net Fee=Rs 60.00",
                "Active=" + exitT1.isActive() + ", Net Fee=Rs " + exitT1.getNetFeePaid(),
                !exitT1.isActive() && exitT1.getNetFeePaid().compareTo(new BigDecimal("60.00")) == 0));

        // 3. Last available parking slot
        List<ParkingTicket> fill4 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            fill4.add(facility.parkVehicle(new Car("FILL" + i, "Brand"), PassType.VISITOR, false, baseTime));
        }
        ParkingTicket lastTicket = facility.parkVehicle(new TwoWheeler("LAST01", "Honda"), PassType.VISITOR, false, baseTime);
        list.add(new Result(3, "Last Available Slot", "Park vehicle when only 1 slot remains",
                "Assigned to last remaining bay (Floor 2, Slot 0)",
                "Assigned to Floor " + lastTicket.getFloor() + ", Slot " + lastTicket.getSlot(),
                lastTicket != null && lastTicket.getFloor() == 2 && lastTicket.getSlot() == 0));

        // 4. Parking facility full
        ParkingTicket overflowTicket = facility.parkVehicle(new Car("OVERFLOW", "Ford"), PassType.VISITOR, false, baseTime);
        list.add(new Result(4, "Parking Facility Full", "Attempt entry when all 5 slots occupied",
                "Returns null (Parking Full Alert)",
                overflowTicket == null ? "Returns null (Facility Full Alert)" : "Allocated",
                overflowTicket == null));

        for (ParkingTicket pt : fill4) {
            facility.exitVehicle(pt.getTicketId(), baseTime.plusHours(1));
        }
        facility.exitVehicle(lastTicket.getTicketId(), baseTime.plusHours(1));

        // 5. Zero-minute parking
        Car zeroMinCar = new Car("KA02ZERO", "Maruti");
        ParkingTicket tZero = facility.parkVehicle(zeroMinCar, PassType.VISITOR, false, baseTime);
        ParkingTicket exitZero = facility.exitVehicle("KA02ZERO", baseTime);
        list.add(new Result(5, "Zero-Minute Parking", "Vehicle exits immediately at entry timestamp",
                "Duration=0 mins, Fee=Rs 0.00",
                "Duration=" + exitZero.getDurationMinutes() + " mins, Fee=Rs " + exitZero.getNetFeePaid(),
                exitZero.getDurationMinutes() == 0 && exitZero.getNetFeePaid().compareTo(BigDecimal.ZERO) == 0));

        // 6. Fee-slab boundary (61 minutes)
        Car slabCar = new Car("KA03SLAB", "Honda");
        ParkingTicket tSlab = facility.parkVehicle(slabCar, PassType.VISITOR, false, baseTime);
        ParkingTicket exitSlab = facility.exitVehicle("KA03SLAB", baseTime.plusMinutes(61));
        list.add(new Result(6, "Fee-Slab Boundary", "61 minutes duration (crosses 1-hr boundary into 2nd hour)",
                "Ceiling 2 hours @ Rs 30 = Rs 60.00",
                "Duration=" + exitSlab.getDurationMinutes() + " mins, Fee=Rs " + exitSlab.getNetFeePaid(),
                exitSlab.getNetFeePaid().compareTo(new BigDecimal("60.00")) == 0));

        // 7. Invalid registration
        boolean invalidRegCaught = false;
        try {
            new Car("BAD_REG!#", "Test");
        } catch (IllegalArgumentException e) {
            invalidRegCaught = true;
        }
        list.add(new Result(7, "Invalid Registration", "Alphanumeric validation rejecting special characters",
                "IllegalArgumentException intercepted",
                "Caught exception = " + invalidRegCaught,
                invalidRegCaught));

        // 8. Duplicate registration
        facility.parkVehicle(new Car("DUPREG01", "Hyundai"), PassType.VISITOR, false, baseTime);
        boolean duplicateCaught = false;
        try {
            facility.parkVehicle(new Car("DUPREG01", "Hyundai"), PassType.VISITOR, false, baseTime);
        } catch (IllegalStateException e) {
            duplicateCaught = true;
        }
        facility.exitVehicle("DUPREG01", baseTime.plusHours(1));
        list.add(new Result(8, "Duplicate Registration", "Rejecting vehicle with active session",
                "IllegalStateException intercepted",
                "Caught duplicate = " + duplicateCaught,
                duplicateCaught));

        // 9. Invalid slot / ID lookup
        boolean invalidSlotHandled = false;
        try {
            facility.exitVehicle("NON_EXISTENT_ID", baseTime);
        } catch (NoSuchElementException e) {
            invalidSlotHandled = true;
        }
        list.add(new Result(9, "Invalid Slot / ID", "Attempt exit on non-existent registration or slot",
                "NoSuchElementException intercepted",
                "Caught invalid lookup = " + invalidSlotHandled,
                invalidSlotHandled));

        // 10. EV charging fee
        ElectricCar evCar = new ElectricCar("EV01CHARG", "Tata", true);
        facility.parkVehicle(evCar, PassType.VISITOR, true, baseTime);
        ParkingTicket exitEv = facility.exitVehicle("EV01CHARG", baseTime.plusHours(3));
        list.add(new Result(10, "EV Charging Fee", "EV car 3 hours: Rs 90 base + Rs 75 charging",
                "Net Fee = Rs 165.00",
                "Net Fee = Rs " + exitEv.getNetFeePaid() + " (Charging Fee: Rs " + exitEv.getEvChargingFee() + ")",
                exitEv.getNetFeePaid().compareTo(new BigDecimal("165.00")) == 0));

        // 11. Student monthly pass
        Car studCar = new Car("STU01PASS", "Maruti");
        facility.parkVehicle(studCar, PassType.STUDENT_MONTHLY, false, baseTime);
        ParkingTicket exitStud = facility.exitVehicle("STU01PASS", baseTime.plusHours(4));
        list.add(new Result(11, "Student Monthly Pass", "4 hours stay (Rs 120) with Student Pass (50% off)",
                "Net Fee = Rs 60.00",
                "Net Fee = Rs " + exitStud.getNetFeePaid() + " (Discount: Rs " + exitStud.getDiscountAmount() + ")",
                exitStud.getNetFeePaid().compareTo(new BigDecimal("60.00")) == 0));

        // 12. Staff monthly pass
        Car staffCar = new Car("STAFF01PS", "Honda");
        facility.parkVehicle(staffCar, PassType.STAFF_MONTHLY, false, baseTime);
        ParkingTicket exitStaff = facility.exitVehicle("STAFF01PS", baseTime.plusHours(6));
        list.add(new Result(12, "Staff Monthly Pass", "6 hours stay (Rs 180) with Staff Pass (100% off)",
                "Net Fee = Rs 0.00",
                "Net Fee = Rs " + exitStaff.getNetFeePaid() + " (Discount: Rs " + exitStaff.getDiscountAmount() + ")",
                exitStaff.getNetFeePaid().compareTo(BigDecimal.ZERO) == 0));

        // 13. Visitor parking
        Car visitCar = new Car("VISIT01", "Kia");
        facility.parkVehicle(visitCar, PassType.VISITOR, false, baseTime);
        ParkingTicket exitVisit = facility.exitVehicle("VISIT01", baseTime.plusHours(5));
        list.add(new Result(13, "Visitor Parking", "5 hours stay with standard visitor tariff",
                "Net Fee = Rs 150.00",
                "Net Fee = Rs " + exitVisit.getNetFeePaid(),
                exitVisit.getNetFeePaid().compareTo(new BigDecimal("150.00")) == 0));

        return list;
    }

    public static void main(String[] args) {
        System.out.println("Running Task 11 Test Suite (Scenarios 1-13)...");
        List<Result> results = runTests();
        for (Result r : results) {
            System.out.printf("[%s] Test %02d: %-30s | %s%n", r.passed ? "PASS" : "FAIL", r.id, r.name, r.actual);
        }
    }
}
