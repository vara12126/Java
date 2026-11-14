/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 07: Method Overriding
 */
public class TwoWheeler extends Vehicle {

    public TwoWheeler(String registrationNumber) {
        super(registrationNumber, "TwoWheeler");
    }

    @Override
    public double calculateFee(int hours) {
        if (hours <= 0) return 0.0;
        return hours * 10.0; // Two-wheeler rate: Rs 10 per hour
    }
}
