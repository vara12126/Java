/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
public class Car extends Vehicle {
    public Car(String registrationNumber, String brand) {
        super(registrationNumber, brand, "Car");
    }

    protected Car(String registrationNumber, String brand, String customType) {
        super(registrationNumber, brand, customType);
    }

    @Override
    public double getHourlyRate() {
        return 30.0;
    }
}
