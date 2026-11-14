package parking.app;

import parking.model.Car;
import parking.model.ElectricCar;
import parking.model.ParkingTicket;
import parking.model.TwoWheeler;
import parking.service.ParkingService;

/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 10: Packages and Encapsulation
 * 
 * Demonstrates:
 * 1. Access modifier boundaries (private, protected, package-private, public).
 * 2. Input validation catching illegal state / parameters.
 * 3. Wrapper classes and autoboxing / unboxing.
 * 4. Why public mutable fields cause security and data corruption vulnerabilities.
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("TASK 10: Packages and Encapsulation");
        System.out.println("==================================================\n");

        ParkingService service = new ParkingService(new int[]{2, 2});

        // 1. Valid Vehicle Registrations & Park Operations
        System.out.println("--- 1. Normal Operations with Encapsulation & Validation ---");
        TwoWheeler bike = new TwoWheeler("TN01A1111");
        Car car = new Car("KA02B2222");
        ElectricCar ev = new ElectricCar("DL03C3333", true);

        ParkingTicket t1 = service.parkVehicle(bike);
        ParkingTicket t2 = service.parkVehicle(car);
        ParkingTicket t3 = service.parkVehicle(ev);

        System.out.println("Parked Bike: " + t1);
        System.out.println("Parked Car:  " + t2);
        System.out.println("Parked EV:   " + t3);

        // 2. Demonstrating Input Validation (Defensive Programming)
        System.out.println("\n--- 2. Input Validation Defenses ---");
        try {
            System.out.println("Attempting to create Vehicle with invalid characters (TN@01#123)...");
            new Car("TN@01#123");
        } catch (IllegalArgumentException e) {
            System.out.println("  -> Successfully intercepted invalid registration: " + e.getMessage());
        }

        try {
            System.out.println("Attempting duplicate entry for already parked vehicle TN01A1111...");
            service.parkVehicle(new TwoWheeler("TN01A1111"));
        } catch (IllegalStateException e) {
            System.out.println("  -> Successfully intercepted duplicate vehicle entry: " + e.getMessage());
        }

        // 3. Exit Processing and Fee Calculation
        System.out.println("\n--- 3. Exit Processing with Autoboxing ---");
        double fee = service.exitVehicle("TN01A1111", 3);
        System.out.printf("Vehicle TN01A1111 exited after 3 hours. Total Fee: Rs %.2f%n", fee);
        System.out.printf("Cumulative Daily Revenue: Rs %.2f%n", service.getDailyTotalRevenue());

        // 4. Encapsulation Security Contrast: Why Public Fields are Dangerous
        System.out.println("\n--- 4. Architectural Analysis: Why Public Fields are Unsafe ---");
        System.out.println("If fields were public (e.g., ticket.billedAmount = 0.0 or slot.occupied = false):");
        System.out.println("  - External unauthorized code could reset bill amounts to zero.");
        System.out.println("  - Slot occupancy could be desynchronized without tracking the parked vehicle.");
        System.out.println("  - Internal structures (like maps) could be cleared from outside.");
        System.out.println("With private fields and unmodifiable collections, state invariants are strictly protected.");
        System.out.println("==================================================");
    }
}
