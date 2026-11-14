/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 06: Inheritance
 * 
 * Demonstrates Single / Hierarchical inheritance from Vehicle.
 */
public class TwoWheeler extends Vehicle {
    private final boolean hasHelmetStorage;

    public TwoWheeler(String registrationNumber, String brand, boolean hasHelmetStorage) {
        super(registrationNumber, brand, "TwoWheeler");
        System.out.println("  -> [2. TwoWheeler Constructor] Initializing TwoWheeler: helmetStorage=" + hasHelmetStorage);
        this.hasHelmetStorage = hasHelmetStorage;
    }

    @Override
    public double getHourlyRate() {
        return 10.0;
    }

    public boolean isHasHelmetStorage() {
        return hasHelmetStorage;
    }
}
