/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 07: Method Overriding
 * 
 * Demonstrates:
 * 1. Dynamic method dispatch with overridden calculateFee().
 * 2. Overriding equals(), hashCode(), and toString().
 * 3. Identity vs Value equality (HashSet membership based on registration number).
 * 4. Final keyword usage (variables, methods, classes).
 * 5. Architectural comparison: Method Overriding vs Switch-Based calculation.
 */
import java.util.HashSet;
import java.util.Set;

public class OverridingDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("TASK 07: Method Overriding & Object Contracts");
        System.out.println("==================================================\n");

        // 1. Polymorphic Method Dispatch via Overridden calculateFee()
        System.out.println("--- 1. Polymorphic Fee Calculation (Overridden Methods) ---");
        Vehicle[] vehicles = new Vehicle[]{
            new TwoWheeler("TN01A1111"),
            new Car("TN02B2222"),
            new ElectricCar("TN03C3333", true)
        };

        int durationHours = 4;
        for (Vehicle v : vehicles) {
            System.out.printf("%s -> Fee for %d hours: Rs %.1f%n",
                    v.toString(), durationHours, v.calculateFee(durationHours));
        }

        // 2. equals() and hashCode() Contract Based on Registration Number
        System.out.println("\n--- 2. Object Equality Contract (equals & hashCode) ---");
        Vehicle car1 = new Car("KA01XY9999");
        Vehicle car2 = new Car("KA01XY9999"); // Same reg number, different heap instance
        Vehicle car3 = new Car("MH02AB1234"); // Different reg number

        System.out.println("car1 (Heap @" + Integer.toHexString(System.identityHashCode(car1)) + "): " + car1);
        System.out.println("car2 (Heap @" + Integer.toHexString(System.identityHashCode(car2)) + "): " + car2);
        System.out.println("Reference equality (car1 == car2): " + (car1 == car2));
        System.out.println("Logical equality    (car1.equals(car2)): " + car1.equals(car2));
        System.out.println("Logical equality    (car1.equals(car3)): " + car1.equals(car3));
        System.out.println("car1.hashCode() == car2.hashCode(): " + (car1.hashCode() == car2.hashCode()));

        // Verification with HashSet (Preventing duplicate parked vehicles)
        Set<Vehicle> activeParkedSet = new HashSet<>();
        activeParkedSet.add(car1);
        boolean addedDuplicate = activeParkedSet.add(car2); // Should be rejected because equals/hashCode match

        System.out.println("\nHashSet Duplicate Prevention:");
        System.out.println("Added car1: true");
        System.out.println("Attempted to add car2 (duplicate registration): " + addedDuplicate);
        System.out.println("Total unique parked vehicles in Set: " + activeParkedSet.size());

        // 3. Final Keyword Demonstration
        System.out.println("\n--- 3. Final Keyword Demonstration ---");
        System.out.println("  - final variable: registrationNumber cannot be reassigned.");
        System.out.println("  - final method:   getRegistrationNumber() cannot be overridden.");
        System.out.println("  - final class:    ElectricCar cannot be extended.");
        System.out.println("==================================================");
    }
}
