/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 08: Polymorphism
 */
public class Car extends Vehicle {

    public Car(String registrationNumber) {
        super(registrationNumber, "Car");
    }

    protected Car(String registrationNumber, String vehicleType) {
        super(registrationNumber, vehicleType);
    }

    @Override
    public double calculateFee(int hours) {
        return hours * 30.0; // Car rate: Rs 30 per hour
    }
}
