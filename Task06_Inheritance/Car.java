/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 06: Inheritance
 * 
 * Demonstrates Hierarchical inheritance (Vehicle -> Car).
 */
public class Car extends Vehicle {
    protected final int seatingCapacity;

    public Car(String registrationNumber, String brand, int seatingCapacity) {
        super(registrationNumber, brand, "Car");
        System.out.println("  -> [2. Car Constructor] Initializing Car: seats=" + seatingCapacity);
        this.seatingCapacity = seatingCapacity;
    }

    // Protected constructor for subclasses like ElectricCar to specify their type name
    protected Car(String registrationNumber, String brand, String customType, int seatingCapacity) {
        super(registrationNumber, brand, customType);
        System.out.println("  -> [2. Car Constructor (Chained)] Initializing Car base for: " + customType);
        this.seatingCapacity = seatingCapacity;
    }

    @Override
    public double getHourlyRate() {
        return 30.0;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }
}
