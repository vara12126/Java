/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 06: Inheritance
 * 
 * Demonstrates:
 * 1. Constructor execution order in multilevel inheritance.
 * 2. Use of super to invoke parent constructor and parent methods.
 * 3. Inheritance ("is-a") vs Composition ("has-a").
 */
public class InheritanceDemo {

    // Helper class demonstrating Composition ("has-a")
    static class ParkingBay {
        private final int bayNumber;
        private Vehicle parkedVehicle; // ParkingBay HAS-A Vehicle

        public ParkingBay(int bayNumber) {
            this.bayNumber = bayNumber;
        }

        public void park(Vehicle v) {
            this.parkedVehicle = v;
            System.out.printf("[Composition: HAS-A] Bay #%d now hosts vehicle %s (%s)%n",
                    bayNumber, v.getRegistrationNumber(), v.getVehicleType());
        }

        public Vehicle getParkedVehicle() { return parkedVehicle; }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("TASK 06: Inheritance & Constructor Order Demo");
        System.out.println("==================================================\n");

        System.out.println("--- 1. Single & Hierarchical Inheritance: TwoWheeler ---");
        TwoWheeler bike = new TwoWheeler("TN09AA1234", "Honda Activa", true);
        bike.displayInfo();

        System.out.println("\n--- 2. Hierarchical Inheritance: Car ---");
        Car sedan = new Car("KA01BB5678", "Hyundai Verna", 5);
        sedan.displayInfo();

        System.out.println("\n--- 3. Multilevel Inheritance: ElectricCar (Vehicle -> Car -> ElectricCar) ---");
        ElectricCar ev = new ElectricCar("DL05EV9999", "Tata Nexon EV", 5, 40.5, true);
        ev.displayInfo();

        System.out.println("\n--- 4. Composition (HAS-A) vs Inheritance (IS-A) ---");
        System.out.println("IS-A Relationship: ElectricCar IS-A Car, Car IS-A Vehicle.");
        System.out.println("HAS-A Relationship: ParkingBay HAS-A Vehicle.");
        ParkingBay bay = new ParkingBay(101);
        bay.park(ev);

        System.out.println("\nObservation of Constructor Execution Order:");
        System.out.println("When ElectricCar is instantiated:");
        System.out.println("  1. Vehicle constructor executed first.");
        System.out.println("  2. Car constructor executed second.");
        System.out.println("  3. ElectricCar constructor executed third.");
        System.out.println("==================================================");
    }
}
