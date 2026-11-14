/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
public class TwoWheeler extends Vehicle {
    public TwoWheeler(String registrationNumber, String brand) {
        super(registrationNumber, brand, "TwoWheeler");
    }

    @Override
    public double getHourlyRate() {
        return 10.0;
    }
}
