/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 07: Method Overriding
 */
public class Car extends Vehicle {

    public Car(String registrationNumber) {
        super(registrationNumber, "Car");
    }

    protected Car(String registrationNumber, String customType) {
        super(registrationNumber, customType);
    }

    @Override
    public double calculateFee(int hours) {
        if (hours <= 0) return 0.0;
        return hours * 30.0; // Car rate: Rs 30 per hour
    }
}
