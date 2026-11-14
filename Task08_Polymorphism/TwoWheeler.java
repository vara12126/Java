/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 08: Polymorphism
 */
public class TwoWheeler extends Vehicle {

    public TwoWheeler(String registrationNumber) {
        super(registrationNumber, "TwoWheeler");
    }

    @Override
    public double calculateFee(int hours) {
        return hours * 10.0; // Two-wheeler rate: Rs 10 per hour
    }
}
