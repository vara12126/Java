/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 09: Abstraction
 */
public class TwoWheeler extends Vehicle {

    public TwoWheeler(String registrationNumber) {
        super(registrationNumber, "TwoWheeler");
    }

    @Override
    public double calculateFee(int hours) {
        return hours * 10.0;
    }

    @Override
    public String getDesignatedParkingZone() {
        return "Zone A (Two-Wheeler Shed)";
    }
}
