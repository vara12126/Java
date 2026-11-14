/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 08: Polymorphism
 * 
 * Supplied assignment verification program demonstrating:
 * 1. Heterogeneous Vehicle[] array holding diverse vehicle objects.
 * 2. Runtime Polymorphism via Dynamic Method Dispatch on calculateFee(hours).
 * 3. Dynamic type inspection verifying Chargeable interface implementation.
 * 
 * Expected Output:
 * TwoWheeler TN01A1 -> Rs 30.0
 * Car TN02B2 -> Rs 90.0
 * ElectricCar TN03C3 -> Rs 210.0
 * true
 */
public class FeeDemo {

    public static void main(String[] args) {
        // Heterogeneous array of Vehicle references storing different subclass instances
        Vehicle[] vehicles = new Vehicle[] {
            new TwoWheeler("TN01A1"),
            new Car("TN02B2"),
            new ElectricCar("TN03C3")
        };

        int hours = 3;
        for (Vehicle v : vehicles) {
            // Dynamic method dispatch resolves calculateFee() at runtime according to actual object type
            System.out.println(v.getVehicleType() + " " + v.getRegistrationNumber() + " -> Rs " + v.calculateFee(hours));
        }

        // Verifying Chargeable interface contract
        System.out.println(vehicles[2] instanceof Chargeable);
    }
}
