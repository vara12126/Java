/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 09: Abstraction
 * 
 * Demonstrates:
 * 1. Abstract classes vs Interfaces.
 * 2. Static and default methods in interfaces.
 * 3. Multiple interface implementation (Chargeable & Trackable).
 * 4. Abstract method enforcement across subclasses.
 */
public class AbstractionDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("TASK 09: Abstraction (Abstract Classes & Interfaces)");
        System.out.println("==================================================\n");

        // 1. Invoking Static Method on Interface
        Chargeable.printRateCardHeader();
        System.out.println("Standard Currency: " + Chargeable.getCurrencySymbol() + "\n");

        // 2. Polymorphic instantiations of Abstract Class Vehicle
        Vehicle[] fleet = new Vehicle[] {
            new TwoWheeler("TN07AA7777"),
            new Car("KA04BB8888"),
            new ElectricCar("DL01EV9999", true)
        };

        // 3. Testing Abstract methods, Default methods, and Multiple Interface methods
        int parkingHours = 5;
        double concessionRate = 20.0; // 20% discount for registered campus staff

        for (Vehicle v : fleet) {
            System.out.println("--- Vehicle Profile ---");
            System.out.println("Entity: " + v);
            System.out.println("Designated Zone: " + v.getDesignatedParkingZone());

            // Trackable interface default method
            v.logMovement("Boom Barrier Entry Gate 1");

            // Chargeable interface abstract method
            double fullFee = v.calculateFee(parkingHours);
            // Chargeable interface default method
            double discountedFee = v.calculateDiscountedFee(parkingHours, concessionRate);

            System.out.printf("Fee for %d hrs: Rs %.2f | With 20%% Discount: Rs %.2f%n%n",
                    parkingHours, fullFee, discountedFee);
        }

        System.out.println("==================================================");
    }
}
